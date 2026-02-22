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
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.StatusCode

class UserService(
  private val unitOfWork: UnitOfWork,
  private val repo: UserRepository,
) : UserUsecase {
  private val tracer = GlobalOpenTelemetry.getTracer("io.ch0wdren.UserService")
  override suspend fun getUser(email: String): Result<User> =
    unitOfWork.transactional { conn ->
      repo.getUser(conn, email)
    }

  override suspend fun register(
    request: RegisterRequest,
  ): Result<UserWithToken> {
    val span = tracer.spanBuilder("UserService.register").startSpan()
    return try {
      span.setAttribute("user.email", request.email)
      span.setAttribute("user.username", request.userName)

      unitOfWork.transactional { conn ->
        val checkEmailSpan = tracer.spanBuilder("check.email.exists").startSpan()
        val existingEmail =
          try {
            repo.findByEmail(conn, request.email).getOrElse {
              checkEmailSpan.setStatus(StatusCode.ERROR, "Failed to check email")
              checkEmailSpan.recordException(it)
              return@transactional Result.failure(it)
            }
          } finally {
            checkEmailSpan.end()
          }

        if (existingEmail != null) {
          span.setAttribute("error.reason", "email_already_registered")
          span.setStatus(StatusCode.ERROR, "Email already registered")
          return@transactional Result.failure(
            Exception("Email already registered"),
          )
        }

        val checkUsernameSpan = tracer.spanBuilder("check.username.exists").startSpan()
        val existingUsername =
          try {
            repo.findByUsername(
              conn,
              request.userName,
            ).getOrElse {
              checkUsernameSpan.setStatus(StatusCode.ERROR, "Failed to check username")
              checkUsernameSpan.recordException(it)
              return@transactional Result.failure(it)
            }
          } finally {
            checkUsernameSpan.end()
          }

        if (existingUsername != null) {
          span.setAttribute("error.reason", "username_already_taken")
          span.setStatus(StatusCode.ERROR, "Username already taken")
          return@transactional Result.failure(
            Exception("Username already taken"),
          )
        }

        val hashPasswordSpan = tracer.spanBuilder("hash.password").startSpan()
        val hashedPassword =
          try {
            PasswordHasher.hash(request.password)
          } finally {
            hashPasswordSpan.end()
          }

        val createUserSpan = tracer.spanBuilder("create.user").startSpan()
        val user =
          try {
            repo.createUser(
              conn,
              request.email,
              request.userName,
              hashedPassword,
            ).getOrElse {
              createUserSpan.setStatus(StatusCode.ERROR, "Failed to create user")
              createUserSpan.recordException(it)
              return@transactional Result.failure(it)
            }
          } finally {
            createUserSpan.end()
          }

        val generateTokenSpan = tracer.spanBuilder("generate.jwt.token").startSpan()
        val token =
          try {
            JwtUtil.generateToken(user.email)
          } finally {
            generateTokenSpan.end()
          }

        span.setStatus(StatusCode.OK)
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
    } catch (e: Exception) {
      span.setStatus(StatusCode.ERROR, "Registration failed")
      span.recordException(e)
      Result.failure(e)
    } finally {
      span.end()
    }
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
