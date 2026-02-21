package io.ch0wdren.infrastructure.ktor

import io.ktor.server.application.Application

fun Application.module() {
  configureKoin()
  configureSerialization()
  configureSecurity()
  configureSwagger()
  configureRouting()
  configureLogging()
}
