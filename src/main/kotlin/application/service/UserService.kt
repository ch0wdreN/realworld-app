package io.ch0wdren.application.service

import io.ch0wdren.application.UnitOfWork
import io.ch0wdren.application.port.repository.UserRepository
import io.ch0wdren.application.port.usecase.LoginRequest
import io.ch0wdren.application.port.usecase.RegisterRequest
import io.ch0wdren.application.port.usecase.UpdateUserRequest
import io.ch0wdren.application.port.usecase.UserUsecase
import io.ch0wdren.domain.User
import io.ch0wdren.domain.UserWithToken
import io.ch0wdren.infrastructure.security.JwtUtil
import io.ch0wdren.infrastructure.security.PasswordHasher

class UserService(
  private val unitOfWork: UnitOfWork,
  private val repo: UserRepository,
) : UserUsecase {
  override suspend fun getUser(email: String): Result<User> =
    unitOfWork.transactional { conn ->
      repo.getUser(conn, email)
    }

  override suspend fun register(
    request: RegisterRequest,
  ): Result<UserWithToken> =
    unitOfWork.transactional { conn ->
      val existingEmail =
        repo.findByEmail(conn, request.email).getOrElse {
          return@transactional Result.failure(it)
        }
      if (existingEmail != null) {
        return@transactional Result.failure(
          Exception("Email already registered"),
        )
      }

      val existingUsername =
        repo.findByUsername(
          conn,
          request.userName,
        ).getOrElse {
          return@transactional Result.failure(it)
        }
      if (existingUsername != null) {
        return@transactional Result.failure(
          Exception("Username already taken"),
        )
      }

      val hashedPassword = PasswordHasher.hash(request.password)
      val user =
        repo.createUser(
          conn,
          request.email,
          request.userName,
          hashedPassword,
        ).getOrElse {
          return@transactional Result.failure(it)
        }
      val token = JwtUtil.generateToken(user.email)

      Result.success(
        UserWithToken(
          email = user.email,
          token = token,
          userName = user.userName,
          bio = user.bio,
          image = user.image,
        ),
      )
    }

  override suspend fun login(request: LoginRequest): Result<UserWithToken> =
    unitOfWork.transactional { conn ->
      val user =
        repo.findByEmail(conn, request.email).getOrElse {
          return@transactional Result.failure(it)
        }
          ?: return@transactional Result.failure(
            Exception("Invalid email or password"),
          )

      if (!PasswordHasher.verify(request.password, user.password)) {
        return@transactional Result.failure(
          Exception("Invalid email or password"),
        )
      }

      val token = JwtUtil.generateToken(user.email)

      Result.success(
        UserWithToken(
          email = user.email,
          token = token,
          userName = user.userName,
          bio = user.bio,
          image = user.image,
        ),
      )
    }

  override suspend fun updateUser(
    email: String,
    request: UpdateUserRequest,
  ): Result<UserWithToken> =
    unitOfWork.transactional { conn ->
      val hashedPassword = request.password?.let { PasswordHasher.hash(it) }
      val user =
        repo
          .updateUser(
            conn,
            email,
            request.userName,
            request.bio,
            request.image,
            hashedPassword,
          )
          .getOrElse { return@transactional Result.failure(it) }

      val token = JwtUtil.generateToken(user.email)

      Result.success(
        UserWithToken(
          email = user.email,
          token = token,
          userName = user.userName,
          bio = user.bio,
          image = user.image,
        ),
      )
    }
}
