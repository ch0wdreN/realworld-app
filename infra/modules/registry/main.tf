resource "google_project_service" "registry" {
  project = var.project_id
  service = "artifactregistry.googleapis.com"
}

resource "google_artifact_registry_repository" "repository" {
  depends_on = [google_project_service.registry]

  format        = "DOCKER"
  repository_id = join("-", [var.project_name, "repository"])
  location      = var.region
}
