package ch.wavein.play.mongo.components

import ch.wavein.play.mongo.model.Identity
import play.api.libs.json.Json
import play.api.mvc.{Action, BaseController}

import scala.concurrent.ExecutionContext

/**
  * Created by unoedx on 21/10/16.
  */
trait SlugGeneration[T <: Identity] extends CrudActions[T] { self:BaseController =>
  def toSlug(obj:T):T

  implicit def ec:ExecutionContext


  override def add(): Action[T] = crudActionBuilder.async(parse.json[T]) { implicit request =>
    val sub = toSlug(request.body)

    for {
      subAdded <- modelProvider.insert(sub)
    } yield Ok(Json.toJson(subAdded))

  }

}
