package io.ch0wdren.domain

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
  val userName: String,
  val bio: String?,
  val image: String?,
  val following: Boolean,
)
