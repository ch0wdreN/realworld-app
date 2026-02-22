package io.ch0wdren.infrastructure.postgresql

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import kotlinx.coroutines.reactive.awaitSingle

object ConnectionPool {
  private val pool: ConnectionPool

  init {
    val connectionFactory =
      PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration
          .builder()
          .host(System.getenv("DB_HOST"))
          .username(System.getenv("DB_USER"))
          .password(System.getenv("DB_PASSWORD"))
          .port(System.getenv("DB_PORT").toInt())
          .database(System.getenv("DB_NAME"))
          .build(),
      )

    val connectionPoolConfig =
      ConnectionPoolConfiguration
        .builder()
        .connectionFactory(connectionFactory)
        .maxSize(10)
        .initialSize(2)
        .maxIdleTime(java.time.Duration.ofMinutes(5))
        .maxLifeTime(java.time.Duration.ofMinutes(30))
        .maxAcquireTime(java.time.Duration.ofSeconds(10))
        .maxCreateConnectionTime(java.time.Duration.ofSeconds(5))
        .validationQuery("SELECT 1")
        .build()

    pool = ConnectionPool(connectionPoolConfig)
  }

  suspend fun getConnection(): Connection =
    Connection(pool.create().awaitSingle())
}
