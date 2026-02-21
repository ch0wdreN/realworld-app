table "favorite" {
  schema = schema.public
  column "user_email" {
    type = varchar
    null = false
  }
  column "article_slug" {
    type = varchar
    null = false
  }
  column "created_at" {
    type = timestamptz
    null = false
    default = sql("now()")
  }
  primary_key {
    columns = [
      column.user_email,
      column.article_slug,
    ]
  }
  foreign_key "favorite_user_fkey" {
    columns = [column.user_email]
    ref_columns = [table.user.column.email]
    on_delete = CASCADE
  }
  foreign_key "favorite_article_fkey" {
    columns = [column.article_slug]
    ref_columns = [table.article.column.slug]
    on_delete = CASCADE
  }
  index "favorite_user_email_idx" {
    columns = [
      column.user_email,
    ]
  }
  index "favorite_article_slug_idx" {
    columns = [
      column.article_slug,
    ]
  }
}
