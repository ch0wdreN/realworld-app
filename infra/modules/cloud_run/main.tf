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
      ports {
        container_port = var.port
      }
      resources {
        limits = {
          cpu    = "2"
          memory = "1024Mi"
        }
        startup_cpu_boost = true
      }
    }
  }
}

resource "google_cloud_run_v2_service_iam_member" "allow_access_lb_service_account" {
  location = google_cloud_run_v2_service.app.location
  name     = google_cloud_run_v2_service.app.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
