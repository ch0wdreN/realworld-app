package io.ch0wdren.application.port.repository

import io.ch0wdren.domain.User

interface UserRepository {
  suspend fun getUser(
    conn: Connection,
    email: String,
  ): Result<User>

  suspend fun findByEmail(
    conn: Connection,
    email: String,
  ): Result<User?>

  suspend fun findByUsername(
    conn: Connection,
    username: String,
  ): Result<User?>

  suspend fun createUser(
    conn: Connection,
    email: String,
    userName: String,
    password: String,
  ): Result<User>

  suspend fun updateUser(
    conn: Connection,
    email: String,
    userName: String?,
    bio: String?,
    image: String?,
    password: String?,
  ): Result<User>
}
