output "vpc_link" {
  value = google_compute_network.vpc.self_link
}

output "private_ip_name" {
  value = google_compute_global_address.db_address.name
}

output "bastion_subnet" {
  value = google_compute_subnetwork.bastion_subnet.name
}
