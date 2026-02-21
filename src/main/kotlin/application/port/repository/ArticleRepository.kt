package io.ch0wdren.application.port.repository

import io.ch0wdren.domain.Article

interface ArticleRepository {
  suspend fun listArticles(
    conn: Connection,
    currentUserEmail: String?,
    tag: String?,
    author: String?,
    favoritedBy: String?,
    limit: Int,
    offset: Int,
  ): Result<List<Article>>

  suspend fun countArticles(
    conn: Connection,
    tag: String?,
    author: String?,
    favoritedBy: String?,
  ): Result<Int>

  suspend fun getFeed(
    conn: Connection,
    currentUserEmail: String,
    limit: Int,
    offset: Int,
  ): Result<List<Article>>

  suspend fun countFeed(
    conn: Connection,
    currentUserEmail: String,
  ): Result<Int>

  suspend fun getArticle(
    conn: Connection,
    slug: String,
    currentUserEmail: String?,
  ): Result<Article?>

  suspend fun createArticle(
    conn: Connection,
    slug: String,
    title: String,
    description: String,
    body: String,
    authorEmail: String,
    tagList: List<String>,
  ): Result<Article>

  suspend fun updateArticle(
    conn: Connection,
    slug: String,
    title: String?,
    description: String?,
    body: String?,
    currentUserEmail: String?,
  ): Result<Article>

  suspend fun deleteArticle(
    conn: Connection,
    slug: String,
  ): Result<Unit>

  suspend fun favoriteArticle(
    conn: Connection,
    slug: String,
    userEmail: String,
  ): Result<Article>

  suspend fun unfavoriteArticle(
    conn: Connection,
    slug: String,
    userEmail: String,
  ): Result<Article>
}
