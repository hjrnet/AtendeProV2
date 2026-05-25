# Web AtendePro

Frontend web do AtendePro com Next.js, TypeScript, Tailwind CSS e base shadcn/ui.

Design obrigatório: SaaS premium, mobile-first, limpo, profundo e moderno.

## Desenvolvimento local

```bash
corepack pnpm install
corepack pnpm lint
corepack pnpm build
corepack pnpm dev --host 0.0.0.0
```

Se a maquina tiver proxy npm local invalido, rode a instalacao com override:

```bash
corepack pnpm --config.proxy=null --config.https-proxy=null install
```

## API client

O client compartilhado fica em `web/lib/api` e usa `NEXT_PUBLIC_API_URL` como base URL.
