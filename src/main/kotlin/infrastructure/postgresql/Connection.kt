package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.Row
import io.r2dbc.spi.Statement
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import reactor.kotlin.core.publisher.toFlux
import kotlin.coroutines.cancellation.CancellationException

private class Row(
  private val row: io.r2dbc.spi.Row,
) : Row {
  override fun <T> get(
    column: String,
    type: Class<T>,
  ): T = row.get(column, type)!!
}

// Safe Result creation function that re-throws CancellationException
inline fun <T> resultOf(block: () -> T): Result<T> =
  try {
    Result.success(block())
  } catch (e: CancellationException) {
    throw e // Must always re-throw coroutine cancellation
  } catch (e: Exception) {
    Result.failure(e)
  }

fun Statement.bindAll(vararg params: Parameter): Statement {
  params.forEach { it -> this.bind(it.name, it.value) }
  return this
}

class Connection(
  private val conn: io.r2dbc.spi.Connection,
) : Connection {
  override suspend fun beginTransaction(): Result<Unit> =
    resultOf {
      conn.beginTransaction().awaitFirstOrNull()
      Unit
    }

  override suspend fun commitTransaction(): Result<Unit> =
    resultOf {
      conn.commitTransaction().awaitFirstOrNull()
      Unit
    }

  override suspend fun rollbackTransaction(): Result<Unit> =
    resultOf {
      conn.rollbackTransaction().awaitFirstOrNull()
      Unit
    }

  override suspend fun execute(
    sql: String,
    vararg params: Parameter,
  ): Result<Unit> =
    resultOf {
      conn
        .createStatement(sql)
        .bindAll(*params)
        .execute()
        .awaitFirstOrNull()

      Unit
    }

  override suspend fun <T> queryRow(
    sql: String,
    mapper: (Row) -> T,
    vararg params: Parameter,
  ): Result<T> =
    resultOf {
      val row =
        conn
          .createStatement(sql)
          .bindAll(*params)
          .execute()
          .awaitFirst()

      row.map { row, _ -> mapper(Row(row)) }.awaitSingle()
    }

  override suspend fun <T> query(
    sql: String,
    mapper: (Row) -> T,
    vararg params: Parameter,
  ): Result<List<T>> =
    resultOf {
      val rows =
        conn
          .createStatement(sql)
          .bindAll(*params)
          .execute()
          .toFlux()

      rows
        .flatMap { result -> result.map { row, _ -> mapper(Row(row)) } }
        .collectList()
        .awaitSingle()
    }
}
