package org.broadinstitute.dsde.workbench.avram.api

import com.fasterxml.jackson.annotation.{JsonAutoDetect, JsonProperty}
import com.google.api.server.spi.config.{Api, ApiMethod, Named}
import com.google.api.server.spi.response.NotFoundException
import com.google.appengine.api.users.User

import scala.util
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import lombok.Data
import org.broadinstitute.dsde.workbench.avram.api.MarshallableImplicits._

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Defines v1 of a helloworld API, which provides simple "greeting" methods.
  */



case class HelloGreeting(@JsonProperty("message") @BeanProperty val message: String)
case class HelloGreeting2(val message: String)


case class SomethingElse( @JsonProperty("message") @BeanProperty val message: String, @JsonProperty("const") @BeanProperty val const: Int)


@Api(name = "helloworld", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"), clientIds = Array("908863053157-du7labuo25ljnguh9kbgp67dfk8po828.apps.googleusercontent.com"), audiences = Array("908863053157-du7labuo25ljnguh9kbgp67dfk8po828.apps.googleusercontent.com"))
class Greetings {
  val greetings = Array(HelloGreeting("hello world!"), HelloGreeting("goodbye world!"))

  @throws[NotFoundException]
  def getGreeting(@Named("id") id: Integer): HelloGreeting = try
    greetings(id)
  catch {
    case e: IndexOutOfBoundsException =>
      throw new NotFoundException("Greeting not found with an index: " + id)
  }

  def listGreeting: Future[Array[HelloGreeting]] = {
    implicit val ec = ExecutionContext.global
    Future{greetings}.
  }

  def listStrings: Array[String] = Array("Please show up")

  @ApiMethod(name = "greetings.multiply", httpMethod = "post")
  def insertGreeting(@Named("times") times: Integer, greeting: SomethingElse): SomethingElse = {
    //logging here
    //HelloGreeting(greeting.message *
    SomethingElse(greeting.message * times, greeting.const + 100)
  }

  @ApiMethod(name = "greetings.authed", path = "hellogreeting/authed")
  def authedGreeting(user: User): HelloGreeting = {
   HelloGreeting("hello " + user.getEmail)
  }
}
