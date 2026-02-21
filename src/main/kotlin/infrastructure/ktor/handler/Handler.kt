package io.ch0wdren.infrastructure.ktor.handler

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val handlerModule =
  module {
    factoryOf(::UserHandler)
    factoryOf(::ProfileHandler)
    factoryOf(::ArticleHandler)
    factoryOf(::CommentHandler)
    factoryOf(::TagHandler)
  }
