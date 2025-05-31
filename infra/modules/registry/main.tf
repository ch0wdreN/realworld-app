resource "google_project_service" "registry" {
  service = "artifactregistry.googleapis.com"
}

resource "google_project_service" "scanning" {
  service = "containerscanning.googleapis.com"
}

resource "google_artifact_registry_repository" "repository" {
  depends_on = [google_project_service.registry]

  format        = "DOCKER"
  repository_id = join("-", [var.project_name, "repository"])
  location      = var.region
}
