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

resource "google_service_account" "bastion_sa" {
  account_id   = join("-", [var.project_name, "bastion-sa"])
  display_name = "Bastion Service Account"
}

resource "google_project_iam_member" "bastion_sql_client_role" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.bastion_sa.email}"
}

resource "google_project_iam_member" "bastion_iap_tunnel_access" {
  project = var.project_id
  role    = "roles/iap.tunnelResourceAccessor"
  member  = "user:${var.my_email}"
}

resource "google_project_iam_member" "bastion_os_admin_login" {
  project = var.project_id
  role    = "roles/compute.osAdminLogin"
  member  = "user:${var.my_email}"
}

resource "google_service_account_iam_member" "bastion_sa_user_permission" {
  service_account_id = google_service_account.bastion_sa.name
  role               = "roles/iam.serviceAccountUser"
  member             = "user:${var.my_email}"
}
