package io.ch0wdren.infrastructure.ktor

import infrastructure.ktor.handler.handlerModule
import io.ch0wdren.application.service.factoryModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
  install(Koin) {
    slf4jLogger()
    modules(
      factoryModule,
      handlerModule,
    )
  }
}
