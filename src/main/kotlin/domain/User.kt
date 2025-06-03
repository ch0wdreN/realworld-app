package io.ch0wdren.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
  val email: String,
  val userName: String,
  val bio: String,
  val image: String,
)
