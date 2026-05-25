# Backend AtendePro

Backend Spring Boot do AtendePro.

Arquitetura obrigatória: Hexagonal + Monolito Modular.

## Desenvolvimento local

```bash
mvn test
mvn spring-boot:run
```

O projeto usa `backend/.mvn/settings.xml` para resolver dependencias pelo Maven Central, evitando dependencia de mirrors globais da maquina.

Endpoint inicial de status:

```text
GET /api/status
```

## Banco e migrations

O profile `local` usa PostgreSQL local e executa Liquibase automaticamente.

```bash
mvn spring-boot:run
```

Para aplicar migrations diretamente:

```bash
mvn -Dliquibase.url=jdbc:postgresql://localhost:5433/atendepro -Dliquibase.username=atendepro -Dliquibase.password=atendepro liquibase:update
```

## OpenAPI

- JSON: `GET /api/docs`
- Swagger UI: `/swagger-ui`

## Shared kernel

Value objects e utilitarios compartilhados ficam em `br.com.atendepro.shared`, incluindo `Money`, `Percentual`, `BaseId`, `Periodo`, `Paginacao` e `ResultadoPaginado`.

## Erros de API

Erros globais sao tratados por `GlobalExceptionHandler`, com respostas padronizadas por `ErroApiResponse`.

## Testes

```bash
mvn test
```

O padrao de testes usa JUnit 5, AssertJ, Mockito via Spring Boot Test e Testcontainers para PostgreSQL.
