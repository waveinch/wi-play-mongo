package ch.wavein.play.mongo.json

import play.api.libs.json._
import reactivemongo.api.bson.{BSONArray, BSONBinary, BSONBoolean, BSONDateTime, BSONDecimal, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONDouble, BSONElement, BSONInteger, BSONJavaScript, BSONJavaScriptWS, BSONLong, BSONMaxKey, BSONMinKey, BSONNull, BSONObjectID, BSONReader, BSONRegex, BSONString, BSONSymbol, BSONTimestamp, BSONUndefined, BSONValue, BSONWriter}

import scala.util.Try

object BsonFormatter {

  implicit def jsObjToBson(obj:JsObject):BSONDocument = BSONDocument(obj.fields.map(toBsPair))

  implicit def toBsValue(js:JsValue):BSONValue = js match {
    case JsArray(value) => BSONArray(value.map(toBsValue))
    case JsNull => BSONNull
    case JsBoolean(bool) => BSONBoolean(bool)
    case JsNumber(value) if value.isValidLong => BSONLong(value.longValue)
    case JsNumber(value) => BSONDouble(value.doubleValue)
    case JsObject(underlying) => BSONDocument(underlying.toSeq.map(toBsPair))
    case JsString(value) => BSONString(value)
  }

  def toBsPair(k:(String,JsValue)):(String,BSONValue) = k._1 -> toBsValue(k._2)

  implicit def toJsValue(bs:BSONValue):JsValue = bs match {
    case BSONDouble(double) => JsNumber(double)
    case BSONString(str) => JsString(str)
    case BSONArray(arr) => JsArray(arr.map(toJsValue))
    case BSONBinary(bin) => JsString(bin.toString)
    case BSONUndefined => JsNull
    case BSONObjectID(id) => JsString(java.util.Base64.getEncoder.encodeToString(id))
    case BSONBoolean(bool) => JsBoolean(bool)
    case BSONDateTime(time) => JsNumber(time)
    case BSONNull => JsNull
    case BSONRegex(regex) => JsString(regex._1)
    case BSONJavaScript(script) => JsString(script)
    case BSONSymbol(sym) => JsString(sym)
    case BSONJavaScriptWS(script) => JsNull
    case BSONInteger(i) => JsNumber(i)
    case BSONTimestamp(ts) => JsNumber(ts)
    case BSONLong(l) => JsNumber(l)
    case BSONDecimal(d) => JsNumber(d._1)
    case BSONMinKey => JsNull
    case BSONMaxKey => JsNull
    case BSONDocument(doc) => JsObject(doc.map(toJsPair))
  }

  def toJsPair(e:BSONElement):(String,JsValue) = e.name -> toJsValue(e.value)

  implicit object JsObjectWriter extends BSONDocumentWriter[JsObject] {
    override def writeTry(t: JsObject): Try[BSONDocument] = Try{
      jsObjToBson(t)
    }
  }

  implicit object JsObjectReader extends BSONDocumentReader[JsObject] {
    override def readDocument(doc: BSONDocument): Try[JsObject] = Try{
      JsObject(doc.elements.map(toJsPair))
    }
  }




}
