package io.ch0wdren.application.port.repository

import io.ch0wdren.domain.Comment

interface CommentRepository {
  suspend fun getComments(
    conn: Connection,
    articleSlug: String,
    currentUserEmail: String?,
  ): Result<List<Comment>>

  suspend fun createComment(
    conn: Connection,
    articleSlug: String,
    authorEmail: String,
    body: String,
  ): Result<Comment>

  suspend fun deleteComment(
    conn: Connection,
    commentId: Int,
    userEmail: String,
  ): Result<Unit>
}
