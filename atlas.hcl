env "local" {
  url = "postgres://admin:passw0rd@:5432/realworld?sslmode=disable"
  migration {
    dir = "file://migrations"
  }
}
