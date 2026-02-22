package io.ch0wdren.application

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.infrastructure.postgresql.ConnectionPool
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.StatusCode

class UnitOfWork {
  private val tracer = GlobalOpenTelemetry.getTracer("io.ch0wdren.UnitOfWork")

  suspend fun <T> transactional(
    f: suspend (tx: Connection) -> Result<T>,
  ): Result<T> =
    ConnectionPool.getConnection().use { conn ->
      val span = tracer.spanBuilder("UnitOfWork.transactional").startSpan()
      try {
        span.addEvent("beginTransaction")
        conn.beginTransaction().getOrElse {
          span.setStatus(StatusCode.ERROR, "Failed to begin transaction")
          return@use Result.failure(it)
        }

        val result = f(conn)
        if (result.isSuccess) {
          span.addEvent("commitTransaction")
          conn.commitTransaction().getOrElse { error ->
            span.setStatus(StatusCode.ERROR, "Failed to commit transaction: ${error.message}")
            conn.rollbackTransaction()
            return@use Result.failure(error)
          }
        } else {
          span.addEvent("rollbackTransaction")
          conn.rollbackTransaction()
        }
        result
      } catch (e: Exception) {
        span.setStatus(StatusCode.ERROR, "Exception in transactional: ${e.message}")
        conn.rollbackTransaction()
        Result.failure(e)
      } finally {
        span.end()
      }
    }
}
