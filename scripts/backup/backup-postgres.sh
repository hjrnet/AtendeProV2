#!/usr/bin/env bash

set -euo pipefail

: "${POSTGRES_HOST:?Defina POSTGRES_HOST}"
: "${POSTGRES_PORT:?Defina POSTGRES_PORT}"
: "${POSTGRES_DB:?Defina POSTGRES_DB}"
: "${POSTGRES_USER:?Defina POSTGRES_USER}"
: "${POSTGRES_PASSWORD:?Defina POSTGRES_PASSWORD}"

BACKUP_DIR="${BACKUP_DIR:-$(pwd)/backup}"
BACKUP_MAX_FILES="${BACKUP_MAX_FILES:-14}"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
BACKUP_FILE="${BACKUP_DIR}/atendepro_${TIMESTAMP}.dump"

mkdir -p "$BACKUP_DIR"

export PGPASSWORD="${POSTGRES_PASSWORD}"
pg_isready -h "${POSTGRES_HOST}" -p "${POSTGRES_PORT}" -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" >/dev/null

pg_dump \
  --host="${POSTGRES_HOST}" \
  --port="${POSTGRES_PORT}" \
  --username="${POSTGRES_USER}" \
  --format=custom \
  --no-owner \
  --no-acl \
  --file="${BACKUP_FILE}" \
  "${POSTGRES_DB}"

ls -1t "${BACKUP_DIR}"/atendepro_*.dump 2>/dev/null | \
  tail -n +$(("${BACKUP_MAX_FILES}" + 1)) | xargs -r rm -f

echo "Backup concluído: ${BACKUP_FILE}"
