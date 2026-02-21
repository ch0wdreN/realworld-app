package io.ch0wdren.application.port.repository

interface TagRepository {
  suspend fun getAllTags(conn: Connection): Result<List<String>>
}
