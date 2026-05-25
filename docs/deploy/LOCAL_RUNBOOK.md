# Local Runbook

## Infra

```bash
cp .env.example .env
docker compose up -d --build
docker compose ps
```

O Compose nao fixa `container_name`; os nomes ficam sob controle do projeto Docker Compose para evitar conflito entre clones ou ambientes locais diferentes.

Detalhes de variaveis e profiles ficam em `docs/deploy/ENVIRONMENT.md`.

## Backend

```bash
cd backend
mvn test
mvn spring-boot:run
```

`mvn test` pode iniciar Testcontainers e exige Docker local disponivel.

## Web

```bash
cd web
corepack pnpm install
corepack pnpm lint
corepack pnpm build
corepack pnpm dev --host 0.0.0.0
```

## Teste no celular

1. Descobrir IP da máquina.
2. Configurar `NEXT_PUBLIC_API_URL=http://IP_DA_MAQUINA:8080`.
3. Acessar `http://IP_DA_MAQUINA:3000`.
4. Garantir CORS para origem do IP.
