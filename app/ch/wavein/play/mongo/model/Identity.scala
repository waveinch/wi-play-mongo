package ch.wavein.play.mongo.model

/**
  * Created by unoedx on 21/10/16.
  */
class IdentityNotFoundException() extends Exception("The _id must exists")

trait Identity {
  def _id: Option[String]
  def identity: String = _id.getOrElse(throw new IdentityNotFoundException)

}
