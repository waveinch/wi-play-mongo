package ch.wavein.play.mongo.components

import play.api.Logger
import play.api.libs.json.{JsValue, Reads}
import play.api.mvc.{Result, Results}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by unoedx on 21/10/16.
  */
trait JsonUtils extends Results {

  implicit def ec: ExecutionContext

  def validateJson[T](json: JsValue, success: (T, JsValue) => Future[Result])(implicit reads: Reads[T]) = {
    json.validate[T].asEither match {
      case Left(errors) => {
        Logger.warn(s"Bad request : $errors")
        Future(BadRequest(errors.mkString(",")))
      }
      case Right(valid) => success(valid, json)
    }
  }
}

