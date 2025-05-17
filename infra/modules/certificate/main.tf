resource "google_project_service" "certificate_manager" {
  service = "certificatemanager.googleapis.com"
}

resource "google_compute_managed_ssl_certificate" "lb_certificate" {
  depends_on = [google_project_service.certificate_manager]

  name = join("-", [var.project_name, "lb-certificate"])
  managed {
    domains = [var.lb_sub_domain]
  }
}
