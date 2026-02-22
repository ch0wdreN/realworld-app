package io.ch0wdren.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
  val id: Int,
  val body: String,
  @SerialName("createdAt")
  val createdAt: Instant,
  @SerialName("updatedAt")
  val updatedAt: Instant,
  val author: Profile,
)

@Serializable
data class CreateCommentRequest(
  val body: String,
)
