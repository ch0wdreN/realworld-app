resource "google_compute_network" "vpc" {
  name = join("-", [var.project_name, "vpc"])
  auto_create_subnetworks = false
}
