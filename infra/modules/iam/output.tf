output "run_sa_email" {
  value = google_service_account.run_sa.email
}

output "bastion_sa_email" {
  value = google_service_account.bastion_sa.email
}
