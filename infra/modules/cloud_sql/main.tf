resource "google_project_service" "service_networking" {
  service = "servicenetworking.googleapis.com"
}

resource "google_service_networking_connection" "vpc_connection" {
  depends_on = [google_project_service.service_networking]

  network                 = var.vpc
  reserved_peering_ranges = [var.private_address_name]
  service                 = "servicenetworking.googleapis.com"
}

resource "google_sql_database_instance" "postgres" {
  depends_on = [google_service_networking_connection.vpc_connection]

  database_version    = "POSTGRES_17"
  name                = join("-", [var.project_name, "db-instance"])
  deletion_protection = true
  settings {
    tier    = "db-custom-1-3840"
    edition = "ENTERPRISE"
    ip_configuration {
      ipv4_enabled                                  = false
      private_network                               = var.vpc
      enable_private_path_for_google_cloud_services = true
    }
  }
}

resource "google_sql_database" "db" {
  instance = google_sql_database_instance.postgres.id
  name     =var.db_name
}

resource "google_sql_user" "postgres_user" {
  instance = google_sql_database_instance.postgres.id
  name     = var.user_name
  password = var.password
}
