resource "google_compute_region_network_endpoint_group" "run_neg" {
  name                  = join("-", [var.project_name, "run-neg"])
  region                = var.region
  network_endpoint_type = "SERVERLESS"

  cloud_run {
    service = var.run_service
  }
}

resource "google_compute_backend_service" "run_backend" {
  name                  = join("-", [var.project_name, "backend"])
  protocol              = "HTTPS"
  enable_cdn            = false
  load_balancing_scheme = "EXTERNAL"

  log_config {
    enable = true
  }
  backend {
    group = google_compute_region_network_endpoint_group.run_neg.self_link
  }
}

resource "google_compute_url_map" "lb_url_map" {
  name            = join("-", [var.project_name, "lb-url-map"])
  default_service = google_compute_backend_service.run_backend.self_link

  host_rule {
    hosts        = [var.domain]
    path_matcher = "allpaths"
  }

  path_matcher {
    name            = "allpaths"
    default_service = google_compute_backend_service.run_backend.self_link
  }
}

resource "google_compute_target_https_proxy" "lb_https_proxy" {
  name             = join("-", [var.project_name, "lb-https-proxy"])
  url_map          = google_compute_url_map.lb_url_map.self_link
  ssl_certificates = [var.cert_id]
}

resource "google_compute_global_address" "lb_ip" {
  name = join("-", [var.project_name, "lb-ip"])
}

resource "google_compute_global_forwarding_rule" "lb_rule" {
  name                  = join("-", [var.project_name, "lb-rule"])
  target                = google_compute_target_https_proxy.lb_https_proxy.self_link
  port_range            = "443"
  load_balancing_scheme = "EXTERNAL"
  ip_address            = google_compute_global_address.lb_ip.address
}
