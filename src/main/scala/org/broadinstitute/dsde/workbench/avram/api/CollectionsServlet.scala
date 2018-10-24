package org.broadinstitute.dsde.workbench.avram.api

import io.circe.generic.auto._
import io.circe.syntax._
import java.util.UUID
import java.util.logging.Logger
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import org.broadinstitute.dsde.workbench.avram.model.{AvramException, SamResource}
import org.broadinstitute.dsde.workbench.avram.service.CollectionsService

import scala.concurrent.Future
import scala.util.{Failure, Success}


class CollectionsServlet extends HttpServlet with AvramServlet {

  private val log = Logger.getLogger(getClass.getName)
  private val collectionsService = new CollectionsService()

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    handleAuthenticatedRequest(request, response) { userInfo =>
      response.setContentType("text/plain")
      val parameter = request.getPathInfo.stripPrefix("/")
      val collectionExternalId = UUID.fromString(parameter)
      val future = collectionsService.getCollection(collectionExternalId)
      registerOnComplete(future, request, response) { collection =>
        response.setStatus(HttpServletResponse.SC_OK)
        response.getWriter.write(collection.asJson.noSpaces)
      }
//      future onComplete {
//        case Success(collection) => {
//          response.setStatus(HttpServletResponse.SC_OK)
//          response.getWriter.write(collection.asJson.noSpaces)
//        }
//        case Failure(e: AvramException) => {
//          response.setStatus(e.status)
//          response.getWriter.write(e.message)
//        }
//        case Failure(e) => response.getWriter.write(e.getMessage)
//      }
//      future
    } //.asJson.noSpaces
  }



//  override def doPost(request: HttpServletRequest, response: HttpServletResponse) = {
//    handleAuthenticatedRequest(request, response) { userInfo =>
//      response.setContentType("text/plain")
//      val parameter = request.getPathInfo.stripPrefix("/")
//      val samResource = SamResource(parameter)
//      collectionsService.createCollection(samResource, userInfo.userEmail)
//    }.asJson.noSpaces
////      collectionFuture onComplete {
////        case Success(collection) => {
////          response.setStatus(HttpServletResponse.SC_OK)
////          response.getWriter.write(collection.asJson.noSpaces)
////        }
////        case Failure(e: AvramException) => {
////          response.setStatus(e.status)
////          response.getWriter.write(e.message)
////        }
////        case Failure(e) => response.getWriter.write(e.getMessage)
////      }
////
////      Await.result(collectionFuture, Duration.apply(30, "second"))
////    }
//  }


}
