# Padroes de Ambiente

## Arquivos

- `.env.example`: variaveis raiz para Docker Compose, backend e web.
- `.env`: arquivo local ignorado pelo Git.
- `backend/src/main/resources/application.yml`: configuracao comum.
- `backend/src/main/resources/application-local.yml`: profile local.
- `backend/src/main/resources/application-test.yml`: profile de testes.
- `web/.env.example`: variaveis publicas do Next.js.

## Profiles

- `local`: profile padrao para desenvolvimento.
- `test`: profile para testes automatizados.

## Criacao do ambiente local

```bash
cp .env.example .env
docker compose up -d --build
docker compose ps
```

No Windows PowerShell, copie manualmente se `cp` nao estiver disponivel:

```powershell
Copy-Item .env.example .env
```

## Observacao sobre npm proxy

Se a maquina possuir `proxy` ou `https-proxy` npm apontando para um servico local indisponivel, use:

```bash
corepack pnpm --config.proxy=null --config.https-proxy=null install
```
