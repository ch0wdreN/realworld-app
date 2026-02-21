package io.ch0wdren.application.port.usecase

import io.ch0wdren.domain.User

interface UserUsecase {
  suspend fun getUser(email: String): Result<User>
}
