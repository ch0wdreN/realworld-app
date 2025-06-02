package io.ch0wdren.infrastructure.ktor

import io.ch0wdren.infrastructure.postgresql.ConnectionPool
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
  install(Koin) {
    slf4jLogger()
    modules(
      module {
        single {
          ConnectionPool()
        }
      },
    )
  }
}
