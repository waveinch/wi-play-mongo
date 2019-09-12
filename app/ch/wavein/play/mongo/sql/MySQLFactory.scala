package ch.wavein.play.mongo.sql

import javax.inject.{Inject, Singleton}

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
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

  val config = new HikariConfig();
  config.setJdbcUrl(configuration.get[String]("db.default.url"));
  config.addDataSourceProperty("maximumPoolSize",configuration.get[Int]("db.default.maximumPoolSize"));

  val ds = new HikariDataSource(config);


  def start() = {
    Class.forName("com.mysql.jdbc.Driver");
    SessionFactory.concreteFactory = Some { () =>
      Session.create(
        ds.getConnection,
        new MySQLAdapter)
    }
  }
}
