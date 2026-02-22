package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.ProfileRepository
import io.ch0wdren.application.port.repository.Row
import io.ch0wdren.domain.Profile

class ProfileRepositoryImpl : ProfileRepository {
  override suspend fun getProfile(
    conn: Connection,
    username: String,
    currentUserEmail: String?,
  ): Result<Profile?> =
    conn.queryRowOrNull(
      """
      SELECT
        u.user_name,
        u.bio,
        u.image,
        (f.follower_email IS NOT NULL)::boolean AS following
      FROM
        public.user u
      LEFT JOIN public.follow f ON
        f.followee_email = u.email
        AND f.follower_email = $2
      WHERE
        u.user_name = $1
      """.trimIndent(),
      { row -> mapRow<Profile>(row) },
      Parameter("$1", username),
      Parameter("$2", currentUserEmail),
    )

  override suspend fun followUser(
    conn: Connection,
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile> {
    conn
      .execute(
        """
        INSERT INTO public.follow (follower_email, followee_email)
        SELECT $1, u.email
        FROM public.user u
        WHERE u.user_name = $2
        ON CONFLICT DO NOTHING
        """.trimIndent(),
        Parameter("$1", followerEmail),
        Parameter("$2", followeeUsername),
      ).getOrElse {
        return Result.failure(it)
      }

    return conn.queryRow(
      """
      SELECT
        u.user_name,
        u.bio,
        u.image,
        TRUE AS following
      FROM
        public.user u
      WHERE
        u.user_name = $1
      """.trimIndent(),
      { row -> mapRow<Profile>(row) },
      Parameter("$1", followeeUsername),
    )
  }

  override suspend fun unfollowUser(
    conn: Connection,
    followerEmail: String,
    followeeUsername: String,
  ): Result<Profile> {
    conn
      .execute(
        """
        DELETE FROM public.follow
        WHERE follower_email = $1
        AND followee_email = (
          SELECT email FROM public.user WHERE user_name = $2
        )
        """.trimIndent(),
        Parameter("$1", followerEmail),
        Parameter("$2", followeeUsername),
      ).getOrElse {
        return Result.failure(it)
      }

    return conn.queryRow(
      """
      SELECT
        u.user_name,
        u.bio,
        u.image,
        FALSE AS following
      FROM
        public.user u
      WHERE
        u.user_name = $1
      """.trimIndent(),
      { row -> mapRow<Profile>(row) },
      Parameter("$1", followeeUsername),
    )
  }
}
