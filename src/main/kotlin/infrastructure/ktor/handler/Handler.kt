package infrastructure.ktor.handler

import io.ch0wdren.infrastructure.ktor.handler.UserHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val handlerModule =
  module {
    factoryOf(::UserHandler)
  }
