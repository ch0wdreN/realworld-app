table "follow" {
  schema = schema.public
  column "follower_email" {
    type = varchar
    null = false
  }
  column "followee_email" {
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
      column.follower_email,
      column.followee_email,
    ]
  }
  foreign_key "follow_follower_fkey" {
    columns = [column.follower_email]
    ref_columns = [table.user.column.email]
    on_delete = CASCADE
  }
  foreign_key "follow_followee_fkey" {
    columns = [column.followee_email]
    ref_columns = [table.user.column.email]
    on_delete = CASCADE
  }
  index "follow_follower_email_idx" {
    columns = [
      column.follower_email,
    ]
  }
  index "follow_followee_email_idx" {
    columns = [
      column.followee_email,
    ]
  }
}
