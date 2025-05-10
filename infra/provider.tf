terraform {
  backend "gcs" {
    # The bucket value is intentionally left empty. It is overridden at runtime
    # via the -backend-config flag in the backend configuration file.
    bucket = ""
    prefix = ""
  }

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.34.0"
    }
  }

  required_version = ">= 1.10.0"
}

provider "google" {
  project = var.project
  region  = var.region
  zone    = var.zone
}
