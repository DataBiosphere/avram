package org.broadinstitute.dsde.workbench.avram.dataaccess

import io.circe.generic.auto._
import io.circe.syntax._
import org.json4s.{DefaultFormats, JsonAST}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{compact, render}
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.{Header, HttpRequest, HttpResponse}
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.scalatest.{BeforeAndAfterEach, Suite}

/**
  * Mix-in trait for managing a mock server for Sam. Includes convenience functions for building Sam
  * requests and expected responses.
  */
trait MockSam extends BeforeAndAfterEach { self: Suite =>
  class MockSam(port: Int) extends ClientAndServer(port) {
    def baseUrl: String = s"http://localhost:$port"
  }
  implicit val formats = DefaultFormats

  def samPort: Int
  var mockSam: MockSam = _

  def buildUserStatusRequest(token: String): HttpRequest = {
    request()
      .withMethod("GET")
      .withPath(s"/register/user/v2/self/info")
      .withHeader(new Header("Authorization", s"Bearer $token".trim))
  }

  def buildQueryActionRequest(samResource: String, action: String, token: String): HttpRequest = {
    request()
      .withMethod("GET")
      .withPath(s"/api/resources/v1/entity-collection/$samResource/action/$action")
      .withHeader(new Header("Authorization", s"Bearer $token".trim))
  }

  def buildUserStatusResponse(subjectId: String, email: String): HttpResponse = {
    val response = buildResponse(
      ("userSubjectId" -> subjectId) ~
      ("userEmail" -> email) ~
      ("enabled" -> true))
    response
  }

  def buildResponse(json: JsonAST.JValue): HttpResponse = {
    response(compact(render(json)))
  }

  override def beforeEach(): Unit = {
    mockSam = new MockSam(samPort)
    super.beforeEach()
  }

  override def afterEach(): Unit = {
    try super.afterEach()
    finally mockSam.stop()
  }
}
