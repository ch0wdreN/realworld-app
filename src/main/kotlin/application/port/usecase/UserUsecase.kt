package io.ch0wdren.application.port.usecase

import io.ch0wdren.domain.User
import io.ch0wdren.domain.UserWithToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
  val email: String,
  @SerialName("username")
  val userName: String,
  val password: String,
)

@Serializable
data class LoginRequest(
  val email: String,
  val password: String,
)

@Serializable
data class UpdateUserRequest(
  @SerialName("username")
  val userName: String? = null,
  val bio: String? = null,
  val image: String? = null,
  val password: String? = null,
)

interface UserUsecase {
  suspend fun getUser(email: String): Result<User>

  suspend fun register(request: RegisterRequest): Result<UserWithToken>

  suspend fun login(request: LoginRequest): Result<UserWithToken>

  suspend fun updateUser(
    email: String,
    request: UpdateUserRequest,
  ): Result<UserWithToken>
}
