table "comment" {
  schema = schema.public
  column "id" {
    type = serial
    null = false
  }
  column "body" {
    type = text
    null = false
  }
  column "article_slug" {
    type = varchar
    null = false
  }
  column "author_email" {
    type = varchar
    null = false
  }
  column "created_at" {
    type = timestamptz
    null = false
    default = sql("now()")
  }
  column "updated_at" {
    type = timestamptz
    null = false
    default = sql("now()")
  }
  primary_key {
    columns = [
      column.id,
    ]
  }
  foreign_key "comment_article_fkey" {
    columns = [column.article_slug]
    ref_columns = [table.article.column.slug]
    on_delete = CASCADE
  }
  foreign_key "comment_author_fkey" {
    columns = [column.author_email]
    ref_columns = [table.user.column.email]
    on_delete = CASCADE
  }
  index "comment_article_slug_idx" {
    columns = [
      column.article_slug,
    ]
  }
  index "comment_author_email_idx" {
    columns = [
      column.author_email,
    ]
  }
}
