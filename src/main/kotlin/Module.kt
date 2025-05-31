package io.ch0wdren

import io.ktor.server.application.Application
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking

fun Application.module() {
  // FIXME: FIX connection initialization
  // ---------- DB CONNECTION CHECK ----------
  val connectionFactoryConfig =
    PostgresqlConnectionConfiguration
      .builder()
      .host(System.getenv("DB_HOST"))
      .username(System.getenv("DB_USER"))
      .password(System.getenv("DB_PASSWORD"))
      .port(System.getenv("DB_PORT").toInt())
      .database(System.getenv("DB_NAME"))
      .build()

  val connectionFactory = PostgresqlConnectionFactory(connectionFactoryConfig)
  runBlocking {
    val conn =
      connectionFactory
        .create()
        .awaitSingle()

    try {
      conn.createStatement("SELECT 1").execute().awaitSingle()
    } catch (e: Exception) {
      throw e
    } finally {
      conn.close()
    }
  }
  // ---------- END ----------

  configureFrameworks()
  configureSerialization()
  configureSecurity()
  configureSwagger()
  configureRouting()
}
