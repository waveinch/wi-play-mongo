package ch.wavein.play.mongo.sql

import javax.inject.{Inject, Singleton}

import org.squeryl.Session
import org.squeryl.adapters.MySQLAdapter
import play.api.Configuration

/**
  * Created by unoedx on 02/11/16.
  */

trait MySQLFactory{
  def start()
}

@Singleton
class MySQLFactorySingleton @Inject()(configuration: Configuration) extends MySQLFactory {
  import org.squeryl.SessionFactory

  start()

  def start() = {
    Class.forName("com.mysql.jdbc.Driver");
    SessionFactory.concreteFactory = Some(() =>
      Session.create(
        java.sql.DriverManager.getConnection(configuration.getString("db.default.url").get),
        new MySQLAdapter))
  }
}
