package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.Row
import io.ch0wdren.application.port.repository.UserRepository
import io.ch0wdren.domain.User
import kotlinx.datetime.Instant

class UserRepositoryImpl : UserRepository {
  override suspend fun getUser(
    conn: Connection,
    email: String,
  ): Result<User> =
    conn.queryRow(
      """
      SELECT
        email,
        user_name,
        password,
        bio,
        image,
        token,
        created_at,
        updated_at
      FROM
        public.user
      WHERE
        email = $1
      """.trimIndent(),
      ::mapRowToUser,
      Parameter("$1", email),
    )

  override suspend fun findByEmail(
    conn: Connection,
    email: String,
  ): Result<User?> =
    conn.queryRowOrNull(
      """
      SELECT
        email,
        user_name,
        password,
        bio,
        image,
        token,
        created_at,
        updated_at
      FROM
        public.user
      WHERE
        email = $1
      """.trimIndent(),
      ::mapRowToUser,
      Parameter("$1", email),
    )

  override suspend fun findByUsername(
    conn: Connection,
    username: String,
  ): Result<User?> =
    conn.queryRowOrNull(
      """
      SELECT
        email,
        user_name,
        password,
        bio,
        image,
        token,
        created_at,
        updated_at
      FROM
        public.user
      WHERE
        user_name = $1
      """.trimIndent(),
      ::mapRowToUser,
      Parameter("$1", username),
    )

  override suspend fun createUser(
    conn: Connection,
    email: String,
    userName: String,
    password: String,
  ): Result<User> =
    conn.queryRow(
      """
      INSERT INTO public.user (email, user_name, password, bio, image, token, created_at, updated_at)
      VALUES ($1, $2, $3, NULL, NULL, NULL, NOW(), NOW())
      RETURNING
        email,
        user_name,
        password,
        bio,
        image,
        token,
        created_at,
        updated_at
      """.trimIndent(),
      ::mapRowToUser,
      Parameter("$1", email),
      Parameter("$2", userName),
      Parameter("$3", password),
    )

  override suspend fun updateUser(
    conn: Connection,
    email: String,
    userName: String?,
    bio: String?,
    image: String?,
    password: String?,
  ): Result<User> =
    conn.queryRow(
      """
      UPDATE public.user
      SET
        user_name = COALESCE($2, user_name),
        bio = COALESCE($3, bio),
        image = COALESCE($4, image),
        password = COALESCE($5, password),
        updated_at = NOW()
      WHERE
        email = $1
      RETURNING
        email,
        user_name,
        password,
        bio,
        image,
        token,
        created_at,
        updated_at
      """.trimIndent(),
      ::mapRowToUser,
      Parameter("$1", email),
      Parameter("$2", userName),
      Parameter("$3", bio),
      Parameter("$4", image),
      Parameter("$5", password),
    )

  private fun mapRowToUser(row: Row): User =
    User(
      email = row.get("email", String::class.java)!!,
      userName = row.get("user_name", String::class.java)!!,
      password = row.get("password", String::class.java)!!,
      bio = row.get("bio", String::class.java),
      image = row.get("image", String::class.java),
      token = row.get("token", String::class.java),
      createdAt = Instant.parse(row.get("created_at", String::class.java)!!),
      updatedAt = Instant.parse(row.get("updated_at", String::class.java)!!),
    )
}
