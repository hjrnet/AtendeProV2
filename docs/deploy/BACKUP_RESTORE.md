# Backup e restore do PostgreSQL

## Objetivo

Definir estratégia operacional de backup e restore para produção com foco em recuperação rápida do banco de dados do AtendePro.

## Arquivos adicionados

- `scripts/backup/backup-postgres.sh`
- `scripts/backup/restore-postgres.sh`

## Pré-requisitos

- PostgreSQL 16+ disponível no host.
- `pg_dump`, `pg_restore` e `pg_isready` acessíveis no PATH (pacote `postgresql-client`).
- Variáveis de ambiente de conexão:
  - `POSTGRES_HOST`
  - `POSTGRES_PORT`
  - `POSTGRES_DB`
  - `POSTGRES_USER`
  - `POSTGRES_PASSWORD`

## Backup (recomendado)

1. Conceda permissão de execução:

```bash
chmod +x scripts/backup/backup-postgres.sh
```

2. Execute:

```bash
./scripts/backup/backup-postgres.sh
```

### O que é gerado

- O script grava arquivo `.dump` no diretório `backup`.
- O nome segue padrão: `atendepro_YYYYMMDD_HHMMSS.dump`.
- `BACKUP_MAX_FILES` mantém retenção configurável dos mais recentes.

## Restore (em ambiente de manutenção)

### Restaurar backup específico

```bash
./scripts/backup/restore-postgres.sh /caminho/do/backup.dump
```

### Restaurar backup mais recente

```bash
./scripts/backup/restore-postgres.sh
```

## Recomendação para produção

- Executar backup automático com intervalo `0 3 * * *` (3h da manhã).
- Criar retention para retenção de 7 dias no mínimo.
- Armazenar cópias dos arquivos em bucket/S3 ou volume externo do VPS.
- Testar restore em ambiente staging ao menos uma vez por mês.

## Estratégia de recuperação de incidente

1. Parar temporariamente serviços de escrita.
2. Executar restore para ambiente isolado de validação.
3. Validar integridade e dados essenciais.
4. Fazer promote para produção e retomar escrita após healthcheck OK.
