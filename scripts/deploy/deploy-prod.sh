#!/usr/bin/env bash

set -euo pipefail

PASTA_INFRA="${PASTA_INFRA:-$(pwd)/infra}"
DOCKER_COMPOSE="${DOCKER_COMPOSE:-docker compose}"
DOCKER_COMPOSE_FILE="${DOCKER_COMPOSE_FILE:-${PASTA_INFRA}/docker-compose.prod.yml}"

if [[ ! -f "${DOCKER_COMPOSE_FILE}" ]]; then
  echo "Arquivo do compose não encontrado: ${DOCKER_COMPOSE_FILE}"
  exit 1
fi

if [[ -f "${PASTA_INFRA}/.env.production" ]]; then
  echo "Carregando ${PASTA_INFRA}/.env.production ..."
  set -a
  # shellcheck source=/dev/null
  source "${PASTA_INFRA}/.env.production"
  set +a
fi

echo "Subindo produção com compose ${DOCKER_COMPOSE_FILE} ..."
${DOCKER_COMPOSE} -f "${DOCKER_COMPOSE_FILE}" pull
${DOCKER_COMPOSE} -f "${DOCKER_COMPOSE_FILE}" up -d
${DOCKER_COMPOSE} -f "${DOCKER_COMPOSE_FILE}" ps
