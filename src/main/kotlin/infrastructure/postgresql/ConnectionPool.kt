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
        .maxSize(20)
        .maxIdleTime(java.time.Duration.ofMillis(1000))
        .build()

    pool = ConnectionPool(connectionPoolConfig)
  }

  suspend fun getConnection(): Connection =
    Connection(pool.create().awaitSingle())
}
