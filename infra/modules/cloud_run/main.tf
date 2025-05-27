resource "google_project_service" "vpc_access" {
  service = "vpcaccess.googleapis.com"
}

resource "google_vpc_access_connector" "connector" {
  depends_on = [google_project_service.vpc_access]

  name          = join("-", [var.project_name, "connector"])
  ip_cidr_range = var.connector_cidr
  network       = var.network
  max_instances = 3
  min_instances = 2
}

resource "google_project_service" "run" {
  service = "run.googleapis.com"
}

resource "google_cloud_run_v2_service" "app" {
  depends_on = [google_project_service.run]

  location            = var.region
  name                = var.service_name
  deletion_protection = false
  ingress             = "INGRESS_TRAFFIC_INTERNAL_LOAD_BALANCER"

  template {
    containers {
      image = "${var.region}-docker.pkg.dev/${var.project_id}/${var.repository_name}/app:latest"
      name  = "app"
      ports {
        container_port = var.port
      }
      env {
        name = "DB_USER"
        # ref. infra/modules/cloud_sql/main.tf  google_sql_user.postgres_user
        value = "app_user"
      }
      env {
        name  = "DB_HOST"
        value = "127.0.0.1"
      }
      env {
        name  = "DB_PORT"
        value = "5432"
      }
      env {
        name  = "DB_NAME"
        value = "realworld_db"
      }
      env {
        name = "DB_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = var.sql_user_secret_id
            version = "latest"
          }
        }
      }
      resources {
        limits = {
          cpu    = "4"
          memory = "2048Mi"
        }
        startup_cpu_boost = true
      }
    }
    containers {
      image = "gcr.io/cloud-sql-connectors/cloud-sql-proxy:2.16.0-bookworm"
      name  = "sql-proxy"
      args = [
        "--address", "0.0.0.0",
        "--port", "5432",
        "--private-ip",
        var.instance_connection_name,
      ]
    }
    vpc_access {
      connector = google_vpc_access_connector.connector.self_link
      egress    = "ALL_TRAFFIC"
    }
    service_account = var.sa_email
  }
}

resource "google_cloud_run_v2_service_iam_member" "allow_access_lb_service_account" {
  location = google_cloud_run_v2_service.app.location
  name     = google_cloud_run_v2_service.app.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
