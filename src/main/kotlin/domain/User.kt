package io.ch0wdren.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
  val email: String,
  val userName: String,
  val password: String,
  val bio: String?,
  val image: String?,
  val token: String?,
  val createdAt: Instant,
  val updatedAt: Instant,
)

@Serializable
data class UserResponse(
  val email: String,
  val userName: String,
  val bio: String?,
  val image: String?,
)

@Serializable
data class UserWithToken(
  val email: String,
  val token: String,
  val userName: String,
  val bio: String?,
  val image: String?,
)
