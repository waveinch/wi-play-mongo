name := """wi-play-mongo"""

organization := "ch.wavein"

version := "1.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "org.reactivemongo" %% "reactivemongo" % "0.11.14",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "org.squeryl" % "squeryl_2.11" % "0.9.7",
  "mysql" % "mysql-connector-java" % "5.1.10"
)

