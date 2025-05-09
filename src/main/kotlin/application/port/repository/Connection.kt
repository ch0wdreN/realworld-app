package io.ch0wdren.application.port.repository

data class Parameter(
  val name: String,
  val value: Any,
)

interface RowMapper<T> {
  fun map(row: Any): T
}

interface Connection {
  fun beginTransaction()

  fun commitTransaction()

  fun rollbackTransaction()

  suspend fun execute(
    sql: String,
    vararg params: Parameter,
  ): Result<Unit>

  suspend fun <T> queryRow(
    sql: String,
    mapper: RowMapper<T>,
    vararg params: Parameter,
  ): Result<T>

  suspend fun <T> query(
    sql: String,
    mapper: RowMapper<T>,
    vararg params: Parameter,
  ): Result<List<T>>
}
