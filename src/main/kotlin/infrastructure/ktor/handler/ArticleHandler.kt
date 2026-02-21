package io.ch0wdren.infrastructure.ktor.handler

import io.ch0wdren.application.service.ArticleServiceFactory
import io.ch0wdren.domain.CreateArticleRequest
import io.ch0wdren.domain.UpdateArticleRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class ArticleResponse<T>(
  val article: T,
)

@Serializable
data class ArticlesResponse<T>(
  val articles: List<T>,
  val articlesCount: Int,
)

@Serializable
data class CreateArticlePayload(
  val article: CreateArticleRequest,
)

@Serializable
data class UpdateArticlePayload(
  val article: UpdateArticleRequest,
)

class ArticleHandler(
  private val articleServiceFactory: ArticleServiceFactory,
) {
  suspend fun listArticles(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail =
      principal?.payload?.getClaim("email")?.asString()

    val tag = call.request.queryParameters["tag"]
    val author = call.request.queryParameters["author"]
    val favorited =
      call.request.queryParameters["favorited"]
    val limit =
      call.request.queryParameters["limit"]
        ?.toIntOrNull() ?: 20
    val offset =
      call.request.queryParameters["offset"]
        ?.toIntOrNull() ?: 0

    val result =
      service
        .listArticles(
          currentUserEmail,
          tag,
          author,
          favorited,
          limit,
          offset,
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
      HttpStatusCode.OK,
      ArticlesResponse(
        articles = result.articles,
        articlesCount = result.articlesCount,
      ),
    )
  }

  suspend fun getFeed(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail =
      principal?.payload?.getClaim("email")?.asString()

    if (currentUserEmail == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    val limit =
      call.request.queryParameters["limit"]
        ?.toIntOrNull() ?: 20
    val offset =
      call.request.queryParameters["offset"]
        ?.toIntOrNull() ?: 0

    val result =
      service
        .getFeed(currentUserEmail, limit, offset)
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
      ArticlesResponse(
        articles = result.articles,
        articlesCount = result.articlesCount,
      ),
    )
  }

  suspend fun getArticle(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()

    val slug = call.parameters["slug"]
    if (slug == null) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail =
      principal?.payload?.getClaim("email")?.asString()

    val article =
      service
        .getArticle(slug, currentUserEmail)
        .getOrElse {
          call.respond(
            HttpStatusCode.NotFound,
            mapOf(
              "errors" to
                mapOf("body" to listOf(it.message)),
            ),
          )
          return
        }

    call.respond(
      HttpStatusCode.OK,
      ArticleResponse(article),
    )
  }

  suspend fun createArticle(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()

    val principal = call.principal<JWTPrincipal>()
    val currentUserEmail =
      principal?.payload?.getClaim("email")?.asString()

    if (currentUserEmail == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return
    }

    val payload = call.receive<CreateArticlePayload>()

    val article =
      service
        .createArticle(currentUserEmail, payload.article)
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
      HttpStatusCode.Created,
      ArticleResponse(article),
    )
  }

  suspend fun updateArticle(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()

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

    val payload = call.receive<UpdateArticlePayload>()

    val article =
      service
        .updateArticle(
          slug,
          currentUserEmail,
          payload.article,
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
      HttpStatusCode.OK,
      ArticleResponse(article),
    )
  }

  suspend fun deleteArticle(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()

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

    service
      .deleteArticle(slug, currentUserEmail)
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

    call.respond(HttpStatusCode.OK)
  }

  suspend fun favoriteArticle(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()
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

    val article =
      service.favoriteArticle(slug, currentUserEmail).getOrElse {
        call.respond(
          HttpStatusCode.UnprocessableEntity,
          mapOf(
            "errors" to
              mapOf("body" to listOf(it.message)),
          ),
        )
        return
      }

    call.respond(HttpStatusCode.OK, ArticleResponse(article))
  }

  suspend fun unfavoriteArticle(call: ApplicationCall) {
    val service =
      articleServiceFactory.createArticleService()
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

    val article =
      service.unfavoriteArticle(slug, currentUserEmail).getOrElse {
        call.respond(
          HttpStatusCode.UnprocessableEntity,
          mapOf(
            "errors" to
              mapOf("body" to listOf(it.message)),
          ),
        )
        return
      }

    call.respond(HttpStatusCode.OK, ArticleResponse(article))
  }
}
