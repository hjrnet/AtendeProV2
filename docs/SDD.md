# SDD — AtendePro Profissional Completo

## Visão

O AtendePro é uma plataforma SaaS multiárea para profissionais de serviço, clínicas, consultórios, salões, espaços compartilhados e profissionais autônomos.

A plataforma une:

1. atendimento;
2. acompanhamento;
3. documentos profissionais;
4. gestão operacional;
5. custo real;
6. precificação;
7. sublocação;
8. planos e assinaturas;
9. suporte;
10. Admin SaaS;
11. app web e mobile.

## Posicionamento

AtendePro ajuda profissionais a atender melhor, organizar clientes, emitir documentos, controlar custos e descobrir quanto cobrar para ter lucro real.

## Estratégia de construção

Não há foco em MVP reduzido nesta versão. O projeto será construído como SaaS profissional completo, porém com entregas progressivas por releases.

## Stack

Backend: Spring Boot + Java 21 + PostgreSQL + Liquibase + Hexagonal Architecture.
Web: Next.js + React + TypeScript + Tailwind + shadcn/ui.
Mobile: Expo + React Native.

## Núcleo comum

- Auth
- Multiempresa/Tenant
- Usuários e permissões
- Planos e assinaturas
- Admin SaaS
- Clientes/Pacientes
- Agenda
- Serviços/Procedimentos
- Custos
- Precificação
- Estoque
- Equipamentos
- Sublocação
- Documentos profissionais
- Carimbo profissional
- Relatórios/PDF
- Chamados/Suporte
- Dashboard
- Notificações

## Verticais profissionais

- Nutri Pro
- Beauty Pro
- Biomed Pro
- Fisio Pro
- Spaces
- Psico Pro futuro
- Fono Pro futuro
- Farmácia Clínica futuro
- Odonto Pro futuro
- Terapias e Saúde Integrativa futuro

## Regras críticas

- Dados isolados por tenant.
- Documentos profissionais sempre vinculados a profissional e conselho.
- Plano Estudante gera documentos com marca d'água.
- Sistema não substitui responsabilidade técnica profissional.
- Cálculos financeiros usam BigDecimal.
- Nomes de negócio em português.
