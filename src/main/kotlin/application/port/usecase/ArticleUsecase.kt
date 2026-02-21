package io.ch0wdren.application.port.usecase

import io.ch0wdren.domain.Article
import io.ch0wdren.domain.CreateArticleRequest
import io.ch0wdren.domain.UpdateArticleRequest

data class ArticleListResult(
  val articles: List<Article>,
  val articlesCount: Int,
)

interface ArticleUsecase {
  suspend fun listArticles(
    currentUserEmail: String?,
    tag: String?,
    author: String?,
    favoritedBy: String?,
    limit: Int,
    offset: Int,
  ): Result<ArticleListResult>

  suspend fun getFeed(
    currentUserEmail: String,
    limit: Int,
    offset: Int,
  ): Result<ArticleListResult>

  suspend fun getArticle(
    slug: String,
    currentUserEmail: String?,
  ): Result<Article>

  suspend fun createArticle(
    currentUserEmail: String,
    request: CreateArticleRequest,
  ): Result<Article>

  suspend fun updateArticle(
    slug: String,
    currentUserEmail: String,
    request: UpdateArticleRequest,
  ): Result<Article>

  suspend fun deleteArticle(
    slug: String,
    currentUserEmail: String,
  ): Result<Unit>

  suspend fun favoriteArticle(
    slug: String,
    currentUserEmail: String,
  ): Result<Article>

  suspend fun unfavoriteArticle(
    slug: String,
    currentUserEmail: String,
  ): Result<Article>
}
