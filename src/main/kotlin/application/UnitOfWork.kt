package io.ch0wdren.application

import io.ch0wdren.application.port.repository.Connection

class UnitOfWork<T>(
  private val conn: Connection,
) {
  suspend fun transactional(
    f: suspend (tx: Connection) -> Result<T>,
  ): Result<T> {
    conn.beginTransaction()

    val result = f(conn)
    if (result.isSuccess) {
      conn.commitTransaction()
    } else {
      conn.rollbackTransaction()
    }

    return result
  }
}
