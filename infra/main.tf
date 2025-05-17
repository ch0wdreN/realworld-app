module "repository" {
  source = "./modules/registry"

  project_id   = var.project
  project_name = var.project_name
  region       = var.region
}

module "cloud_run" {
  source = "./modules/cloud_run"

  project_id      = var.project
  region          = var.region
  service_name    = "app"
  repository_name = module.repository.repository_name
  port            = 8080
  project_number  = var.project_number
}

module "certificate" {
  source = "./modules/certificate"

  project_name  = var.project_name
  lb_sub_domain = "api.${var.base_domain}"
}

module "load_balancer" {
  source = "./modules/load_balancer"

  cert_id      = module.certificate.self_link
  project_name = var.project_name
  region       = var.region
  run_service  = module.cloud_run.run_id
  domain       = "api.${var.base_domain}"
}

module "cloud_dns" {
  source = "./modules/cloud_dns"

  lb_sub_domain = "api.${var.base_domain}"
  zone          = var.dns_zone_name
  lb_ip         = module.load_balancer.lb_ip
}

module "vpc" {
  source = "./modules/vpc"

  project_name = var.project_name
  region       = var.region
}
