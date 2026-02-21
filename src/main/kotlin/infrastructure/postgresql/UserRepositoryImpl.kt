package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.UserRepository
import io.ch0wdren.domain.User

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
        bio,
        image
      FROM
        public.user
      WHERE
        email = $1
      """.trimIndent(),
      { row ->
        User(
          row.get("email", String::class.java),
          row.get("user_name", String::class.java),
          row.get("bio", String::class.java),
          row.get("image", String::class.java),
        )
      },
      Parameter("$1", email),
    )
}
