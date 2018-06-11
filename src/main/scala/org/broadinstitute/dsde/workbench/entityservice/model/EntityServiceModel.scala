package org.broadinstitute.dsde.workbench.entityservice.model

import java.net.URL

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsde.workbench.model.ValueObjectFormat
import org.broadinstitute.dsde.workbench.model.google.{GcsPath, parseGcsPath}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat}
import org.broadinstitute.dsde.workbench.model.WorkbenchIdentityJsonSupport._
import org.broadinstitute.dsde.workbench.model.google.GoogleModelJsonSupport._
import org.broadinstitute.dsde.workbench.model.google._
import org.broadinstitute.dsde.workbench.model._
import spray.json._


object EntityServiceJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object URLFormat extends JsonFormat[URL] {
    def write(obj: URL) = JsString(obj.toString)

    def read(json: JsValue): URL = json match {
      case JsString(url) => new URL(url)
      case other => throw DeserializationException("Expected URL, got: " + other)
    }
  }

  // Overrides the one from workbench-libs to serialize/deserialize as a URI
  implicit object GcsPathFormat extends JsonFormat[GcsPath] {
    def write(obj: GcsPath) = JsString(obj.toUri)

    def read(json: JsValue): GcsPath = json match {
      case JsString(uri) => parseGcsPath(uri).getOrElse(throw DeserializationException(s"Could not parse bucket URI from: $uri"))
      case other => throw DeserializationException(s"Expected bucket URI, got: $other")
    }
  }

//  implicit val UserClusterExtensionConfigFormat = jsonFormat3(UserJupyterExtensionConfig.apply)

}