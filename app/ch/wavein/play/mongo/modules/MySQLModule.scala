package ch.wavein.play.mongo.modules

import com.google.inject.AbstractModule

import ch.wavein.play.mongo.sql.{MySQLFactory, MySQLFactorySingleton}

/**
  * Created by unoedx on 02/11/16.
  */
class MySQLModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[MySQLFactory]).to(classOf[MySQLFactorySingleton]).asEagerSingleton()
  }
}
