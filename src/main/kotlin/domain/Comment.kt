package io.ch0wdren.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
  val id: Int,
  val body: String,
  val createdAt: Instant,
  val updatedAt: Instant,
  val author: Profile,
)

@Serializable
data class CreateCommentRequest(
  val body: String,
)
