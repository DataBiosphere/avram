package org.broadinstitute.dsde.workbench.avram

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.broadinstitute.dsde.workbench.avram.api.AvramRoutes

object Boot extends App with LazyLogging {

  private def startup(): Unit = {

    val config = ConfigFactory.load()

    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("avram")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global

    val avramRoutes = new AvramRoutes()

    Http().bindAndHandle(avramRoutes.route, "0.0.0.0", 8080)
      .recover {
        case t: Throwable =>
          logger.error("FATAL - failure starting http server", t)
          throw t
      }
  }

  startup()
}
