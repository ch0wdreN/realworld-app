package io.ch0wdren.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
  val email: String,
  @SerialName("username")
  val userName: String,
  val password: String,
  val bio: String?,
  val image: String?,
  val token: String?,
  @SerialName("createdAt")
  val createdAt: Instant,
  @SerialName("updatedAt")
  val updatedAt: Instant,
)

@Serializable
data class UserResponse(
  val email: String,
  @SerialName("username")
  val userName: String,
  val bio: String?,
  val image: String?,
)

@Serializable
data class UserWithToken(
  val email: String,
  val token: String,
  @SerialName("username")
  val userName: String,
  val bio: String?,
  val image: String?,
)
