package io.ch0wdren.application.service

import io.ch0wdren.application.UnitOfWork
import io.ch0wdren.application.port.repository.ProfileRepository
import io.ch0wdren.application.port.usecase.ProfileUsecase
import io.ch0wdren.domain.Profile

class ProfileService(
  private val unitOfWork: UnitOfWork,
  private val repo: ProfileRepository,
) : ProfileUsecase {
  override suspend fun getProfile(
    username: String,
    currentUserEmail: String?,
  ): Result<Profile> =
    unitOfWork.transactional { conn ->
      val profile =
        repo.getProfile(conn, username, currentUserEmail).getOrElse {
          return@transactional Result.failure(it)
        }
          ?: return@transactional Result.failure(
            Exception("Profile not found"),
          )

      Result.success(profile)
    }

  override suspend fun followUser(
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile> =
    unitOfWork.transactional { conn ->
      repo.followUser(conn, followerEmail, followeeUsername)
    }

  override suspend fun unfollowUser(
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile> =
    unitOfWork.transactional { conn ->
      repo.unfollowUser(conn, followerEmail, followeeUsername)
    }
}
