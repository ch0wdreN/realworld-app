schema "public" {
  comment = "Public schema"
}

table "user" {
  schema = schema.public
  column "email" {
    type = varchar
  }
  primary_key {
    columns = [
      column.email,
    ]
  }
}
