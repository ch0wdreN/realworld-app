package io.ch0wdren.application.service

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.UserRepository
import io.ch0wdren.application.port.usecase.UserUsecase
import io.ch0wdren.domain.User

class UserService(
  private val conn: Connection,
  private val repo: UserRepository,
) : UserUsecase {
  override suspend fun getUser(email: String): Result<User> =
    repo.getUser(conn, email)
}
