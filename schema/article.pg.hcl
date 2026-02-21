table "article" {
  schema = schema.public
  column "slug" {
    type = varchar
    null = false
  }
  column "title" {
    type = varchar
    null = false
  }
  column "description" {
    type = varchar
    null = false
  }
  column "body" {
    type = text
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
      column.slug,
    ]
  }
  foreign_key "article_author_fkey" {
    columns = [column.author_email]
    ref_columns = [table.user.column.email]
    on_delete = CASCADE
  }
  index "article_author_email_idx" {
    columns = [
      column.author_email,
    ]
  }
  index "article_created_at_idx" {
    columns = [
      column.created_at,
    ]
  }
}
