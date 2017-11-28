package ch.wavein.play.mongo.providers

import ch.wavein.play.mongo.model.Identity

import scala.concurrent.Future
import scala.util.Random


/**
  * Created by unoedx on 21/10/16.
  */
trait MockProvider[T <: Identity] extends Provider[T] {

  val storage = scala.collection.mutable.HashMap[String, T]()

  def addId(obj:T):T

  override def insert(obj: T, autoGenerateId:Boolean = true): Future[T] = Future.successful {

    val result = addId(obj)

    storage += result.identity -> result

    result
  }

  override def get(id: String): Future[T] = Future.successful {
    storage.get(id).get
  }

  override def find(id: String): Future[Option[T]] = Future.successful {
    storage.get(id)
  }

  override def find(id: Option[String]): Future[Option[T]] = Future.successful {
    id.flatMap(storage.get)
  }

  override def findMany(ids: Seq[String], ordered: Boolean): Future[Seq[T]] = Future.successful {
    ids.flatMap{ id =>
      storage.get(id)
    }
  }

  override def update(obj: T): Future[T] = Future.successful {
    storage.update(obj.identity, obj)
    obj
  }

  override def softUpdate(obj: T): Future[T] = update(obj)

  override def delete(id: String): Future[T] = Future.successful{
    storage.remove(id).get
  }

  override def list(): Future[Seq[T]] = Future.successful{
    storage.values.toSeq
  }

  override def list(limit: Int, order: String): Future[Seq[T]] = Future.successful{
    storage.values.toSeq.take(limit)
  }

  override def listIds(): Future[Seq[String]] = Future.successful{
    storage.keys.toSeq
  }

  override def exists(id: String): Future[Boolean] = Future.successful{
    storage.keys.exists(_ == id)
  }
}
