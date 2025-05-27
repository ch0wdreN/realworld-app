output "sql_user_password" {
  value     = random_password.sql_user_password.result
  sensitive = true
}

output "sql_user_secret" {
  value = google_secret_manager_secret.sql_user_secret.secret_id
}
