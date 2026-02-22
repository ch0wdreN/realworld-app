package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.Row
import kotlinx.datetime.Instant
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

/**
 * Generic reflection-based row mapper for R2DBC result sets.
 *
 * Automatically maps database rows to Kotlin data classes by:
 * - Converting camelCase property names to snake_case column names
 * - Handling nullable fields
 * - Converting PostgreSQL types to Kotlin types (especially timestamps to Instant)
 *
 * Usage:
 * ```
 * val user = mapRow<User>(row)
 * ```
 */
inline fun <reified T : Any> mapRow(row: Row): T = mapRow(T::class, row)

fun <T : Any> mapRow(
  klass: KClass<T>,
  row: Row,
): T {
  val constructor =
    klass.primaryConstructor
      ?: throw IllegalArgumentException("${klass.simpleName} must have a primary constructor")

  val args =
    constructor.parameters.associateWith { param ->
      val columnName = param.name?.toSnakeCase() ?: throw IllegalArgumentException("Parameter ${param.name} has no name")
      val value = getValueFromRow(row, columnName, param)
      value
    }

  return constructor.callBy(args)
}

private fun getValueFromRow(
  row: Row,
  columnName: String,
  param: KParameter,
): Any? {
  // Handle nullable parameters
  val isNullable = param.type.isMarkedNullable
  val classifier = param.type.classifier as? KClass<*>
    ?: throw IllegalArgumentException("Unsupported parameter type: ${param.type}")

  // Get raw value from database
  val rawValue: Any? =
    when (classifier) {
      String::class -> row.get(columnName, String::class.java)
      Int::class -> row.get(columnName, Integer::class.java)?.toInt()
      Long::class -> row.get(columnName, java.lang.Long::class.java)?.toLong()
      Boolean::class -> row.get(columnName, java.lang.Boolean::class.java)?.booleanValue()
      Instant::class -> {
        val timestamp = row.get(columnName, String::class.java)
        timestamp?.let { Instant.parse(it.replace(" ", "T")) }
      }
      UUID::class -> {
        val uuidString = row.get(columnName, String::class.java)
        uuidString?.let { UUID.fromString(it) }
      }
      else -> throw IllegalArgumentException("Unsupported type: ${classifier.simpleName}")
    }

  // Validate non-null requirement
  if (!isNullable && rawValue == null) {
    throw IllegalStateException("Required parameter '${param.name}' (column: $columnName) is null")
  }

  return rawValue
}

/**
 * Converts camelCase string to snake_case.
 * Example: "userName" -> "user_name", "createdAt" -> "created_at"
 */
private fun String.toSnakeCase(): String =
  this.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
