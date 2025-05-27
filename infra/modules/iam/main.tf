resource "google_service_account" "run_sa" {
  account_id   = join("-", [var.project_name, "run-sa"])
  display_name = "Cloud Run Service Account"
}

resource "google_project_iam_member" "sql_client_role" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.run_sa.email}"
}

resource "google_secret_manager_secret_iam_member" "sql_secret_role" {
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.run_sa.email}"
  project   = var.project_id
  secret_id = var.secret_id
}
