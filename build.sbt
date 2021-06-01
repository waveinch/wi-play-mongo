import com.typesafe.sbt.GitVersioning

name := """wi-play-mongo"""

organization := "ch.wavein"

isSnapshot := false

lazy val root = (project in file("."))
  .settings(
    licenses += ("Apache-2.0", url("http://www.opensource.org/licenses/apache2.0.php")),
    developers := List(
      Developer(id="minettiandrea", name="Andrea Minetti", email="andrea@wavein.ch", url=url("https://wavein.ch")),
    ),
    homepage := Some(url("https://github.com/waveinch/wi-play-mongo"))
  ).enablePlugins(
  PlayScala
)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "org.reactivemongo" %% "play2-reactivemongo" % "1.0.0-play27",
  "org.squeryl" % "squeryl_2.13" % "0.9.14",
  "mysql" % "mysql-connector-java" % "5.1.10",
  "com.zaxxer" % "HikariCP" % "2.5.1"
)
