env "local" {
  # https://atlasgo.io/atlas-schema/projects#getenv
  url = getenv("LOCAL_DB_URL")
  migration {
    dir = "file://migrations"
  }
}
