package ch.wavein.play.mongo.providers

import ch.wavein.play.mongo.model.{Identity, Slug}
import play.api.libs.json.{Format, JsValue, Json}
import reactivemongo.play.json.compat._
import json2bson.{toDocumentReader, toDocumentWriter}

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by unoedx on 13/09/16.
  */
trait SlugFinder[T <: Identity with Slug] extends Provider[T] {
  def findBySlug(slug: String): Future[Option[T]]
}

trait SlugField{
  def slugField:String
}

trait DefaultSlugField extends SlugField{
  override def slugField:String = "slug"
}

trait MongoSlugFinder[T <: Identity with Slug] extends MongoProvider[T] with SlugFinder[T] with SlugField{

  implicit def ec: ExecutionContext

  override def findBySlug(slug: String): Future[Option[T]] = for {
    coll <- collection
    page <- coll.find(Json.obj(slugField -> slug.toLowerCase)).one[JsValue].map(_.map(_.as[T]))
  } yield page
}
