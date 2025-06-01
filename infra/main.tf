module "vpc" {
  source = "./modules/vpc"

  project_name  = var.project_name
  db_cidr       = "10.0.0.0"
  prefix_length = 16
  bastion_cidr  = "10.10.0.0/16"
}

module "secrets" {
  source = "./modules/secret_manager"

  project_name = var.project_name
  region       = var.region
}

module "cloud_sql" {
  source = "./modules/cloud_sql"

  private_address_name = module.vpc.private_ip_name
  project_name         = var.project_name
  vpc                  = module.vpc.vpc_link
  password             = module.secrets.sql_user_password
  db_name              = var.db_name
  user_name            = var.db_user
}

module "repository" {
  source = "./modules/registry"

  project_name = var.project_name
  region       = var.region
}

module "iam" {
  source = "./modules/iam"

  project_name = var.project_name
  project_id   = var.project
  secret_id    = module.secrets.sql_user_secret
  my_email     = var.my_email
}

module "cloud_run" {
  source = "./modules/cloud_run"

  project_id               = var.project
  region                   = var.region
  service_name             = "app"
  repository_name          = module.repository.repository_name
  port                     = 8080
  connector_cidr           = "10.1.0.0/28"
  network                  = module.vpc.vpc_link
  project_name             = var.project_name
  sa_email                 = module.iam.run_sa_email
  instance_connection_name = module.cloud_sql.instance_connection_name
  sql_user_secret_id       = module.secrets.sql_user_secret
  db_name                  = var.db_name
  db_user                  = var.db_user
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

module "bastion" {
  source = "./modules/compute_engine"

  project_name = var.project_name
  subnetwork   = module.vpc.bastion_subnet
  sa_email     = module.iam.bastion_sa_email
  vpc          = module.vpc.vpc_link
}
