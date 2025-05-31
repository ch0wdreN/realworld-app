#!/usr/bin/env bash

set -euxo pipefail

: "${REGION:?REGION is not set}"
: "${PROJECT_NAME:?PROJECT_NAME is not set}"

IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${PROJECT_NAME}-repository/app:latest"

gcloud run deploy app \
  --image "${IMAGE}" \
  --region asia-northeast1 \
  --platform managed \
  --quiet
