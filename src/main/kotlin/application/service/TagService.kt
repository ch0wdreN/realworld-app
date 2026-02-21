package io.ch0wdren.application.service

import io.ch0wdren.application.UnitOfWork
import io.ch0wdren.application.port.repository.TagRepository
import io.ch0wdren.application.port.usecase.TagUsecase

class TagService(
  private val unitOfWork: UnitOfWork,
  private val repo: TagRepository,
) : TagUsecase {
  override suspend fun getAllTags(): Result<List<String>> =
    unitOfWork.transactional { conn ->
      repo.getAllTags(conn)
    }
}
