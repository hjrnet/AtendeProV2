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

## Gateway de pagamentos R29

A R29 define o Asaas como provedor aprovado para a primeira integracao real de assinaturas/pagamentos, sempre iniciando em sandbox e com `PAGAMENTOS_CONFIGURADA=false` por padrao local.

Variaveis previstas:

- `PAGAMENTOS_CONFIGURADA`: habilita chamadas reais ao adapter externo apenas quando `true`.
- `PAGAMENTOS_PROVEDOR`: `asaas`.
- `PAGAMENTOS_AMBIENTE`: `sandbox` ou `producao`.
- `PAGAMENTOS_ASAAS_BASE_URL`: URL da API Asaas do ambiente.
- `PAGAMENTOS_ASAAS_API_KEY`: chave secreta do provedor, nunca versionada.
- `PAGAMENTOS_ASAAS_WEBHOOK_TOKEN`: segredo para validar webhooks recebidos.

Regras:

- ambiente local e CI nao devem acionar cobranca real;
- credenciais reais nao devem ser gravadas em `.env.example`, logs, banco ou documentos;
- webhooks externos reais exigem decisao humana e configuracao explicita.
