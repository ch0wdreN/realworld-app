package io.ch0wdren.infrastructure.ktor

import io.ch0wdren.infrastructure.security.JwtUtil
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity() {
  install(Authentication) {
    jwt("auth-jwt") {
      authSchemes("Token", "Bearer")
      verifier(JwtUtil.verifier())
      validate { credential ->
        if (credential.payload.getClaim("email").asString() != null) {
          JWTPrincipal(credential.payload)
        } else {
          null
        }
      }
    }
  }
}
