#!/usr/bin/env bash

set -euxo pipefail

: "${REGION:?REGION is not set}"
: "${PROJECT_NAME:?PROJECT_NAME is not set}"

gcloud auth configure-docker ${REGION}-docker.pkg.dev

docker buildx build \
  --platform linux/amd64 \
  -t ${REGION}-docker.pkg.dev/${PROJECT_ID}/${PROJECT_NAME}-repository/app:latest \
  --push \
  "."
