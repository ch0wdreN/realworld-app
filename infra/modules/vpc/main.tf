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
  network       = google_compute_network.vpc.self_link
}

resource "google_compute_subnetwork" "bastion_subnet" {
  name          = join("-", [var.project_name, "bastion-subnet"])
  network       = google_compute_network.vpc.self_link
  ip_cidr_range = var.bastion_cidr
}

resource "google_compute_router" "router" {
  name    = join("-", [var.project_name, "router"])
  network = google_compute_network.vpc.self_link
  region  = google_compute_subnetwork.bastion_subnet.region
}

resource "google_compute_router_nat" "nat" {
  name                               = join("-", [var.project_name, "nat"])
  router                             = google_compute_router.router.name
  source_subnetwork_ip_ranges_to_nat = "LIST_OF_SUBNETWORKS"
  nat_ip_allocate_option             = "AUTO_ONLY"
  subnetwork {
    name                    = google_compute_subnetwork.bastion_subnet.self_link
    source_ip_ranges_to_nat = ["ALL_IP_RANGES"]
  }
}
