package io.ch0wdren.infrastructure.ktor.handler

import io.ch0wdren.application.service.CommentServiceFactory
import io.ch0wdren.domain.Comment
import io.ch0wdren.domain.CreateCommentRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
  val comment: Comment,
)

@Serializable
data class CommentsResponse(
  val comments: List<Comment>,
)

@Serializable
data class CreateCommentPayload(
  val comment: CreateCommentRequest,
)

class CommentHandler(
  private val commentServiceFactory: CommentServiceFactory,
) {
  suspend fun getComments(call: ApplicationCall) {
    val service =
      commentServiceFactory.createCommentService()

    val slug = call.parameters["slug"]
    if (slug == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail =
      principal?.payload?.getClaim("email")?.asString()

    val comments =
      service
        .getComments(slug, currentUserEmail)
        .getOrElse {
          call.respond(
            HttpStatusCode.UnprocessableEntity,
            mapOf(
              "errors" to
                mapOf("body" to listOf(it.message)),
            ),
          )
          return
        }

    call.respond(
      HttpStatusCode.OK,
      CommentsResponse(comments),
    )
  }

  suspend fun createComment(call: ApplicationCall) {
    val service =
      commentServiceFactory.createCommentService()

    val slug = call.parameters["slug"]
    if (slug == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail =
      principal?.payload?.getClaim("email")?.asString()

    if (currentUserEmail == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    val payload = call.receive<CreateCommentPayload>()

    val comment =
      service
        .createComment(
          slug,
          currentUserEmail,
          payload.comment,
        ).getOrElse {
          call.respond(
            HttpStatusCode.UnprocessableEntity,
            mapOf(
              "errors" to
                mapOf("body" to listOf(it.message)),
            ),
          )
          return
        }

    call.respond(
      HttpStatusCode.Created,
      CommentResponse(comment),
    )
  }

  suspend fun deleteComment(call: ApplicationCall) {
    val service =
      commentServiceFactory.createCommentService()

    val slug = call.parameters["slug"]
    if (slug == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val commentId =
      call.parameters["id"]?.toIntOrNull()
    if (commentId == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail =
      principal?.payload?.getClaim("email")?.asString()

    if (currentUserEmail == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    service
      .deleteComment(slug, commentId, currentUserEmail)
      .getOrElse {
        val status =
          if (it.message?.contains("Forbidden") == true) {
            HttpStatusCode.Forbidden
          } else {
            HttpStatusCode.UnprocessableEntity
          }
        call.respond(
          status,
          mapOf(
            "errors" to
              mapOf("body" to listOf(it.message)),
          ),
        )
        return
      }

    call.respond(HttpStatusCode.OK)
  }
}
