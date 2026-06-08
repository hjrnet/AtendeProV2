# TASK-DEVOPS-001 — Configurar Testcontainers no Docker Desktop Windows

## Status

CONCLUIDA

## Contexto

Durante a validacao da R15, o `PostgreSqlContainerSmokeTest` falhava no host Windows mesmo com o Docker Desktop ativo e acessivel pelo Docker CLI.

## Causa

O Docker CLI acessava corretamente o contexto `desktop-linux`, mas o Testcontainers/docker-java recebia uma resposta incompleta do Docker Desktop ao consultar o endpoint sem API version explicita pelo named pipe local.

## Correcao aplicada

Foi adicionada a configuracao Maven `-Dapi.version=1.41` em `backend/.mvn/maven.config`.

Essa versao e conservadora, compativel com Docker Engine 20.10+ e suficiente para fazer o docker-java consultar o Docker Desktop usando API versionada.

## Validacao

Comando:

```bash
mvn test
```

Resultado:

```text
Tests run: 309, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Observacao

Nao foi necessario atualizar a versao do Testcontainers gerenciada pelo Spring Boot.
