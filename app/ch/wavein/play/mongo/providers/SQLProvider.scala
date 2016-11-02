package ch.wavein.play.mongo.providers

import ch.wavein.play.mongo.model.Identity
import ch.wavein.play.mongo.sql.SquerylEntrypoint
import org.squeryl.{KeyedEntityDef, Schema, Table}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by unoedx on 02/11/16.
  */

trait SQLProvider[M <: Identity] extends Provider[M] {

  val entryPoint:SquerylEntrypoint

  import entryPoint._

  implicit def ex:ExecutionContext

  implicit def key:KeyedEntityDef[M, String] = new KeyedEntityDef[M,String] {
    override def getId(a: M): String = a._id.getOrElse("no id yet")

    override def isPersisted(a: M): Boolean = a._id.isDefined

    override def idPropertyName: String = "_id"
  }

  def setId(o:M):M

  def table:Table[M]

  def exec[T](f: => T) = Future{
    transaction{
      f
    }
  }

  override def insert(obj: M): Future[M] = exec{
      val newObj = setId(obj)
      table.insert(newObj)
  }

  override def get(id: String): Future[M] =  exec{
      table.get(id)
  }

  override def find(id: String): Future[Option[M]] = get(id).map(x => Some(x)).recover{case _ => None}

  override def find(id: Option[String]): Future[Option[M]] = id match {
    case Some(i) => find(i)
    case None => Future.successful(None)
  }

  override def findMany(ids: Seq[String], ordered: Boolean): Future[Seq[M]] = exec{
      from(table) { s =>
        where(s.identity in ids) select(s)
      }.toSeq
  }

  override def update(obj: M): Future[M] = exec{
      table.update(obj)
      obj
  }

  override def softUpdate(obj: M): Future[M] = update(obj)

  override def delete(id: String): Future[M] = exec{
      val obj = table.get(id)
      table.delete(id)
      obj

  }

  override def list(): Future[Seq[M]] = exec{
      from(table) { s =>
        select(s)
      }.toSeq

  }

  override def list(limit: Int, order: String): Future[Seq[M]] = exec{
      from(table) { s =>
        select(s)
      }.page(0, limit).toSeq

  }

  override def listIds(): Future[Seq[String]] = exec{
      from(table) { s =>
        select(s.identity)
      }.toSeq
  }

  override def exists(id: String): Future[Boolean] = exec{
      Try(table.get(id)).toOption.isDefined
  }
}


