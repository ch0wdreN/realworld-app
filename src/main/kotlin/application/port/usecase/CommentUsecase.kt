package io.ch0wdren.application.port.usecase

import io.ch0wdren.domain.Comment
import io.ch0wdren.domain.CreateCommentRequest

interface CommentUsecase {
  suspend fun getComments(
    articleSlug: String,
    currentUserEmail: String?,
  ): Result<List<Comment>>

  suspend fun createComment(
    articleSlug: String,
    authorEmail: String,
    request: CreateCommentRequest,
  ): Result<Comment>

  suspend fun deleteComment(
    articleSlug: String,
    commentId: Int,
    userEmail: String,
  ): Result<Unit>
}
