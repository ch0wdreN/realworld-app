package io.ch0wdren.infrastructure.ktor.handler

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val handlerModule =
  module {
    factoryOf(::UserHandler)
  }
