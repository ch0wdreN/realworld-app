table "tag" {
  schema = schema.public
  column "name" {
    type = varchar
    null = false
  }
  primary_key {
    columns = [
      column.name,
    ]
  }
}

table "article_tag" {
  schema = schema.public
  column "article_slug" {
    type = varchar
    null = false
  }
  column "tag_name" {
    type = varchar
    null = false
  }
  primary_key {
    columns = [
      column.article_slug,
      column.tag_name,
    ]
  }
  foreign_key "article_tag_article_fkey" {
    columns = [column.article_slug]
    ref_columns = [table.article.column.slug]
    on_delete = CASCADE
  }
  foreign_key "article_tag_tag_fkey" {
    columns = [column.tag_name]
    ref_columns = [table.tag.column.name]
    on_delete = CASCADE
  }
  index "article_tag_article_slug_idx" {
    columns = [
      column.article_slug,
    ]
  }
  index "article_tag_tag_name_idx" {
    columns = [
      column.tag_name,
    ]
  }
}
