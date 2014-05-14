import sbt._
import sbt.Keys._
import play.Project._

object ApplicationBuild extends Build {
  val buildName = "playr-hack"
  val appVersion = "0.0.1-SNAPSHOT"

  val BuildSettings = super.settings ++  
  Seq(
    scalaVersion := "2.10.3",
    organization := "26lights",
    scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls"),
    version := appVersion,
    resolvers ++= repos,
    Keys.fork in Test := true
  )
  //--------------------------------------------------

  val repos = Seq(
     "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )
  
  val dependencies = Seq(
    "com.typesafe.play"       %% "play"                   % "2.2.3",
    "com.typesafe.play"       %% "play-cache"             % "2.2.3",
    // test scope
    "com.typesafe.play"       %% "play-test"              % "2.2.3"  % "test",
    "org.scalatest"           %% "scalatest"              % "2.0"    % "test",
    "org.reactivemongo"       %%  "play2-reactivemongo"   % "0.10.2"
  )


  lazy val playr = Project(
    id = "playr",
    base = file("PlayR"),
    settings = Project.defaultSettings ++ BuildSettings ++ Seq(
      name := "playr", 
      version := appVersion,
      libraryDependencies := dependencies
    )
  )

  lazy val playrTutorial = play.Project(
    name = "playr-tutorial",
    applicationVersion = appVersion,
    dependencies = dependencies,
    settings = BuildSettings,
    path = file("PlayR/samples/playr-tutorial")
  ).dependsOn(playr, playrMongo)

  lazy val playrSwagger = play.Project(
    name = "playr-swagger",
    applicationVersion = appVersion,
    dependencies = dependencies,
    settings = BuildSettings,
    path = file("Playr-Swagger")
  ).dependsOn(playr)

  lazy val playrMongo = play.Project(
    name = "playr-mongo",
    applicationVersion = appVersion,
    dependencies = dependencies,
    settings = BuildSettings,
    path = file("PlayR-mongo")
  ).dependsOn(playr)

  lazy val main = play.Project(
    name = "hack-app",
    applicationVersion = appVersion,
    dependencies = dependencies,
    path = file("."),
    settings = BuildSettings
  ).dependsOn(playr, playrTutorial, playrSwagger, playrMongo)
   .aggregate(playr, playrTutorial, playrSwagger, playrMongo)
}
