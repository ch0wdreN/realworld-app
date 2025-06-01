variable "project_name" {
  description = "Project Name"
  type        = string
}

variable "db_cidr" {
  type = string
}

variable "prefix_length" {
  type = number
}

variable "bastion_cidr" {
  type = string
}
