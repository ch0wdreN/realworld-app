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

variable "project_number" {
  description = "Google Cloud Project Number (numeric identifier for the project)"
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
  type = string
}

variable "db_user" {
  type = string
}
