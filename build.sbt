name := """wi-play-mongo"""

organization := "ch.wavein"

version := "2.0.4"
isSnapshot := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  guice,
  "org.reactivemongo" %% "reactivemongo" % "0.19.1"
)

publishTo := sonatypePublishTo.value
