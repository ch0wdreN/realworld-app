variable "vpc" {
  type = string
}

variable "private_address_name" {
  type = string
}

variable "project_name" {
  type = string
}

variable "password" {
  type      = string
  sensitive = true
}
