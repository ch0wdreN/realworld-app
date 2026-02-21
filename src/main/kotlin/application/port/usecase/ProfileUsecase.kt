package io.ch0wdren.application.port.usecase

import io.ch0wdren.domain.Profile

interface ProfileUsecase {
  suspend fun getProfile(
    username: String,
    currentUserEmail: String?,
  ): Result<Profile>

  suspend fun followUser(
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile>

  suspend fun unfollowUser(
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile>
}
