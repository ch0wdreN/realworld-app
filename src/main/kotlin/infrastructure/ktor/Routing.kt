package io.ch0wdren.infrastructure.ktor

import io.ch0wdren.infrastructure.ktor.handler.UserHandler
import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
  val userHandler by inject<UserHandler>()
  routing {
    route("/api") {
      route("/user") {
        get {
          userHandler.getUser(call)
        }
      }
    }
  }
}
