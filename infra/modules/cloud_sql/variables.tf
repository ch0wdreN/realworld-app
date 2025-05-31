variable "vpc" {
  type = string
}

variable "private_address_name" {
  type = string
}

variable "project_name" {
  type = string
}

variable "db_name" {
  type = string
}

variable "user_name" {
  type = string
}

variable "password" {
  type      = string
  sensitive = true
}
