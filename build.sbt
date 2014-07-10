organization := "com.github.dnvriend"

name := "spray-basic-authentication"

version := "0.0.1"

isSnapshot := true

scalaVersion := "2.11.1"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= {
  val scalaV = "2.11.1"
  val akkaV = "2.3.4"
  val sprayV = "1.3.1"
  val shapelessV = "2.0.0"
  val jsonV = "1.2.6"
  Seq(
    "org.scala-lang" % "scala-library" % scalaV,
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe" % "config" % "1.2.0",
    "com.typesafe.akka" %% "akka-persistence-experimental" % akkaV,
    "io.spray" %% "spray-http" % sprayV,
    "io.spray" %% "spray-httpx" % sprayV,
    "io.spray" %% "spray-routing-shapeless2" % sprayV,
    "io.spray" %% "spray-util" % sprayV,
    "io.spray" %% "spray-io" % sprayV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-client" % sprayV,
    "io.spray" %% "spray-json" % jsonV,
    "org.apache.shiro" % "shiro-all" % "1.2.3"
  )
}

autoCompilerPlugins := true

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

publishArtifact in Test := false

net.virtualvoid.sbt.graph.Plugin.graphSettings

com.github.retronym.SbtOneJar.oneJarSettings

net.virtualvoid.sbt.graph.Plugin.graphSettings