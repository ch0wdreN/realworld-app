#!/usr/bin/env bash

: "${PROJECT_NAME:?PROJECT_NAME is not set}"

INSTANCE="${PROJECT_NAME}-db-instance"
CONNECTION_NAME=$(gcloud sql instances list --format='json' | jq -r ".[] | select(.name == \"${INSTANCE}\") | .connectionName")
VM_NAME="${PROJECT_NAME}-bastion-instance"
LOCAL_PORT=5678
REMOTE_PORT=5432

CHECK_CMD="ps aux | grep 'cloud-sql-proxy.*--port 5432' | grep -v grep || echo 'NOT_RUNNING'"
status=$(gcloud compute ssh "${VM_NAME}" --command="${CHECK_CMD}")

if [[ "$status" == "NOT_RUNNING" ]]; then
  echo "start and port forward ..."
  gcloud compute ssh "${VM_NAME}" \
    --project="${PROJECT_ID}" \
    --command="cloud-sql-proxy \
        --address 0.0.0.0 \
        --port ${REMOTE_PORT} \
        --private-ip \
        ${CONNECTION_NAME}" \
    -- -L ${LOCAL_PORT}:localhost:${REMOTE_PORT}
else
  echo "port forwarding ..."
  gcloud compute ssh "${VM_NAME}" \
    --project="${PROJECT_ID}" \
    -- -NL ${LOCAL_PORT}:localhost:${REMOTE_PORT}
fi
