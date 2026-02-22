package io.ch0wdren.infrastructure.postgresql

import io.ch0wdren.application.port.repository.ArticleRepository
import io.ch0wdren.application.port.repository.Connection
import io.ch0wdren.application.port.repository.Parameter
import io.ch0wdren.application.port.repository.Row
import io.ch0wdren.domain.Article
import io.ch0wdren.domain.Profile
import kotlinx.datetime.Instant
import java.util.UUID

internal data class ArticleInsertResult(
  val slug: String,
  val title: String,
  val description: String,
  val body: String,
  val createdAt: Instant,
  val updatedAt: Instant,
)

class ArticleRepositoryImpl : ArticleRepository {
  override suspend fun listArticles(
    conn: Connection,
    currentUserEmail: String?,
    tag: String?,
    author: String?,
    favoritedBy: String?,
    limit: Int,
    offset: Int,
  ): Result<List<Article>> {
    val joins = mutableListOf<String>()
    val conditions = mutableListOf<String>()
    val params = mutableListOf<Parameter>()
    var idx = 1

    params.add(Parameter("$${idx++}", currentUserEmail))

    if (tag != null) {
      joins.add(
        """
        INNER JOIN public.article_tag at
          ON at.article_slug = a.slug
        """.trimIndent(),
      )
      conditions.add("at.tag_name = $$idx")
      params.add(Parameter("$${idx++}", tag))
    }
    if (author != null) {
      conditions.add("u.user_name = $$idx")
      params.add(Parameter("$${idx++}", author))
    }
    if (favoritedBy != null) {
      joins.add(
        """
        INNER JOIN public.favorite fav
          ON fav.article_slug = a.slug
        INNER JOIN public.user u_fav
          ON u_fav.email = fav.user_email
        """.trimIndent(),
      )
      conditions.add("u_fav.user_name = $$idx")
      params.add(Parameter("$${idx++}", favoritedBy))
    }

    val joinClause = if (joins.isEmpty()) "" else joins.joinToString(" ")
    val whereClause =
      if (conditions.isEmpty()) {
        ""
      } else {
        "AND " + conditions.joinToString(" AND ")
      }

    val sql =
      """
      SELECT
        a.slug,
        a.title,
        a.description,
        a.body,
        a.created_at,
        a.updated_at,
        u.user_name,
        u.bio,
        u.image,
        CASE
          WHEN fl.follower_email IS NOT NULL
            THEN TRUE
          ELSE FALSE
        END AS following,
        CASE
          WHEN fv.user_email IS NOT NULL
            THEN TRUE
          ELSE FALSE
        END AS favorited,
        COALESCE(fc.cnt, 0) AS favorites_count,
        COALESCE(tl.tags, '') AS tag_list
      FROM public.article a
      JOIN public.user u
        ON u.email = a.author_email
      $joinClause
      LEFT JOIN public.follow fl
        ON fl.followee_email = a.author_email
        AND fl.follower_email = $1
      LEFT JOIN public.favorite fv
        ON fv.article_slug = a.slug
        AND fv.user_email = $1
      LEFT JOIN (
        SELECT article_slug, COUNT(*) AS cnt
        FROM public.favorite
        GROUP BY article_slug
      ) fc ON fc.article_slug = a.slug
      LEFT JOIN (
        SELECT
          at3.article_slug,
          STRING_AGG(at3.tag_name, ',' ORDER BY at3.tag_name)
            AS tags
        FROM public.article_tag at3
        GROUP BY at3.article_slug
      ) tl ON tl.article_slug = a.slug
      WHERE TRUE
      $whereClause
      ORDER BY a.created_at DESC
      LIMIT $$idx
      OFFSET $${idx + 1}
      """.trimIndent()

    params.add(Parameter("$${idx++}", limit))
    params.add(Parameter("$${idx}", offset))

    return conn.query(
      sql,
      ::mapRowToArticle,
      *params.toTypedArray(),
    )
  }

  override suspend fun countArticles(
    conn: Connection,
    tag: String?,
    author: String?,
    favoritedBy: String?,
  ): Result<Int> {
    val joins = mutableListOf<String>()
    val conditions = mutableListOf<String>()
    val params = mutableListOf<Parameter>()
    var idx = 1

    if (tag != null) {
      joins.add(
        """
        INNER JOIN public.article_tag at
          ON at.article_slug = a.slug
        """.trimIndent(),
      )
      conditions.add("at.tag_name = $$idx")
      params.add(Parameter("$${idx++}", tag))
    }
    if (author != null) {
      conditions.add("u.user_name = $$idx")
      params.add(Parameter("$${idx++}", author))
    }
    if (favoritedBy != null) {
      joins.add(
        """
        INNER JOIN public.favorite fav
          ON fav.article_slug = a.slug
        INNER JOIN public.user u_fav
          ON u_fav.email = fav.user_email
        """.trimIndent(),
      )
      conditions.add("u_fav.user_name = $$idx")
      params.add(Parameter("$${idx++}", favoritedBy))
    }

    val joinClause = if (joins.isEmpty()) "" else joins.joinToString(" ")
    val whereClause =
      if (conditions.isEmpty()) {
        ""
      } else {
        "AND " + conditions.joinToString(" AND ")
      }

    val sql =
      """
      SELECT COUNT(*) AS cnt
      FROM public.article a
      JOIN public.user u
        ON u.email = a.author_email
      $joinClause
      WHERE TRUE
      $whereClause
      """.trimIndent()

    return conn.queryRow(
      sql,
      { row -> row.get("cnt", Long::class.java)!!.toInt() },
      *params.toTypedArray(),
    )
  }

  override suspend fun getFeed(
    conn: Connection,
    currentUserEmail: String,
    limit: Int,
    offset: Int,
  ): Result<List<Article>> =
    conn.query(
      """
      SELECT
        a.slug,
        a.title,
        a.description,
        a.body,
        a.created_at,
        a.updated_at,
        u.user_name,
        u.bio,
        u.image,
        TRUE AS following,
        CASE
          WHEN fv.user_email IS NOT NULL
            THEN TRUE
          ELSE FALSE
        END AS favorited,
        COALESCE(fc.cnt, 0) AS favorites_count,
        COALESCE(tl.tags, '') AS tag_list
      FROM public.article a
      JOIN public.user u
        ON u.email = a.author_email
      JOIN public.follow fl
        ON fl.followee_email = a.author_email
        AND fl.follower_email = $1
      LEFT JOIN public.favorite fv
        ON fv.article_slug = a.slug
        AND fv.user_email = $1
      LEFT JOIN (
        SELECT article_slug, COUNT(*) AS cnt
        FROM public.favorite
        GROUP BY article_slug
      ) fc ON fc.article_slug = a.slug
      LEFT JOIN (
        SELECT
          at3.article_slug,
          STRING_AGG(at3.tag_name, ',' ORDER BY at3.tag_name)
            AS tags
        FROM public.article_tag at3
        GROUP BY at3.article_slug
      ) tl ON tl.article_slug = a.slug
      ORDER BY a.created_at DESC
      LIMIT $2
      OFFSET $3
      """.trimIndent(),
      ::mapRowToArticle,
      Parameter("$1", currentUserEmail),
      Parameter("$2", limit),
      Parameter("$3", offset),
    )

  override suspend fun countFeed(
    conn: Connection,
    currentUserEmail: String,
  ): Result<Int> =
    conn.queryRow(
      """
       SELECT COUNT(*) AS cnt
       FROM public.article a
       JOIN public.follow fl
         ON fl.followee_email = a.author_email
         AND fl.follower_email = $1
       """.trimIndent(),
      { row -> row.get("cnt", Long::class.java)!!.toInt() },
      Parameter("$1", currentUserEmail),
    )

  override suspend fun getArticle(
    conn: Connection,
    slug: String,
    currentUserEmail: String?,
  ): Result<Article?> =
    conn.queryRowOrNull(
      """
      SELECT
        a.slug,
        a.title,
        a.description,
        a.body,
        a.created_at,
        a.updated_at,
        u.user_name,
        u.bio,
        u.image,
        CASE
          WHEN fl.follower_email IS NOT NULL
            THEN TRUE
          ELSE FALSE
        END AS following,
        CASE
          WHEN fv.user_email IS NOT NULL
            THEN TRUE
          ELSE FALSE
        END AS favorited,
        COALESCE(fc.cnt, 0) AS favorites_count,
        COALESCE(tl.tags, '') AS tag_list
      FROM public.article a
      JOIN public.user u
        ON u.email = a.author_email
      LEFT JOIN public.follow fl
        ON fl.followee_email = a.author_email
        AND fl.follower_email = $2
      LEFT JOIN public.favorite fv
        ON fv.article_slug = a.slug
        AND fv.user_email = $2
      LEFT JOIN (
        SELECT article_slug, COUNT(*) AS cnt
        FROM public.favorite
        GROUP BY article_slug
      ) fc ON fc.article_slug = a.slug
      LEFT JOIN (
        SELECT
          at3.article_slug,
          STRING_AGG(at3.tag_name, ',' ORDER BY at3.tag_name)
            AS tags
        FROM public.article_tag at3
        GROUP BY at3.article_slug
      ) tl ON tl.article_slug = a.slug
      WHERE a.slug = $1
      """.trimIndent(),
      ::mapRowToArticle,
      Parameter("$1", slug),
      Parameter("$2", currentUserEmail),
    )

  override suspend fun createArticle(
    conn: Connection,
    slug: String,
    title: String,
    description: String,
    body: String,
    authorEmail: String,
    tagList: List<String>,
  ): Result<Article> {
    val insertedArticle = conn
      .queryRow(
        """
        INSERT INTO public.article
          (slug, title, description, body, author_email,
           created_at, updated_at)
        VALUES ($1, $2, $3, $4, $5, NOW(), NOW())
        RETURNING slug, title, description, body, author_email, created_at, updated_at
        """.trimIndent(),
        { row -> mapRow<ArticleInsertResult>(row) },
        Parameter("$1", slug),
        Parameter("$2", title),
        Parameter("$3", description),
        Parameter("$4", body),
        Parameter("$5", authorEmail),
      ).getOrElse {
        return Result.failure(it)
      }

    for (tag in tagList) {
      conn
        .execute(
          """
          INSERT INTO public.tag (name)
          VALUES ($1)
          ON CONFLICT DO NOTHING
          """.trimIndent(),
          Parameter("$1", tag),
        ).getOrElse {
          return Result.failure(it)
        }

      conn
        .execute(
          """
          INSERT INTO public.article_tag
            (article_slug, tag_name)
          VALUES ($1, $2)
          ON CONFLICT DO NOTHING
          """.trimIndent(),
          Parameter("$1", slug),
          Parameter("$2", tag),
        ).getOrElse {
          return Result.failure(it)
        }
    }

    val author = conn.queryRow(
      "SELECT user_name, bio, image FROM public.user WHERE email = $1",
      { row: Row ->
        Profile(
          userName = row.get("user_name", String::class.java)!!,
          bio = row.get("bio", String::class.java),
          image = row.get("image", String::class.java),
          following = false,
        )
      },
      Parameter("$1", authorEmail),
    ).getOrElse {
      return Result.failure(it)
    }

    return Result.success(
      Article(
        slug = insertedArticle.slug,
        title = insertedArticle.title,
        description = insertedArticle.description,
        body = insertedArticle.body,
        tagList = tagList,
        createdAt = insertedArticle.createdAt,
        updatedAt = insertedArticle.updatedAt,
        favorited = false,
        favoritesCount = 0,
        author = author,
      )
    )
  }

  override suspend fun updateArticle(
    conn: Connection,
    slug: String,
    title: String?,
    description: String?,
    body: String?,
    currentUserEmail: String?,
  ): Result<Article> {
    val newSlug =
      if (title != null) {
        generateSlug(title)
      } else {
        null
      }

    conn
      .execute(
        """
        UPDATE public.article
        SET
          slug = COALESCE($2, slug),
          title = COALESCE($3, title),
          description = COALESCE($4, description),
          body = COALESCE($5, body),
          updated_at = NOW()
        WHERE slug = $1
        """.trimIndent(),
        Parameter("$1", slug),
        Parameter("$2", newSlug),
        Parameter("$3", title),
        Parameter("$4", description),
        Parameter("$5", body),
      ).getOrElse {
        return Result.failure(it)
      }

    val resultSlug = newSlug ?: slug
    return getArticle(
      conn,
      resultSlug,
      currentUserEmail,
    ).let { result ->
      result.getOrElse {
        return Result.failure(it)
      }?.let { Result.success(it) }
        ?: Result.failure(Exception("Article not found"))
    }
  }

  override suspend fun deleteArticle(
    conn: Connection,
    slug: String,
  ): Result<Unit> =
    conn.execute(
      """
      DELETE FROM public.article
      WHERE slug = $1
      """.trimIndent(),
      Parameter("$1", slug),
    )

  private fun mapRowToArticle(row: Row): Article {
    val tagString = row.get("tag_list", String::class.java)
    val tags =
      if (tagString.isNullOrEmpty()) {
        emptyList()
      } else {
        tagString.split(",")
      }

    return Article(
      slug = row.get("slug", String::class.java)!!,
      title = row.get("title", String::class.java)!!,
      description =
        row.get("description", String::class.java)!!,
      body = row.get("body", String::class.java)!!,
      tagList = tags,
      createdAt =
        Instant.parse(
          row.get("created_at", String::class.java)!!.replace(" ", "T"),
        ),
      updatedAt =
        Instant.parse(
          row.get("updated_at", String::class.java)!!.replace(" ", "T"),
        ),
      favorited =
        row.get("favorited", Boolean::class.java)!!,
      favoritesCount =
        row.get("favorites_count", Long::class.java)!!.toInt(),
      author =
        Profile(
          userName =
            row.get("user_name", String::class.java)!!,
          bio = row.get("bio", String::class.java),
          image = row.get("image", String::class.java),
          following =
            row.get("following", Boolean::class.java)!!,
        ),
    )
  }

  override suspend fun favoriteArticle(
    conn: Connection,
    slug: String,
    userEmail: String,
  ): Result<Article> {
    conn
      .execute(
        """
        INSERT INTO public.favorite (user_email, article_slug)
        VALUES ($1, $2)
        ON CONFLICT DO NOTHING
        """.trimIndent(),
        Parameter("$1", userEmail),
        Parameter("$2", slug),
      ).getOrElse {
        return Result.failure(it)
      }

    return getArticle(conn, slug, userEmail).mapCatching {
      it ?: throw Exception("Article not found")
    }
  }

  override suspend fun unfavoriteArticle(
    conn: Connection,
    slug: String,
    userEmail: String,
  ): Result<Article> {
    conn
      .execute(
        """
        DELETE FROM public.favorite
        WHERE user_email = $1 AND article_slug = $2
        """.trimIndent(),
        Parameter("$1", userEmail),
        Parameter("$2", slug),
      ).getOrElse {
        return Result.failure(it)
      }

    return getArticle(conn, slug, userEmail).mapCatching {
      it ?: throw Exception("Article not found")
    }
  }

  companion object {
    fun generateSlug(title: String): String =
      title
        .lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
  }
}
