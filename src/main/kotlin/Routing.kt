package io.ch0wdren

import io.ktor.server.application.Application
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
  routing {
    get("/") {
      call.respondText("Hello, world!!!")
    }
    routing {
      swaggerUI(path = "swagger")
    }
  }
}
