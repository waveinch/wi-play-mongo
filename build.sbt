import com.typesafe.sbt.GitVersioning

name := """wi-play-mongo"""

organization := "ch.wavein"

isSnapshot := false

lazy val root = (project in file("."))
  .settings(
    bintrayRepository := "maven",
    bintrayOrganization := Some("waveinch"),
    publishMavenStyle := true,
    licenses += ("Apache-2.0", url("http://www.opensource.org/licenses/apache2.0.php")),
    git.useGitDescribe := true
  ).enablePlugins(
  PlayScala,
  GitVersioning
)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.18.1-play27",
  "org.squeryl" % "squeryl_2.13" % "0.9.14",
  "mysql" % "mysql-connector-java" % "5.1.10",
  "com.zaxxer" % "HikariCP" % "2.5.1"
)
