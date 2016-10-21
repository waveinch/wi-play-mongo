package ch.wavein.play.mongo

import ch.wavein.play.mongo.model.Identity
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Results._

import scala.concurrent.Future

/**
  * Created by mattia on 27/01/16.
  */
object SecureUpdateAction {
  def apply[A <: Identity](id: String)(action: Action[A]) = Action.async(action.parser) { request =>
    if (request.body.identity == id)
      action(request)
    else
      Future.successful(BadRequest(Json.obj("result" -> "Id in the url and in the body don't match")))
  }

}

