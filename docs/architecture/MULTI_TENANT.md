# Multiempresa / Multi-tenant

## Estratégia

Cada empresa cliente possui dados isolados por `tenant_id` ou `empresa_id`.

## Regras

- Usuário comum sempre pertence a uma empresa.
- Super Admin pode acessar visão global.
- Queries devem filtrar por tenant.
- Documentos, clientes, agenda, custos e serviços pertencem ao tenant.
- Endpoint nunca deve permitir acessar dados de outro tenant.

## Entidades centrais

- Empresa
- Usuario
- Perfil
- Permissao
- Assinatura
- Plano
