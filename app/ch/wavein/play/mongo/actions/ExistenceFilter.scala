package ch.wavein.play.mongo.actions

import ch.wavein.play.mongo.model.Identity
import ch.wavein.play.mongo.providers.Provider
import play.api.libs.json.Json
import play.api.mvc.{ActionFilter, Request}
import play.api.mvc.Results._

import scala.concurrent.ExecutionContext

/**
  * Created by unoedx on 21/10/16.
  */
object ExistenceFilter {
  def apply[B <: Identity, T[A] <: Request[A]](id: String, provider: Provider[B])(implicit ec: ExecutionContext) = new ActionFilter[T] {

    override protected def executionContext: ExecutionContext = ec

    override protected def filter[A](request: T[A]) = for {
      exist <- provider.exists(id)
    } yield if (exist) None else Some(BadRequest(Json.obj("Result" -> "Item not exists")))
  }
}