package com.geoevent

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spray.json._
import java.io.File
import scala.io.Source

class OpenAPISpecTest extends AnyFlatSpec with Matchers {

  // Load OpenAPI specification
  val openAPISpecFile = new File("/home/johan/dev/Geoevent-api/openapi.json")
  val openAPISpecSource = Source.fromFile(openAPISpecFile)
  val openAPISpecJson = try {
    openAPISpecSource.mkString.parseJson.asJsObject
  } finally {
    openAPISpecSource.close()
  }

  // Defined expected routes
  val expectedRoutes = Set(
    "/auth",
    "/users",
    "/users/{id}",
    "/geoevents",
    "/geoevents/{id}",
    "/geostamps",
    "/geostamps/{id}",
    "/msg",
    "/msg/{id}"
  )

  // Defined expected HTTP methods
  val expectedMethodsPerRoute = Map(
    "/auth" -> Set("post"),
    "/users" -> Set("post"),
    "/users/{id}" -> Set("get", "put", "delete"),
    "/geoevents" -> Set("get", "post"),
    "/geoevents/{id}" -> Set("get", "delete"),
    "/geostamps" -> Set("get", "post"),
    "/geostamps/{id}" -> Set("get", "put", "delete"),
    "/msg" -> Set("get", "post", "put"),
    "/msg/{id}" -> Set("delete")
  )

  "OpenAPI Specification" should "have the correct number of paths" in {
    val paths = openAPISpecJson.fields.get("paths").get.asJsObject.fields.keySet
    paths.size shouldBe expectedRoutes.size
    paths shouldBe expectedRoutes
  }

  it should "have correct HTTP methods for each route" in {
    val paths = openAPISpecJson.fields.get("paths").get.asJsObject.fields

    expectedMethodsPerRoute.foreach { case (route, expectedMethods) =>
      withClue(s"Route $route: ") {
        val routeObj = paths.get(route).get.asJsObject.fields.keySet.map(_.toLowerCase)
        routeObj shouldBe expectedMethods
      }
    }
  }

  it should "have a valid OpenAPI version" in {
    val openAPIVersion = openAPISpecJson.fields.get("openapi").get.toString().replace("\"", "")
    openAPIVersion should startWith("3.0")
  }

  it should "have basic info section" in {
    val info = openAPISpecJson.fields.get("info").get.asJsObject
    info.fields.get("title") shouldBe defined
    info.fields.get("version") shouldBe defined
    info.fields.get("description") shouldBe defined
  }

  it should "have a security scheme defined" in {
    val components = openAPISpecJson.fields.get("components").get.asJsObject
    val securitySchemes = components.fields.get("securitySchemes").get.asJsObject
    securitySchemes.fields.get("bearerAuth") shouldBe defined
  }

  it should "have security requirements" in {
    val security = openAPISpecJson.fields.get("security").get.asInstanceOf[JsArray]
    security.elements.size shouldBe 1
  }

  // Optional comprehensive route validation
  it should "validate each route has proper response codes" in {
    val paths = openAPISpecJson.fields.get("paths").get.asJsObject.fields

    paths.foreach { case (route, routeObj) =>
      val methods = routeObj.asJsObject.fields
      methods.foreach { case (method, methodDetails) =>
        withClue(s"Route $route with method $method: ") {
          val responses = methodDetails.asJsObject.fields.get("responses").get.asJsObject.fields
          
          // Check for at least one 2xx success code and one error code
          val successCodes = responses.keys.filter(_.startsWith("2"))
          val errorCodes = responses.keys.filter(_.startsWith("4"))

          successCodes.nonEmpty shouldBe true
          errorCodes.nonEmpty shouldBe true
        }
      }
    }
  }
}