package io.ch0wdren.infrastructure.ktor.handler

import io.ch0wdren.application.service.UserServiceFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

class UserHandler(
  private val userServiceFactory: UserServiceFactory,
) {
  suspend fun getUser(call: ApplicationCall) {
    val service = userServiceFactory.createUserService()

    val email =
      call.request.queryParameters["email"] ?: run {
        call.respond(HttpStatusCode.BadRequest)
        return
      }
    val user =
      service.getUser(email).getOrElse {
        call.respond(HttpStatusCode.InternalServerError)
      }

    call.respond(HttpStatusCode.OK, user)
  }
}
