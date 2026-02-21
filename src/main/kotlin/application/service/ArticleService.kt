package io.ch0wdren.application.service

import io.ch0wdren.application.UnitOfWork
import io.ch0wdren.application.port.repository.ArticleRepository
import io.ch0wdren.application.port.usecase.ArticleListResult
import io.ch0wdren.application.port.usecase.ArticleUsecase
import io.ch0wdren.domain.Article
import io.ch0wdren.domain.CreateArticleRequest
import io.ch0wdren.domain.UpdateArticleRequest
import io.ch0wdren.infrastructure.postgresql.ArticleRepositoryImpl

class ArticleService(
  private val unitOfWork: UnitOfWork,
  private val repo: ArticleRepository,
) : ArticleUsecase {
  override suspend fun listArticles(
    currentUserEmail: String?,
    tag: String?,
    author: String?,
    favoritedBy: String?,
    limit: Int,
    offset: Int,
  ): Result<ArticleListResult> =
    unitOfWork.transactional { conn ->
      val articles =
        repo
          .listArticles(
            conn,
            currentUserEmail,
            tag,
            author,
            favoritedBy,
            limit,
            offset,
          ).getOrElse {
            return@transactional Result.failure(it)
          }

      val count =
        repo
          .countArticles(conn, tag, author, favoritedBy)
          .getOrElse {
            return@transactional Result.failure(it)
          }

      Result.success(
        ArticleListResult(
          articles = articles,
          articlesCount = count,
        ),
      )
    }

  override suspend fun getFeed(
    currentUserEmail: String,
    limit: Int,
    offset: Int,
  ): Result<ArticleListResult> =
    unitOfWork.transactional { conn ->
      val articles =
        repo
          .getFeed(conn, currentUserEmail, limit, offset)
          .getOrElse {
            return@transactional Result.failure(it)
          }

      val count =
        repo.countFeed(conn, currentUserEmail).getOrElse {
          return@transactional Result.failure(it)
        }

      Result.success(
        ArticleListResult(
          articles = articles,
          articlesCount = count,
        ),
      )
    }

  override suspend fun getArticle(
    slug: String,
    currentUserEmail: String?,
  ): Result<Article> =
    unitOfWork.transactional { conn ->
      val article =
        repo
          .getArticle(conn, slug, currentUserEmail)
          .getOrElse {
            return@transactional Result.failure(it)
          }
          ?: return@transactional Result.failure(
            Exception("Article not found"),
          )

      Result.success(article)
    }

  override suspend fun createArticle(
    currentUserEmail: String,
    request: CreateArticleRequest,
  ): Result<Article> =
    unitOfWork.transactional { conn ->
      val slug =
        ArticleRepositoryImpl.generateSlug(request.title)
      val tagList = request.tagList ?: emptyList()

      repo.createArticle(
        conn,
        slug,
        request.title,
        request.description,
        request.body,
        currentUserEmail,
        tagList,
      )
    }

  override suspend fun updateArticle(
    slug: String,
    currentUserEmail: String,
    request: UpdateArticleRequest,
  ): Result<Article> =
    unitOfWork.transactional { conn ->
      val article =
        repo
          .getArticle(conn, slug, currentUserEmail)
          .getOrElse {
            return@transactional Result.failure(it)
          }
          ?: return@transactional Result.failure(
            Exception("Article not found"),
          )

      repo.updateArticle(
        conn,
        slug,
        request.title,
        request.description,
        request.body,
        currentUserEmail,
      )
    }

  override suspend fun deleteArticle(
    slug: String,
    currentUserEmail: String,
  ): Result<Unit> =
    unitOfWork.transactional { conn ->
      val article =
        repo
          .getArticle(conn, slug, currentUserEmail)
          .getOrElse {
            return@transactional Result.failure(it)
          }
          ?: return@transactional Result.failure(
            Exception("Article not found"),
          )

      repo.deleteArticle(conn, slug)
    }

  override suspend fun favoriteArticle(
    slug: String,
    currentUserEmail: String,
  ): Result<Article> =
    unitOfWork.transactional { conn ->
      repo.favoriteArticle(conn, slug, currentUserEmail)
    }

  override suspend fun unfavoriteArticle(
    slug: String,
    currentUserEmail: String,
  ): Result<Article> =
    unitOfWork.transactional { conn ->
      repo.unfavoriteArticle(conn, slug, currentUserEmail)
    }
}
