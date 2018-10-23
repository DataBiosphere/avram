package org.broadinstitute.dsde.workbench.avram.api

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import io.circe.syntax._

import java.util.UUID
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import org.broadinstitute.dsde.workbench.avram.model.SamResource
import org.broadinstitute.dsde.workbench.avram.service.CollectionsService
import scala.concurrent.ExecutionContext.Implicits.global


@WebServlet(name = "Collections", description = "Collections endpoints", urlPatterns = Array("/api/collections/v1/*"))
class CollectionsServlet extends HttpServlet with BaseEndpoint {

  private val log = Logger.getLogger(getClass.getName)
  private val collectionsService = new CollectionsService()

  //@throws[IOException]
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    resp.setContentType("text/plain")
    val parameter = req.getPathInfo.stripPrefix("/")
    log.info("parameter:" + parameter)
    val collectionExternalId = UUID.fromString(parameter)
    val collection = collectionsService.getCollection(collectionExternalId)
    resp.getWriter.append(collection.asJson.noSpaces)
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    resp.setContentType("text/plain")
    val parameters = req.getPathInfo.split("/").toList
    val samResource = SamResource(parameters.head)
    val collection = collectionsService.createCollection(samResource, "somebodythatiusedtoknow")
    resp.getWriter.append(collection.asJson.noSpaces)
  }
}
