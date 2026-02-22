package io.ch0wdren.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Article(
  val slug: String,
  val title: String,
  val description: String,
  val body: String,
  @SerialName("tagList")
  val tagList: List<String>,
  @SerialName("createdAt")
  val createdAt: Instant,
  @SerialName("updatedAt")
  val updatedAt: Instant,
  val favorited: Boolean,
  @SerialName("favoritesCount")
  val favoritesCount: Int,
  val author: Profile,
)

@Serializable
data class CreateArticleRequest(
  val title: String,
  val description: String,
  val body: String,
  @SerialName("tagList")
  val tagList: List<String>? = emptyList(),
)

@Serializable
data class UpdateArticleRequest(
  val title: String? = null,
  val description: String? = null,
  val body: String? = null,
)
