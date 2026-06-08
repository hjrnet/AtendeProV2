# AtendePro Mobile (Expo)

## Objetivo da estrutura base (TASK-0802)

- Montar uma base operacional em `Expo + React Native + TypeScript`.
- Criar rotas iniciais de:
  - home
  - autenticação
  - área do cliente (`/cliente/*`)
  - área do profissional (`/profissional/*`)
- Organizar pasta de features futuras:
  - `agenda`
  - `mensagens`
  - `documentos`
  - `diario`
  - `evolucao`
  - `notificacoes`
- Adicionar clientes API mínimos para evolução das próximas tasks.

## Estrutura

```text
mobile/
├── app/
│   ├── _layout.tsx
│   ├── index.tsx
│   ├── auth/
│   │   └── index.tsx
│   ├── cliente/
│   │   ├── index.tsx
│   │   ├── agenda.tsx
│   │   ├── documentos.tsx
│   │   └── diario.tsx
│   ├── profissional/
│   │   ├── index.tsx
│   │   ├── agenda.tsx
│   │   ├── mensagens.tsx
│   │   └── evolucao.tsx
├── components/
├── features/
│   ├── agenda/
│   ├── mensagens/
│   ├── documentos/
│   ├── diario/
│   ├── evolucao/
│   └── notificacoes/
├── lib/
```

## Comandos

```bash
cd mobile
npm install
npm run typecheck
npm run lint
npm run start
```

