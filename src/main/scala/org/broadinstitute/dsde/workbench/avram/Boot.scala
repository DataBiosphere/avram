package org.broadinstitute.dsde.workbench.avram

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.broadinstitute.dsde.workbench.avram.api.{AvramRoutes, Skeleton}

object Boot extends App with LazyLogging {

  private def startup(): Unit = {

    println("IN STARTUP()")

    val config = ConfigFactory.load()

    println("LOADED CONFIG FACTORY")

//    // we need an ActorSystem to host our application in
//    implicit val system = ActorSystem("avram")
//
//    println("CREATED ACTOR SYSTEM")
//
//    implicit val materializer = ActorMaterializer()
//
//    println("CREATED MATERIALIZER")

    import scala.concurrent.ExecutionContext.Implicits.global

    //println("IMPORTED EXECUTION CONTEXT")

    //val avramRoutes = new AvramRoutes()


    //println("INITILIZED AVRAM ROUTES")

    val skel = new Skeleton
//
//    Http().bindAndHandle(avramRoutes.route, "0.0.0.0", 8080)
//      .recover {
//        case t: Throwable =>
//          logger.error("FATAL - failure starting http server", t)
//          throw t
//      }

    println("BINDED THE THING")
  }

  startup()
}
