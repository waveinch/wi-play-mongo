package ch.wavein.play.mongo.providers

import ch.wavein.play.mongo.model.Identity
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import play.modules.reactivemongo.json._

import scala.collection._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mattia on 28/06/16.
  */
trait MongoProvider[T <: Identity] extends Provider[T] {
  def collection: Future[JSONCollection]

  implicit def formatter: OFormat[T]

  implicit def ec: ExecutionContext

  protected def createUniqueId(obj: T): String = BSONObjectID.generate.stringify

  override def insert(obj: T): Future[T] = {

    val newId = createUniqueId(obj)
    val jsonWithNewId = Json.toJson(obj).as[JsObject] ++ Json.obj("_id" -> newId)

    for {
      coll <- collection
      _ <- coll.insert(jsonWithNewId)
      obj <- get(newId)
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
    ).cursor[T]().collect[Seq]()
  } yield ordered match {
    case true =>
      val hashMap: Map[String, T] = unorderedList.map(o => o.identity -> o)(breakOut)
      ids.flatMap(hashMap.get)
    case false => unorderedList
  }

  override def update(obj: T): Future[T] = for {
    coll <- collection
    _ <- checkExistence(obj.identity)
    _ <- coll.update(Json.obj("_id" -> obj._id), obj)
    updatedObj <- get(obj.identity)
  } yield updatedObj

  override def softUpdate(obj: T): Future[T] = for {
    coll <- collection
    _ <- checkExistence(obj.identity)
    _ <- coll.update(
      Json.obj("_id" -> obj._id),
      Json.obj("$set" -> obj))
    updatedObj <- get(obj.identity)
  } yield updatedObj

  override def delete(id: String): Future[T] = for {
    coll <- collection
    obj <- get(id)
    _ <- coll.remove(Json.obj("_id" -> id))
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
      allObjects <- coll.find(Json.obj()).sort(mongoOrder).cursor[T]().collect[Seq](limit)
    } yield allObjects
  }

  def listIds(): Future[Seq[String]] = for {
    coll <- collection
    ids <- coll.find(Json.obj(), Json.obj("_id" -> true))
      .cursor[JsObject]().collect[Seq]()
      .map(_.flatMap({ j => (j \ "_id").validate[String].asOpt }))
  } yield ids

  def rawUpdate(id: String, jsUpdate: JsObject): Future[T] = for {
    coll <- collection
    _ <- checkExistence(id)
    _ <- coll.update(Json.obj("_id" -> id), jsUpdate)
    updatedObj <- get(id)
  } yield updatedObj

  protected def list(query: JsObject): Future[Seq[T]] = for {
    coll <- collection
    allObjects <- coll.find(query).cursor[T]().collect[Seq]()
  } yield allObjects

  protected def checkExistence(id: String): Future[Boolean] = for {
    existed <- exists(id)
  } yield if (existed) true else throw new ObjectNotExistsException(id)

}
