package io.ch0wdren.infrastructure.ktor

import io.ch0wdren.domain.User
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
  routing {
    route("/api") {
      userRoute()
    }
  }
}

fun Route.userRoute() {
  route("/user") {
    get {
      call.respond(
        User(
          "hoge@example.com",
          "hoge",
          "i am hoge",
          "https://image.example.com",
        ),
      )
    }
    put {
    }
  }
}
