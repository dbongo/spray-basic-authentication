package com.example

import spray.routing.{Directives, Route, SimpleRoutingApp}
import spray.http._
import akka.actor.{ActorRef, ActorSystem}
import spray.routing.authentication.{BasicAuth, UserPass}
import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

trait RequestTimeout {
  implicit val timeout = Timeout(5 seconds)
  implicit def executionContext: ExecutionContextExecutor
}

trait UserAuthenticator extends RequestTimeout {
  def securityService: ActorRef

  def authenticator(userPass: Option[UserPass]):Future[Option[String]] =
    (securityService ? SecurityService.Authenticate(userPass)).mapTo[Option[String]]
}

trait SecurityRoute extends Directives with RequestTimeout {
  import SecurityService.JsonMarshaller._
  def securityService: ActorRef
  def securityServiceView: ActorRef

  def securityRoute: Route = {
    pathPrefix("users") {
      path(Segment) { name =>
        get {
          complete {
            (securityServiceView ? SecurityServiceView.GetUserByName(name)).mapTo[Option[SecurityServiceView.User]]
          }
        } ~
          delete {
            complete {
              securityService ! SecurityService.DeleteUserByName(name)
              StatusCodes.NoContent
            }
          }
      } ~
        get {
          complete {
            (securityServiceView ? SecurityServiceView.GetAllUsers).mapTo[List[SecurityServiceView.User]]
          }
        } ~
        put {
          entity(as[SecurityService.User]) { user =>
            complete {
              securityService ! SecurityService.AddUser(user)
              StatusCodes.NoContent
            }
          }
        }
    }
  }
}

object Main extends App with SimpleRoutingApp with UserAuthenticator with SecurityRoute {
  implicit val system = ActorSystem("my-system")
  implicit val executionContext = system.dispatcher

  val securityService = system.actorOf(SecurityService.props, "security")
  val securityServiceView = system.actorOf(SecurityServiceView.props, "securityServiceView")

  startServer(interface = "localhost", port = 8080) {
    pathPrefix("api") {
      securityRoute
    } ~
    pathPrefix("web") {
      getFromResourceDirectory("web")
    } ~
    authenticate(BasicAuth(authenticator _, realm = "secure site")) { username =>
      path("secure") {
        complete("Welcome!")
      }
    }
  }
}
