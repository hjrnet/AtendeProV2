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
