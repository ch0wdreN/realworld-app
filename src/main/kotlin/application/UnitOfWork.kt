package io.ch0wdren.application

import io.ch0wdren.application.port.repository.Connection

class UnitOfWork(
  private val conn: Connection,
) {
  suspend fun <T> transactional(
    f: suspend (tx: Connection) -> Result<T>,
  ): Result<T> {
    conn.beginTransaction().getOrElse {
      return Result.failure(it)
    }

    return try {
      val result = f(conn)
      if (result.isSuccess) {
        conn.commitTransaction().getOrElse { error ->
          conn.rollbackTransaction()
          return Result.failure(error)
        }
      } else {
        conn.rollbackTransaction()
      }
      result
    } catch (e: Exception) {
      conn.rollbackTransaction()
      Result.failure(e)
    }
  }
}
