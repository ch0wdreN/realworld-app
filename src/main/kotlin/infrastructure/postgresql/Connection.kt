package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.Row
import io.r2dbc.spi.Statement
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import reactor.kotlin.core.publisher.toFlux

private class Row(
  private val row: io.r2dbc.spi.Row,
) : Row {
  override fun <T> get(
    column: String,
    type: Class<T>,
  ): T = row.get(column, type)!!
}

fun Statement.bindAll(vararg params: Parameter): Statement {
  params.forEach { it -> this.bind(it.name, it.value) }
  return this
}

class Connection(
  private val conn: io.r2dbc.spi.Connection,
) : Connection {
  override fun beginTransaction() {
    conn.beginTransaction()
  }

  override fun commitTransaction() {
    conn.commitTransaction()
  }

  override fun rollbackTransaction() {
    conn.rollbackTransaction()
  }

  override suspend fun execute(
    sql: String,
    vararg params: Parameter,
  ): Result<Unit> =
    try {
      conn
        .createStatement(sql)
        .bindAll(*params)
        .execute()
        .awaitFirstOrNull()

      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }

  override suspend fun <T> queryRow(
    sql: String,
    mapper: (Row) -> T,
    vararg params: Parameter,
  ): Result<T> =
    try {
      val row =
        conn
          .createStatement(sql)
          .bindAll(*params)
          .execute()
          .awaitFirst()

      val result = row.map { row, _ -> mapper(Row(row)) }.awaitSingle()

      Result.success(result)
    } catch (e: Exception) {
      Result.failure(e)
    }

  override suspend fun <T> query(
    sql: String,
    mapper: (Row) -> T,
    vararg params: Parameter,
  ): Result<List<T>> =
    try {
      val rows =
        conn
          .createStatement(sql)
          .bindAll(*params)
          .execute()
          .toFlux()
      val results =
        rows
          .flatMap { result -> result.map { row, _ -> mapper(Row(row)) } }
          .collectList()
          .awaitSingle()

      Result.success(results)
    } catch (e: Exception) {
      Result.failure(e)
    }
}
