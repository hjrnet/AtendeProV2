#!/usr/bin/env bash

set -euo pipefail

: "${POSTGRES_HOST:?Defina POSTGRES_HOST}"
: "${POSTGRES_PORT:?Defina POSTGRES_PORT}"
: "${POSTGRES_DB:?Defina POSTGRES_DB}"
: "${POSTGRES_USER:?Defina POSTGRES_USER}"
: "${POSTGRES_PASSWORD:?Defina POSTGRES_PASSWORD}"

BACKUP_DIR="${BACKUP_DIR:-$(pwd)/backup}"
BACKUP_FILE="${1:-}"

if [[ -z "${BACKUP_FILE}" ]]; then
  BACKUP_FILE="$(ls -1t "${BACKUP_DIR}"/atendepro_*.dump 2>/dev/null | head -n 1)"
fi

if [[ -z "${BACKUP_FILE}" || ! -f "${BACKUP_FILE}" ]]; then
  echo "Arquivo de backup não encontrado: ${BACKUP_FILE}"
  exit 1
fi

if [[ "${SKIP_CONFIRMATION:-false}" != "true" ]]; then
  echo "ATENÇÃO: restore vai sobrescrever dados de ${POSTGRES_DB} em ${POSTGRES_HOST}:${POSTGRES_PORT}."
  echo "Arquivo: ${BACKUP_FILE}"
  read -p "Continuar? (sim/N): " resposta
  if [[ "${resposta,,}" != "sim" && "${resposta,,}" != "s" ]]; then
    echo "Operação cancelada."
    exit 0
  fi
fi

export PGPASSWORD="${POSTGRES_PASSWORD}"
pg_isready -h "${POSTGRES_HOST}" -p "${POSTGRES_PORT}" -U "${POSTGRES_USER}" -d postgres >/dev/null

psql \
  --host="${POSTGRES_HOST}" \
  --port="${POSTGRES_PORT}" \
  --username="${POSTGRES_USER}" \
  --dbname=postgres \
  --set=ON_ERROR_STOP=on \
  --command="SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='${POSTGRES_DB}' AND pid <> pg_backend_pid();" >/dev/null

psql \
  --host="${POSTGRES_HOST}" \
  --port="${POSTGRES_PORT}" \
  --username="${POSTGRES_USER}" \
  --dbname=postgres \
  --set=ON_ERROR_STOP=on \
  --command="DROP DATABASE IF EXISTS \"${POSTGRES_DB}\";"

createdb \
  --host="${POSTGRES_HOST}" \
  --port="${POSTGRES_PORT}" \
  --username="${POSTGRES_USER}" \
  "${POSTGRES_DB}"

pg_restore \
  --clean \
  --if-exists \
  --no-owner \
  --no-acl \
  --host="${POSTGRES_HOST}" \
  --port="${POSTGRES_PORT}" \
  --username="${POSTGRES_USER}" \
  --dbname="${POSTGRES_DB}" \
  "${BACKUP_FILE}"

echo "Restore concluído: ${BACKUP_FILE}"
