/**
 *  Copyright (C) 2009-2012 Typesafe Inc. <http://www.typesafe.com>
 */
package sample.remote.calculator

/*
 * comments like //#<tag> are there for inclusion into docs, please don’t remove
 */

import akka.kernel.Bootable
import scala.util.Random
import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import javax.naming.directory.{ InitialDirContext, Attribute }
import javax.naming.NamingException
import scala.collection.JavaConversions._

class LookupApplication extends Bootable {
  //#setup
  val system = ActorSystem("LookupApplication", ConfigFactory.load.getConfig("remotelookup"))
  val actor = system.actorOf(Props[LookupActor], "lookupActor")
  val (host, port) = {
    val x = ConfigFactory.load.getConfig("calculator")
    (x.getString("akka.remote.netty.hostname"), x.getString("akka.remote.netty.port"))
  }
  val ip = lookupIp(host).head
  println('server, host, ip, port)
  val remoteActor = System getProperty "remotetype" match {
    case "host" => system.actorFor(
      "akka://CalculatorApplication@%s:%s/user/simpleCalculator" format (host, port))
    case "ip" => system.actorFor(
      "akka://CalculatorApplication@%s:%s/user/simpleCalculator" format (ip, port))
  }

  def doSomething(op: MathOp) = {
    println('doSomething, op)
    actor ! (remoteActor, op)
  }
  //#setup

  def startup() {
    while (true) {
      println('ok)
      if (Random.nextInt(100) % 2 == 0) this.doSomething(Add(Random.nextInt(100), Random.nextInt(100)))
      else this.doSomething(Subtract(Random.nextInt(100), Random.nextInt(100)))

      Thread.sleep(200)
    }

  }

  def shutdown() {
    system.shutdown()
  }

  private def lookupIp(host: String): List[String] = {
    val attributes = try {
      new InitialDirContext getAttributes ("dns:/%s" format host)
    } catch {
      case _: NamingException ⇒ return Nil
    }
    val list = {
      val attributeEnumeration = attributes.getAll
      var list = List[Attribute]()
      while (attributeEnumeration.hasMore)
        list = attributeEnumeration.next :: list
      attributeEnumeration.close
      list.reverse
    }
    list map (x ⇒ x.getID -> x.get.toString) flatMap {
      case ("A", x)     ⇒ List(x)
      case ("CNAME", x) ⇒ lookupIp(x)
      case (_, x)       ⇒ Nil
    }
  }
}

//#actor
class LookupActor extends Actor {
  def receive = {
    case (actor: ActorRef, op: MathOp) ⇒ actor ! op
    case result: MathResult ⇒ result match {
      case AddResult(n1, n2, r)      ⇒ println("Add result: %d + %d = %d".format(n1, n2, r))
      case SubtractResult(n1, n2, r) ⇒ println("Sub result: %d - %d = %d".format(n1, n2, r))
    }
  }
}
//#actor

object LookupApp {
  def main(args: Array[String]) {
    val app = new LookupApplication
    println("Started Lookup Application")
    while (true) {
      println('ok)
      if (Random.nextInt(100) % 2 == 0) app.doSomething(Add(Random.nextInt(100), Random.nextInt(100)))
      else app.doSomething(Subtract(Random.nextInt(100), Random.nextInt(100)))

      Thread.sleep(200)
    }
  }
}
