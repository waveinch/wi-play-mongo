package ch.wavein.play.mongo.components

import ch.wavein.play.mongo.{SecureUpdateController}
import ch.wavein.play.mongo.actions.ExistenceFilter
import ch.wavein.play.mongo.model.Identity
import ch.wavein.play.mongo.providers.Provider
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.BodyParsers.parse
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * Created by unoedx on 21/10/16.
  */
trait CrudActions[T <: Identity] extends SecureUpdateController { self:BaseController =>
  def crudActionBuilder: DefaultActionBuilder

  implicit def crudFormatter: OFormat[T]

  def modelProvider: Provider[T]

  implicit def ec: ExecutionContext


  def list() = crudActionBuilder.async { implicit request =>
    for {
      allSubs <- modelProvider.list()
    } yield Ok(Json.toJson(allSubs))
  }

  def get(id: String) = (crudActionBuilder andThen ExistenceFilter(id, modelProvider)).async { implicit request =>
    for {
      sub <- modelProvider.get(id)
    } yield Ok(Json.toJson(sub))
  }

  def update(id: String) = secureUpdateAction(id) {
    (crudActionBuilder andThen ExistenceFilter(id, modelProvider)).async(parse.json[T]) { implicit request =>
      val sub = request.body

      for {
        subUpdate <- modelProvider.update(sub)
      } yield Ok(Json.toJson(subUpdate))
    }
  }

  def add() = crudActionBuilder.async(parse.json[T]) { implicit request =>
    val sub = request.body

    for {
      subAdded <- modelProvider.insert(sub)
    } yield Ok(Json.toJson(subAdded))

  }

  def delete(id: String) = (crudActionBuilder andThen ExistenceFilter(id, modelProvider)).async { implicit request =>
    for {
      subDeleted <- modelProvider.delete(id)
    } yield Ok(Json.toJson(subDeleted))
  }
}

