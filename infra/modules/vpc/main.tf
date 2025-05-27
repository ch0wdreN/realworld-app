resource "google_compute_network" "vpc" {
  name                    = join("-", [var.project_name, "vpc"])
  auto_create_subnetworks = false
}

resource "google_compute_global_address" "db_address" {
  name          = join("-", [var.project_name, "db-address"])
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  address       = var.db_cidr
  prefix_length = var.prefix_length
  network       = google_compute_network.vpc.network_id
}
