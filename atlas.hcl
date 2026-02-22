env "local" {
  # https://atlasgo.io/atlas-schema/projects#getenv
  url = getenv("LOCAL_DB_URL")
  src = "file://schema"
  dev = "docker://postgres/15/realworld?search_path=public"
  migration {
    dir = "file://migrations"
  }
  diff {
    skip {
      drop_schema = true
      drop_column = false
    }
  }
}
