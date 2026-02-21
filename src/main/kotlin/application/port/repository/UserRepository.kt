package io.ch0wdren.application.port.repository

import io.ch0wdren.domain.User

interface UserRepository {
  suspend fun getUser(
    conn: Connection,
    email: String,
  ): Result<User>
}
