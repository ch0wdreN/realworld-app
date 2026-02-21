package io.ch0wdren.infrastructure.security

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
  private const val COST = 12

  fun hash(password: String): String =
    BCrypt.withDefaults().hashToString(
      COST,
      password.toCharArray(),
    )

  fun verify(
    password: String,
    hashedPassword: String,
  ): Boolean =
    BCrypt.verifyer().verify(
      password.toCharArray(),
      hashedPassword,
    ).verified
}
