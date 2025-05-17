resource "google_compute_network" "vpc" {
  name                    = join("-", [var.project_name, "vpc"])
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "private_db_subnet" {
  name                     = join("-", [var.project_name, "private-db-subnet"])
  network                  = google_compute_network.vpc.id
  ip_cidr_range            = "10.0.1.0/24"
  region                   = var.region
  private_ip_google_access = true
  stack_type               = "IPV4_ONLY"
  purpose                  = "PRIVATE"
}
