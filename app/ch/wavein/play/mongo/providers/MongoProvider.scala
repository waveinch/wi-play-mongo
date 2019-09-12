package ch.wavein.play.mongo.providers

import ch.wavein.play.mongo.model.Identity
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.Cursor

/**
  * Created by mattia on 28/06/16.
  */
trait MongoProvider[T <: Identity] extends Provider[T] {

  def collectionName:String
  def reactiveMongoApi:ReactiveMongoApi

  def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection](collectionName))

  implicit def formatter: OFormat[T]

  implicit def ec: ExecutionContext

  protected def createUniqueId(obj: T): String = BSONObjectID.generate.stringify

  override def insert(obj: T, autoGenerateId:Boolean = true): Future[T] = {


    val id =  autoGenerateId match {
      case true => createUniqueId(obj)
      case false => obj.identity
    }

    val jsonToInsert = autoGenerateId match {
      case true => {
        Json.toJson(obj).as[JsObject] ++ Json.obj("_id" -> id)
      }
      case false => Json.toJson(obj).as[JsObject]
    }

    for {
      coll <- collection
      _ <- coll.insert(ordered = false).one(jsonToInsert)
      obj <- get(id)
    } yield obj
  }

  override def get(id: String): Future[T] =
    find(id).map(_.getOrElse(throw new ObjectNotExistsException(id)))

  override def find(id: String): Future[Option[T]] = for {
    coll <- collection
    obj <- coll.find(Json.obj("_id" -> id)).one[T]
  } yield obj


  override def find(id: Option[String]): Future[Option[T]] = id match {
    case None => Future.successful(None)
    case Some(id) => find(id)
  }

  override def findMany(ids: Seq[String], ordered: Boolean): Future[Seq[T]] = for {
    coll <- collection
    unorderedList <- coll.find(
      Json.obj("_id" -> Json.obj("$in" -> ids))
    ).cursor[T]().collect[Seq](Int.MaxValue, Cursor.FailOnError[Seq[T]]())
  } yield ordered match {
    case true =>
      val hashMap: Map[String, T] = unorderedList.map(o => o.identity -> o).toMap
      ids.flatMap(hashMap.get)
    case false => unorderedList
  }

  override def update(obj: T): Future[T] = for {
    coll <- collection
    _ <- checkExistence(obj.identity)
    _ <- coll.update(ordered = false).one(Json.obj("_id" -> obj._id), obj)
    updatedObj <- get(obj.identity)
  } yield updatedObj

  override def softUpdate(obj: T): Future[T] = for {
    coll <- collection
    _ <- checkExistence(obj.identity)
    _ <- coll.update(ordered = false).one(
      Json.obj("_id" -> obj._id),
      Json.obj("$set" -> obj))
    updatedObj <- get(obj.identity)
  } yield updatedObj

  override def delete(id: String): Future[T] = for {
    coll <- collection
    obj <- get(id)
    _ <- coll.delete().one(Json.obj("_id" -> id))
  } yield obj

  override def exists(id: String): Future[Boolean] = for {
    coll <- collection
    obj <- coll.find(
      Json.obj("_id" -> id),
      Json.obj("_id" -> 1)
    ).one[JsValue].map(_.isDefined)
  } yield obj

  override def list(): Future[Seq[T]] =
    list(Json.obj())


  override def list(limit: Int, order:String): Future[scala.Seq[T]] = {
    val sortField = order.substring(1)
    val sortOrder = order.charAt(0) match {
      case '+' => 1
      case '-' => -1
    }
    val mongoOrder = Json.obj(sortField -> sortOrder)

    for {
      coll <- collection
      allObjects <- coll.find(Json.obj()).sort(mongoOrder).cursor[T]().collect[Seq](limit,Cursor.FailOnError[Seq[T]]())
    } yield allObjects
  }

  def listIds(): Future[Seq[String]] = for {
    coll <- collection
    ids <- coll.find(Json.obj(), Json.obj("_id" -> true))
      .cursor[JsObject]().collect[Seq](Int.MaxValue,Cursor.FailOnError[Seq[JsObject]]())
      .map(_.flatMap({ j => (j \ "_id").validate[String].asOpt }))
  } yield ids

  def rawUpdate(id: String, jsUpdate: JsObject): Future[T] = for {
    coll <- collection
    _ <- checkExistence(id)
    _ <- coll.update(ordered = false).one(Json.obj("_id" -> id),jsUpdate)
    updatedObj <- get(id)
  } yield updatedObj

  protected def list(query: JsObject): Future[Seq[T]] = for {
    coll <- collection
    allObjects <- coll.find(query).cursor[T]().collect[Seq](Int.MaxValue,Cursor.FailOnError[Seq[T]]())
  } yield allObjects

  protected def checkExistence(id: String): Future[Boolean] = for {
    existed <- exists(id)
  } yield if (existed) true else throw new ObjectNotExistsException(id)

}
