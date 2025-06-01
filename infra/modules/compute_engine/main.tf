resource "google_compute_instance" "bastion_instance" {
  machine_type   = "e2-micro"
  name           = join("-", [var.project_name, "bastion-instance"])
  desired_status = "RUNNING"
  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-12"
    }
  }
  network_interface {
    subnetwork = var.subnetwork
  }
  metadata = {
    enable-oslogin = "TRUE"
  }
  service_account {
    scopes = ["cloud-platform"]
    email  = var.sa_email
  }
  # from /infra/main.tf
  metadata_startup_script = file("../bastion/setup.sh")

  lifecycle {
    ignore_changes = [desired_status]
  }
}

resource "google_compute_firewall" "bastion_firewall" {
  name          = join("-", [var.project_name, "bastion-fw"])
  network       = var.vpc
  direction     = "INGRESS"
  source_ranges = ["35.235.240.0/20"]
  allow {
    protocol = "tcp"
    ports    = [3389, 22]
  }
}
