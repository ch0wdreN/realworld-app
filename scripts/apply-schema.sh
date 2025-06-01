#!/usr/bin/env bash

: "${PROJECT_NAME:?PROJECT_NAME is not set}"
: "${PROJECT_ID:?PROJECT_ID is not set}"

SECRET="${PROJECT_NAME}-sql-user-secret"
PASSWORD=$(gcloud secrets versions access latest --secret="${SECRET}")
HOST=localhost
PORT=5678
USER="app_user"
DB="realworld_db"

atlas schema apply \
  --url "postgres://${USER}:${PASSWORD}@${HOST}:${PORT}/${DB}?sslmode=disable" \
  --to file://schema
