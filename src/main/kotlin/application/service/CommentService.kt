package io.ch0wdren.application.service

import io.ch0wdren.application.UnitOfWork
import io.ch0wdren.application.port.repository.CommentRepository
import io.ch0wdren.application.port.usecase.CommentUsecase
import io.ch0wdren.domain.Comment
import io.ch0wdren.domain.CreateCommentRequest

class CommentService(
  private val unitOfWork: UnitOfWork,
  private val repo: CommentRepository,
) : CommentUsecase {
  override suspend fun getComments(
    articleSlug: String,
    currentUserEmail: String?,
  ): Result<List<Comment>> =
    unitOfWork.transactional { conn ->
      repo.getComments(conn, articleSlug, currentUserEmail)
    }

  override suspend fun createComment(
    articleSlug: String,
    authorEmail: String,
    request: CreateCommentRequest,
  ): Result<Comment> =
    unitOfWork.transactional { conn ->
      repo.createComment(
        conn,
        articleSlug,
        authorEmail,
        request.body,
      )
    }

  override suspend fun deleteComment(
    articleSlug: String,
    commentId: Int,
    userEmail: String,
  ): Result<Unit> =
    unitOfWork.transactional { conn ->
      repo.deleteComment(conn, commentId, userEmail)
    }
}
