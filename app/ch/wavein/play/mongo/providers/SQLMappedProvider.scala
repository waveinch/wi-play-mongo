package ch.wavein.play.mongo.providers

import ch.wavein.play.mongo.model.Identity

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by unoedx on 07/11/16.
  */
trait SQLMappedProvider[M <: Identity,T <: Identity] extends Provider[M]  {

  implicit def ec:ExecutionContext

  def sqlProvider:SQLProvider[T]

  def toSQL(m:M):T
  def fromSQL(t:T):M

  def sql(m: M)(f:(T => Future[T])) = f(toSQL(m)).map(fromSQL)

  override def insert(obj: M, autoGenerateId:Boolean = true): Future[M] = sql(obj) { t =>
    sqlProvider.insert(t)
  }

  override def update(obj: M): Future[M] = sql(obj) { t =>
    sqlProvider.update(t)
  }

  override def softUpdate(obj: M): Future[M] = sql(obj) { t =>
    sqlProvider.softUpdate(t)
  }

  override def get(id: String): Future[M] = sqlProvider.get(id).map(fromSQL)

  override def find(id: String): Future[Option[M]] = sqlProvider.find(id).map(_.map(fromSQL))

  override def find(id: Option[String]): Future[Option[M]] = sqlProvider.find(id).map(_.map(fromSQL))

  override def findMany(ids: Seq[String], ordered: Boolean): Future[Seq[M]] = sqlProvider.findMany(ids,ordered).map(_.map(fromSQL))

  override def delete(id: String): Future[M] = sqlProvider.delete(id).map(fromSQL)

  override def list(): Future[Seq[M]] = sqlProvider.list().map(_.map(fromSQL))

  override def list(limit:Int, order:String): Future[Seq[M]] = sqlProvider.list(limit,order).map(_.map(fromSQL))

  override def listIds(): Future[Seq[String]] = sqlProvider.listIds()

  override def exists(id: String): Future[Boolean] = sqlProvider.exists(id)

}
