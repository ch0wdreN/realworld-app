package io.ch0wdren.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Article(
  val slug: String,
  val title: String,
  val description: String,
  val body: String,
  val tagList: List<String>,
  val createdAt: Instant,
  val updatedAt: Instant,
  val favorited: Boolean,
  val favoritesCount: Int,
  val author: Profile,
)

@Serializable
data class CreateArticleRequest(
  val title: String,
  val description: String,
  val body: String,
  val tagList: List<String>? = emptyList(),
)

@Serializable
data class UpdateArticleRequest(
  val title: String? = null,
  val description: String? = null,
  val body: String? = null,
)
