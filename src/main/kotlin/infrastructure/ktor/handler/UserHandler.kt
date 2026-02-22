package io.ch0wdren.infrastructure.ktor.handler

import io.ch0wdren.application.port.usecase.LoginRequest
import io.ch0wdren.application.port.usecase.RegisterRequest
import io.ch0wdren.application.port.usecase.UpdateUserRequest
import io.ch0wdren.application.service.UserServiceFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationPayload(
  val user: RegisterRequest,
)

@Serializable
data class UserLoginPayload(
  val user: LoginRequest,
)

@Serializable
data class UserUpdatePayload(
  val user: UpdateUserRequest,
)

@Serializable
data class UserResponse<T>(
  val user: T,
)

class UserHandler(
  private val userServiceFactory: UserServiceFactory,
) {
  suspend fun register(call: ApplicationCall) {
    val service = userServiceFactory.createUserService()
    val payload = call.receive<UserRegistrationPayload>()

    val userWithToken =
      service.register(payload.user).getOrElse {
        call.respond(
          HttpStatusCode.UnprocessableEntity,
          mapOf("errors" to mapOf("body" to listOf(it.message ?: it.toString()))),
        )
        return
      }

    call.respond(HttpStatusCode.Created, UserResponse(userWithToken))
  }

  suspend fun login(call: ApplicationCall) {
    val service = userServiceFactory.createUserService()
    val payload = call.receive<UserLoginPayload>()

    val userWithToken =
      service.login(payload.user).getOrElse {
        call.respond(
          HttpStatusCode.UnprocessableEntity,
          mapOf("errors" to mapOf("body" to listOf(it.message))),
        )
        return
      }

    call.respond(HttpStatusCode.OK, UserResponse(userWithToken))
  }

  suspend fun getCurrentUser(call: ApplicationCall) {
    val service = userServiceFactory.createUserService()
    val principal = call.principal<JWTPrincipal>()
    val email = principal?.payload?.getClaim("email")?.asString()

    if (email == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    val user =
      service.getUser(email).getOrElse {
        call.respond(
          HttpStatusCode.InternalServerError,
          mapOf("errors" to mapOf("body" to listOf(it.message))),
        )
        return
      }

    call.respond(HttpStatusCode.OK, UserResponse(user))
  }

  suspend fun updateUser(call: ApplicationCall) {
    val service = userServiceFactory.createUserService()
    val principal = call.principal<JWTPrincipal>()
    val email = principal?.payload?.getClaim("email")?.asString()

    if (email == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    val payload = call.receive<UserUpdatePayload>()

    val userWithToken =
      service.updateUser(email, payload.user).getOrElse {
        call.respond(
          HttpStatusCode.UnprocessableEntity,
          mapOf("errors" to mapOf("body" to listOf(it.message))),
        )
        return
      }

    call.respond(HttpStatusCode.OK, UserResponse(userWithToken))
  }
}
