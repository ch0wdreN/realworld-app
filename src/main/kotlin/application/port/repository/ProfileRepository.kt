package io.ch0wdren.application.port.repository

import io.ch0wdren.domain.Profile

interface ProfileRepository {
  suspend fun getProfile(
    conn: Connection,
    username: String,
    currentUserEmail: String?,
  ): Result<Profile?>

  suspend fun followUser(
    conn: Connection,
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile>

  suspend fun unfollowUser(
    conn: Connection,
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile>
}
