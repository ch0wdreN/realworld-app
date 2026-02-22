package io.ch0wdren.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
  @SerialName("username")
  val userName: String,
  val bio: String?,
  val image: String?,
  val following: Boolean,
)
