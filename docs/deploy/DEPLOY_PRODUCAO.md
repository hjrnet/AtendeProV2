# Deploy produção (VPS / cloud)

## Estratégia recomendada

Para produção, a execução mais estável do AtendePro é via `docker compose` com:

- PostgreSQL dedicado;
- Backend Spring Boot em container;
- Frontend Next.js em container;
- Nginx como reverse proxy com HTTPS e redirecionamento de domínio;
- healthcheck + restart automático;
- backups e restore automatizados.

## Arquivos de operação

- `infra/docker-compose.prod.yml` (exemplo de orquestração)
- `infra/nginx/conf.d/atendepro.conf` (reverse proxy)
- `scripts/deploy/deploy-prod.sh` (exemplo de rotina de atualização)
- `.env.production` (segredos e variáveis de cada ambiente)

## Exemplo de .env de produção

```bash
POSTGRES_DB=atendepro
POSTGRES_USER=atendepro
POSTGRES_PASSWORD=troque_essa_senha_forte

SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/atendepro
SPRING_DATASOURCE_USERNAME=atendepro
SPRING_DATASOURCE_PASSWORD=troque_essa_senha_forte
SPRING_PROFILES_ACTIVE=prod

JWT_SECRET=troque-um-segredo-pelo-menor-64-bytes
JWT_ISSUER=atendepro
JWT_EXPIRACAO_MINUTOS=120

BACKEND_PORT=8080
WEB_PORT=3000
NEXT_PUBLIC_API_URL=https://seu-dominio.com

CORS_ALLOWED_ORIGINS=https://seu-dominio.com

BACKEND_IMAGE=ghcr.io/seu-org/atendepro-backend:latest
WEB_IMAGE=ghcr.io/seu-org/atendepro-web:latest

HTTP_PORT=80
HTTPS_PORT=443
```

## Deploy de atualização (manual)

1. Atualize `backend` e `web` para as novas tags:

```bash
cd infra
cp /caminho/infra/.env.production .
export BACKEND_IMAGE=ghcr.io/seu-org/atendepro-backend:TAG
export WEB_IMAGE=ghcr.io/seu-org/atendepro-web:TAG
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

2. Valide saúde dos serviços:

```bash
docker compose -f docker-compose.prod.yml ps
curl -fsS https://seu-dominio.com/actuator/health | jq
```

3. Execute smoke-checks da operação:

- login;
- criação de cliente/paciente;
- criação de agendamento.

## Hardening operacional recomendado

- Terminar HTTPS com certificado válido (Let's Encrypt);
- Redirecionar `http -> https`;
- Definir `RateLimit` e `Headers` de segurança (já implementados no backend);
- Manter `JWT_SECRET` e demais segredos em secrets da VPS/Cloud;
- Ativar monitoramento de CPU/memória e alertas no provider.

## Rollback

Em caso de regressão, volte para a tag anterior das imagens:

```bash
export BACKEND_IMAGE=ghcr.io/seu-org/atendepro-backend:vX
export WEB_IMAGE=ghcr.io/seu-org/atendepro-web:vX
docker compose -f docker-compose.prod.yml up -d --force-recreate
```

## Observações

- O `rolling update` depende de estratégia no CI/CD para gerar imagens com tag imutável.
- Mantenha backups (`scripts/backup`) e restore (`scripts/backup/restore-postgres.sh`) validados antes de publicar.
