package io.ch0wdren.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtUtil {
  private const val SECRET = "realworld-secret-key-change-in-production"
  private const val ISSUER = "realworld-api"
  private const val VALIDITY_MS = 36_000_000 * 10L

  private val algorithm = Algorithm.HMAC256(SECRET)

  fun generateToken(email: String): String =
    JWT
      .create()
      .withIssuer(ISSUER)
      .withClaim("email", email)
      .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
      .sign(algorithm)

  fun verifier(): JWTVerifier =
    JWT
      .require(algorithm)
      .withIssuer(ISSUER)
      .build()
}
