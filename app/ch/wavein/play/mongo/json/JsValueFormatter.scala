package ch.wavein.play.mongo.json

import ch.wavein.play.mongo.json.BsonFormatter.{toBsValue, toJsValue}
import play.api.libs.json.JsValue
import reactivemongo.api.bson.{BSONReader, BSONValue, BSONWriter}

import scala.util.Try

object JsValueFormatter {
  implicit object JsValueWriter extends BSONWriter[JsValue] {

    override def writeTry(t: JsValue): Try[BSONValue] = Try{
      toBsValue(t)
    }
  }

  implicit object JsValueReader extends BSONReader[JsValue] {
    override def readTry(bson: BSONValue): Try[JsValue] = Try{
      toJsValue(bson)
    }
  }
}
