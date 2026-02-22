package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.Row
import io.ch0wdren.application.port.repository.UserRepository
import io.ch0wdren.domain.User
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.StatusCode
import kotlinx.datetime.Instant

class UserRepositoryImpl : UserRepository {
  private val tracer = GlobalOpenTelemetry.getTracer("io.ch0wdren.UserRepository")
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
      { row -> mapRow<User>(row) },
      Parameter("$1", email),
    )

  override suspend fun findByEmail(
    conn: Connection,
    email: String,
  ): Result<User?> {
    val span = tracer.spanBuilder("UserRepository.findByEmail").startSpan()
    return try {
      span.setAttribute("user.email", email)
      span.setAttribute("sql.query", "SELECT * FROM public.user WHERE email = $1")
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
        { row -> mapRow<User>(row) },
        Parameter("$1", email),
      )
    } catch (e: Exception) {
      span.setStatus(StatusCode.ERROR, "Exception in findByEmail: ${e.message}")
      throw e
    } finally {
      span.end()
    }
  }

  override suspend fun findByUsername(
    conn: Connection,
    username: String,
  ): Result<User?> {
    val span = tracer.spanBuilder("UserRepository.findByUsername").startSpan()
    return try {
      span.setAttribute("user.username", username)
      span.setAttribute("sql.query", "SELECT * FROM public.user WHERE user_name = $1")
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
        { row -> mapRow<User>(row) },
        Parameter("$1", username),
      )
    } catch (e: Exception) {
      span.setStatus(StatusCode.ERROR, "Exception in findByUsername: ${e.message}")
      throw e
    } finally {
      span.end()
    }
  }

  override suspend fun createUser(
    conn: Connection,
    email: String,
    userName: String,
    password: String,
  ): Result<User> {
    val span = tracer.spanBuilder("UserRepository.createUser").startSpan()
    return try {
      span.setAttribute("user.email", email)
      span.setAttribute("user.username", userName)
      span.setAttribute("sql.query", "INSERT INTO public.user (email, user_name, password, ...) VALUES ($1, $2, $3, ...)")
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
        { row -> mapRow<User>(row) },
        Parameter("$1", email),
        Parameter("$2", userName),
        Parameter("$3", password),
      )
    } catch (e: Exception) {
      span.setStatus(StatusCode.ERROR, "Exception in createUser: ${e.message}")
      throw e
    } finally {
      span.end()
    }
  }

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
      { row -> mapRow<User>(row) },
      Parameter("$1", email),
      Parameter("$2", userName),
      Parameter("$3", bio),
      Parameter("$4", image),
      Parameter("$5", password),
    )
}
