package io.ch0wdren.application.port.usecase

interface TagUsecase {
  suspend fun getAllTags(): Result<List<String>>
}
