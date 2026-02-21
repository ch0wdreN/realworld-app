package io.ch0wdren.infrastructure.ktor

import io.ch0wdren.infrastructure.ktor.handler.ArticleHandler
import io.ch0wdren.infrastructure.ktor.handler.CommentHandler
import io.ch0wdren.infrastructure.ktor.handler.ProfileHandler
import io.ch0wdren.infrastructure.ktor.handler.TagHandler
import io.ch0wdren.infrastructure.ktor.handler.UserHandler
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
  val userHandler by inject<UserHandler>()
  val profileHandler by inject<ProfileHandler>()
  val articleHandler by inject<ArticleHandler>()
  val commentHandler by inject<CommentHandler>()
  val tagHandler by inject<TagHandler>()
  routing {
    route("/api") {
      route("/users") {
        post {
          userHandler.register(call)
        }
        post("/login") {
          userHandler.login(call)
        }
      }
      authenticate("auth-jwt") {
        route("/user") {
          get {
            userHandler.getCurrentUser(call)
          }
          put {
            userHandler.updateUser(call)
          }
        }
      }
      route("/profiles/{username}") {
        get {
          profileHandler.getProfile(call)
        }
        authenticate("auth-jwt") {
          post("/follow") {
            profileHandler.followUser(call)
          }
          delete("/follow") {
            profileHandler.unfollowUser(call)
          }
        }
      }
      route("/articles") {
        get {
          articleHandler.listArticles(call)
        }
        authenticate("auth-jwt") {
          get("/feed") {
            articleHandler.getFeed(call)
          }
          post {
            articleHandler.createArticle(call)
          }
        }
        route("/{slug}") {
          get {
            articleHandler.getArticle(call)
          }
          authenticate("auth-jwt") {
            put {
              articleHandler.updateArticle(call)
            }
            delete {
              articleHandler.deleteArticle(call)
            }
          }
          route("/comments") {
            get {
              commentHandler.getComments(call)
            }
            authenticate("auth-jwt") {
              post {
                commentHandler.createComment(call)
              }
              delete("/{id}") {
                commentHandler.deleteComment(call)
              }
            }
          }
          authenticate("auth-jwt") {
            post("/favorite") {
              articleHandler.favoriteArticle(call)
            }
            delete("/favorite") {
              articleHandler.unfavoriteArticle(call)
            }
          }
        }
      }
      route("/tags") {
        get {
          tagHandler.getTags(call)
        }
      }
    }
  }
}
