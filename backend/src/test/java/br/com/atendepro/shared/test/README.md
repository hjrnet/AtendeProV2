# Padrao de Testes Backend

## Camadas

- Testes unitarios para value objects, services e use cases.
- Testes de adapter web com controller/handler quando houver contrato HTTP.
- Testes com Testcontainers para integracoes reais com PostgreSQL.

## Comandos

```bash
mvn test
```

Testes com Testcontainers exigem Docker local disponivel.
