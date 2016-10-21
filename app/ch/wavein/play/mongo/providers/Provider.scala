package ch.wavein.play.mongo.providers



import ch.wavein.play.mongo.model.Identity

import scala.concurrent.Future

/**
  * Created by mattia on 28/06/16.
  */
class ObjectNotExistsException(val id: String) extends Exception(s"Object $id not exists")

class CantUpdateObject(val id: String) extends Exception("Can't update (or find) obj -> " + id)

trait Provider[T <: Identity] {
  def insert(obj: T): Future[T]

  /**
    * Warinig NOT thread safe!
    * @param obj
    * @return
    */
  def insertOrUpdate(obj: T): Future[T] = obj._id.isDefined match {
    case true => update(obj)
    case false => insert(obj)
  }

  def get(id: String): Future[T]

  def find(id: String): Future[Option[T]]

  def find(id: Option[String]): Future[Option[T]]

  def findMany(ids: Seq[String], ordered: Boolean): Future[Seq[T]]

  def update(obj: T): Future[T]

  def softUpdate(obj: T): Future[T]

  def delete(id: String): Future[T]

  def list(): Future[Seq[T]]

  def list(limit:Int, order:String): Future[Seq[T]]

  def listIds() : Future[Seq[String]]

  def exists(id: String): Future[Boolean]
}