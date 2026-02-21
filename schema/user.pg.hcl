table "user" {
  schema = schema.public
  column "email" {
    type = varchar
  }
  column "user_name" {
    type = varchar
  }
  column "bio" {
    type = varchar
  }
  column "image" {
    type = varchar
  }
  primary_key {
    columns = [
      column.email,
    ]
  }
  index "user_name_idx" {
    columns = [
      column.user_name,
    ]
  }
}
