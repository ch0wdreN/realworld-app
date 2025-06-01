#!/usr/bin/env bash

: "${PROJECT_NAME:?PROJECT_NAME is not set}"

SECRET="${PROJECT_NAME}-sql-user-secret"
PASSWORD=$(gcloud secrets versions access latest --secret="${SECRET}")
HOST=localhost
PORT=5678
USER="app_user"
DB="realworld_db"

cat <<EOF > "${HOME}/.pgpass"
${HOST}:${PORT}:${DB}:${USER}:${PASSWORD}
EOF

chmod 600 "${HOME}/.pgpass"

pgcli -U "${USER}" --host "${HOST}" --port "${PORT}" -d "${DB}"
