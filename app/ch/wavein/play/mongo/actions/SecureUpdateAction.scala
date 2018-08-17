package ch.wavein.play.mongo

import ch.wavein.play.mongo.model.Identity
import play.api.libs.json.Json
import play.api.mvc.{Action, BaseController, Controller}

import scala.concurrent.Future

/**
  * Created by mattia on 27/01/16.
  */
trait SecureUpdateController { self:BaseController =>
  def secureUpdateAction[A <: Identity](id: String)(action: Action[A]) = Action.async(action.parser) { request =>
    if (request.body.identity == id)
      action(request)
    else
      Future.successful(BadRequest(Json.obj("result" -> "Id in the url and in the body don't match")))
  }

}

