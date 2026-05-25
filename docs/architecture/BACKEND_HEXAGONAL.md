# Arquitetura Backend — Spring Boot Hexagonal

## Estrutura base

```text
backend/src/main/java/br/com/atendepro/
├── AtendeProApplication.java
├── shared/
└── modules/
    ├── auth/
    ├── tenant/
    ├── usuario/
    ├── plano/
    ├── assinatura/
    ├── adminsaas/
    ├── cliente/
    ├── agenda/
    ├── servico/
    ├── custo/
    ├── precificacao/
    ├── estoque/
    ├── equipamento/
    ├── sublocacao/
    ├── documento/
    ├── suporte/
    ├── notificacao/
    ├── dashboard/
    ├── nutripro/
    ├── beautypro/
    ├── biomedpro/
    ├── fisiopro/
    └── spaces/
```

## Estrutura de módulo

```text
modules/<modulo>/
├── domain/
│   ├── model/
│   ├── service/
│   └── exception/
├── application/
│   ├── command/
│   ├── result/
│   ├── usecase/
│   └── port/
│       ├── in/
│       └── out/
├── adapter/
│   ├── in/web/
│   │   ├── controller/
│   │   ├── request/
│   │   ├── response/
│   │   └── mapper/
│   └── out/persistence/
│       ├── entity/
│       ├── repository/
│       ├── adapter/
│       └── mapper/
└── config/
```

## Regras

- Controller não acessa Repository.
- Controller não recebe/retorna Entity.
- Domain não conhece Spring.
- Application orquestra caso de uso.
- Adapter implementa detalhes externos.
- Money usa BigDecimal.
- Request/Response DTO são exclusivos da API.
