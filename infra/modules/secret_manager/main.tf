resource "google_project_service" "secret_manager" {
  service = "secretmanager.googleapis.com"
}

resource "random_password" "sql_user_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

resource "google_secret_manager_secret" "sql_user_secret" {
  depends_on = [google_project_service.secret_manager]

  secret_id = join("-", [var.project_name, "sql-user-secret"])

  replication {
    user_managed {
      replicas {
        location = var.region
      }
    }
  }
}

resource "google_secret_manager_secret_version" "sql_user_secret_version" {
  secret      = google_secret_manager_secret.sql_user_secret.id
  secret_data = random_password.sql_user_password.result
}
