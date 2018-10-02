package org.broadinstitute.dsde.workbench.avram.api

import java.io.BufferedReader
import java.security.Principal
import java.util
import java.util.Locale

import javax.servlet.http._
import javax.servlet._

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Stub implementations of header functions for HttpServletRequest.
  */
trait StubbedHeaders {
  def headers: Map[String, List[String]]

  def getHeader(name: String): String = {
    Try(headers.filterKeys(_.equalsIgnoreCase(name))
      .foldLeft(List.empty[String])((acc, kv) => acc ++ kv._2).head).getOrElse(null)
  }

  def getHeaders(name: String): util.Enumeration[_] =
    headers.filterKeys(_.equalsIgnoreCase(name))
      .foldLeft(List.empty[String])((acc, kv) => acc ++ kv._2).iterator.asJavaEnumeration

  def getHeaderNames: util.Enumeration[_] = headers.keys.iterator.asJavaEnumeration
}

/**
  * Concrete implementation of HttpServletRequest that can be instantiated for tests. All properties
  * have default values so they can be named and set independently.
  *
  * All stub/mock implementations are in mixed-in traits. The result is that you can always delete
  * the body of this class and have the IDE fill in default implementations for anything that isn't
  * stubbed/mocked.
  *
  * @param headers HTTP headers
  */
case class FakeHttpServletRequest(headers: Map[String, List[String]] = Map.empty)
  extends HttpServletRequest with StubbedHeaders {

  override def getAuthType: String = ???

  override def getCookies: Array[Cookie] = ???

  override def getDateHeader(name: String): Long = ???

  override def getIntHeader(name: String): Int = ???

  override def getMethod: String = ???

  override def getPathInfo: String = ???

  override def getPathTranslated: String = ???

  override def getContextPath: String = ???

  override def getQueryString: String = ???

  override def getRemoteUser: String = ???

  override def isUserInRole(role: String): Boolean = ???

  override def getUserPrincipal: Principal = ???

  override def getRequestedSessionId: String = ???

  override def getRequestURI: String = ???

  override def getRequestURL: StringBuffer = ???

  override def getServletPath: String = ???

  override def getSession(create: Boolean): HttpSession = ???

  override def getSession: HttpSession = ???

  override def isRequestedSessionIdValid: Boolean = ???

  override def isRequestedSessionIdFromCookie: Boolean = ???

  override def isRequestedSessionIdFromURL: Boolean = ???

  override def isRequestedSessionIdFromUrl: Boolean = ???

  override def getAttribute(name: String): AnyRef = ???

  override def getAttributeNames: util.Enumeration[_] = ???

  override def getCharacterEncoding: String = ???

  override def setCharacterEncoding(env: String): Unit = ???

  override def getContentLength: Int = ???

  override def getContentType: String = ???

  override def getInputStream: ServletInputStream = ???

  override def getParameter(name: String): String = ???

  override def getParameterNames: util.Enumeration[_] = ???

  override def getParameterValues(name: String): Array[String] = ???

  override def getParameterMap: util.Map[_, _] = ???

  override def getProtocol: String = ???

  override def getScheme: String = ???

  override def getServerName: String = ???

  override def getServerPort: Int = ???

  override def getReader: BufferedReader = ???

  override def getRemoteAddr: String = ???

  override def getRemoteHost: String = ???

  override def setAttribute(name: String, o: Any): Unit = ???

  override def removeAttribute(name: String): Unit = ???

  override def getLocale: Locale = ???

  override def getLocales: util.Enumeration[_] = ???

  override def isSecure: Boolean = ???

  override def getRequestDispatcher(path: String): RequestDispatcher = ???

  override def getRealPath(path: String): String = ???

  override def getRemotePort: Int = ???

  override def getLocalName: String = ???

  override def getLocalAddr: String = ???

  override def getLocalPort: Int = ???
}