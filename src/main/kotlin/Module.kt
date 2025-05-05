package io.ch0wdren

import io.ktor.server.application.Application

fun Application.module() {
  configureFrameworks()
  configureSerialization()
  configureSecurity()
  configureSwagger()
  configureRouting()
}
