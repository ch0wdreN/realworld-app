table "user" {
  schema = schema.public
  column "email" {
    type = varchar
    null = false
  }
  column "user_name" {
    type = varchar
    null = false
  }
  column "password" {
    type = varchar
    null = false
  }
  column "bio" {
    type = varchar
    null = true
  }
  column "image" {
    type = varchar
    null = true
  }
  column "token" {
    type = varchar
    null = true
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
      column.email,
    ]
  }
  index "user_name_idx" {
    unique = true
    columns = [
      column.user_name,
    ]
  }
}
