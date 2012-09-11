import sbt._
import sbt.Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions}

object AkkaProjectInScalaBuild extends Build {

  lazy val akkaProjectInScala = Project(
    id = "akka-project-in-scala",
    base = file("."),
    settings = Project.defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      name := "Akka Project In Scala",
      organization := "org.example",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.1",
      libraryDependencies ++= Dependencies.sessionKernel,
      distJvmOptions in Dist := "$JAVA_OPTS",
      outputDirectory in Dist := file("target/remoter-dist")

      //resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      //libraryDependencies ++= Seq(
	  //    "com.typesafe.akka" % "akka-actor" % "2.0.3",
	  //    "com.typesafe.akka" % "akka-remote" % "2.0.3")
    )
  )
}

object Dependencies {
  import Dependency._
 
  val sessionKernel = Seq(
    akkaKernel, akkaSlf4j, akkaRemote, logback, netty, rabbit, finagleCore, finagleMemcache //, akkaAmqp
  )
}
 
object Dependency {
  // Versions
  object V {
    val Akka      = "2.0.3"
  }
 
  val akkaKernel        = "com.typesafe.akka" % "akka-kernel"        % V.Akka
  val akkaSlf4j         = "com.typesafe.akka" % "akka-slf4j"         % V.Akka
  val akkaRemote = "com.typesafe.akka" % "akka-remote" % V.Akka
  val logback           = "ch.qos.logback"    % "logback-classic"    % "1.0.0"

  val netty = "io.netty" % "netty" % "3.5.4.Final"
  val zeromq = "org.zeromq" % "jzmq" % "1.0.0"
  
  //This block needed for hootbomb-commons in lib/ :
  val salat = "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
  val rabbit = "com.rabbitmq" % "amqp-client" % "2.8.2"
  //val akkaamqp = "com.typesafe.akka" %% "akka-amqp" % "2.1-SNAPSHOT"
  val finagleCore = "com.twitter" % "finagle-core" % "5.0.3"
  val finagleMemcache = "com.twitter" % "finagle-memcached" % "5.0.3"
  val codahaleJerkson = "com.codahale"              %% "jerkson"         % "0.5.0"


  val hootbombCommons = "com.hootsuite" %% "hootbomb-commons" % "0.1" changing()
  val akkaAmqp = "com.typesafe.akka" %% "akka-amqp" % "2.1-SNAPSHOT"
  
}
