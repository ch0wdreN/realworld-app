package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Row
import io.ch0wdren.application.port.repository.TagRepository

class TagRepositoryImpl : TagRepository {
  override suspend fun getAllTags(conn: Connection): Result<List<String>> =
    conn.query(
      """
      SELECT name FROM public.tag
      ORDER BY name
      """.trimIndent(),
      ::mapRowToString,
    )

  private fun mapRowToString(row: Row): String =
    row.get("name", String::class.java)!!
}
