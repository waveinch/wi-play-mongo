package ch.wavein.play.mongo.modules

import ch.wavein.play.mongo.sql.{MySQLFactory, MySQLFactorySingleton}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by unoedx on 02/11/16.
  */
class MySQLModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[MySQLFactory].to[MySQLFactorySingleton].asEagerSingleton()
  }
}
