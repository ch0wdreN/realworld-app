package io.ch0wdren.infrastructure.ktor.handler

import io.ch0wdren.application.service.TagServiceFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class TagsResponse(
  val tags: List<String>,
)

class TagHandler(
  private val tagServiceFactory: TagServiceFactory,
) {
  suspend fun getTags(call: ApplicationCall) {
    val service = tagServiceFactory.createTagService()

    val tags =
      service.getAllTags().getOrElse {
        call.respond(
          HttpStatusCode.InternalServerError,
          mapOf("errors" to mapOf("body" to listOf(it.message))),
        )
        return
      }

    call.respond(HttpStatusCode.OK, TagsResponse(tags))
  }
}
