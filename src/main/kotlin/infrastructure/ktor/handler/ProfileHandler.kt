package io.ch0wdren.infrastructure.ktor.handler

import io.ch0wdren.application.service.ProfileServiceFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse<T>(
  val profile: T,
)

class ProfileHandler(
  private val profileServiceFactory: ProfileServiceFactory,
) {
  suspend fun getProfile(call: ApplicationCall) {
    val service = profileServiceFactory.createProfileService()
    val username = call.parameters["username"]

    if (username == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail = principal?.payload?.getClaim("email")?.asString()

    val profile =
      service.getProfile(username, currentUserEmail).getOrElse {
        call.respond(
          HttpStatusCode.NotFound,
          mapOf("errors" to mapOf("body" to listOf(it.message))),
        )
        return
      }

    call.respond(HttpStatusCode.OK, ProfileResponse(profile))
  }

  suspend fun followUser(call: ApplicationCall) {
    val service = profileServiceFactory.createProfileService()
    val username = call.parameters["username"]

    if (username == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail = principal?.payload?.getClaim("email")?.asString()

    if (currentUserEmail == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    val profile =
      service.followUser(currentUserEmail, username).getOrElse {
        call.respond(
          HttpStatusCode.UnprocessableEntity,
          mapOf("errors" to mapOf("body" to listOf(it.message))),
        )
        return
      }

    call.respond(HttpStatusCode.OK, ProfileResponse(profile))
  }

  suspend fun unfollowUser(call: ApplicationCall) {
    val service = profileServiceFactory.createProfileService()
    val username = call.parameters["username"]

    if (username == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail = principal?.payload?.getClaim("email")?.asString()

    if (currentUserEmail == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    val profile =
      service.unfollowUser(currentUserEmail, username).getOrElse {
        call.respond(
          HttpStatusCode.UnprocessableEntity,
          mapOf("errors" to mapOf("body" to listOf(it.message))),
        )
        return
      }

    call.respond(HttpStatusCode.OK, ProfileResponse(profile))
  }
}
