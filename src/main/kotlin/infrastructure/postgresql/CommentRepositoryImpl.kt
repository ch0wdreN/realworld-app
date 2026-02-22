package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.CommentRepository
import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.Row
import io.ch0wdren.domain.Comment
import io.ch0wdren.domain.Profile
import kotlinx.datetime.Instant

class CommentRepositoryImpl : CommentRepository {
  override suspend fun getComments(
    conn: Connection,
    articleSlug: String,
    currentUserEmail: String?,
  ): Result<List<Comment>> =
    conn.query(
      """
      SELECT
        c.id,
        c.body,
        c.created_at,
        c.updated_at,
        u.user_name,
        u.bio,
        u.image,
        CASE
          WHEN fl.follower_email IS NOT NULL
            THEN TRUE
          ELSE FALSE
        END AS following
      FROM public.comment c
      JOIN public.user u
        ON u.email = c.author_email
      LEFT JOIN public.follow fl
        ON fl.followee_email = c.author_email
        AND fl.follower_email = $2
      WHERE c.article_slug = $1
      ORDER BY c.created_at ASC
      """.trimIndent(),
      ::mapRowToComment,
      Parameter("$1", articleSlug),
      Parameter("$2", currentUserEmail),
    )

  override suspend fun createComment(
    conn: Connection,
    articleSlug: String,
    authorEmail: String,
    body: String,
  ): Result<Comment> {
     val commentId =
       conn
         .queryRow(
           """
           INSERT INTO public.comment
             (body, article_slug, author_email,
              created_at, updated_at)
           VALUES ($1, $2, $3, NOW(), NOW())
           RETURNING id
           """.trimIndent(),
           { row -> row.get("id", Int::class.java)!! },
           Parameter("$1", body),
           Parameter("$2", articleSlug),
           Parameter("$3", authorEmail),
         ).getOrElse {
           return Result.failure(it)
         }

    return conn.queryRowOrNull(
      """
      SELECT
        c.id,
        c.body,
        c.created_at,
        c.updated_at,
        u.user_name,
        u.bio,
        u.image,
        FALSE AS following
      FROM public.comment c
      JOIN public.user u
        ON u.email = c.author_email
      WHERE c.id = $1
      """.trimIndent(),
      ::mapRowToComment,
      Parameter("$1", commentId),
    ).let { result ->
      result.getOrElse {
        return Result.failure(it)
      }?.let { Result.success(it) }
        ?: Result.failure(Exception("Comment not found"))
    }
  }

  override suspend fun deleteComment(
    conn: Connection,
    commentId: Int,
    userEmail: String,
  ): Result<Unit> {
     val authorEmail =
       conn
         .queryRowOrNull(
           """
           SELECT author_email
           FROM public.comment
           WHERE id = $1
           """.trimIndent(),
           { row ->
             row.get("author_email", String::class.java)!!
           },
           Parameter("$1", commentId),
         ).getOrElse {
           return Result.failure(it)
         }
         ?: return Result.failure(
           Exception("Comment not found"),
         )

    if (authorEmail != userEmail) {
      return Result.failure(
        Exception("Forbidden: not the comment author"),
      )
    }

    return conn.execute(
      """
      DELETE FROM public.comment
      WHERE id = $1
      """.trimIndent(),
      Parameter("$1", commentId),
    )
  }

    private fun mapRowToComment(row: Row): Comment =
      Comment(
        id = row.get("id", Int::class.java)!!,
        body = row.get("body", String::class.java)!!,
        createdAt =
          Instant.parse(
            row.get("created_at", String::class.java)!!.replace(" ", "T"),
          ),
        updatedAt =
          Instant.parse(
            row.get("updated_at", String::class.java)!!.replace(" ", "T"),
          ),
        author =
          Profile(
            userName =
              row.get("user_name", String::class.java)!!,
            bio = row.get("bio", String::class.java),
            image = row.get("image", String::class.java),
            following =
              row.get("following", Boolean::class.java)!!,
          ),
      )
}
