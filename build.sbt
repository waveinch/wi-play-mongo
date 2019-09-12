name := """wi-play-mongo"""

organization := "ch.wavein"

version := "1.7.1"
isSnapshot := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  guice,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.18.1-play27",
  "org.squeryl" % "squeryl_2.13" % "0.9.14",
  "mysql" % "mysql-connector-java" % "5.1.10",
  "com.zaxxer" % "HikariCP" % "2.5.1"
)

publishTo := sonatypePublishTo.value