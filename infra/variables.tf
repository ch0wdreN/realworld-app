variable "project" {
  description = "Google Cloud Project ID"
  type        = string
}

variable "region" {
  description = "Region"
  type        = string
}

variable "zone" {
  description = "Zone"
  type        = string
}

variable "project_name" {
  description = "Project Name"
  type        = string
}

variable "base_domain" {
  description = "The base domain for the project (e.g., 'example.com')"
  type        = string
}

variable "dns_zone_name" {
  description = "The name of the Cloud DNS zone to be used."
  type        = string
}

variable "db_name" {
  description = "The name of the Cloud SQL database to be used."
  type = string
}

variable "db_user" {
  description = "The name of the Cloud SQL user to be used."
  type = string
}

variable "my_email" {
  description = "My email address"
  type = string
}
