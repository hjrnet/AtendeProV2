# Arquitetura Frontend — Next.js

## Stack

- Next.js
- React
- TypeScript
- Tailwind CSS
- shadcn/ui
- TanStack Query
- React Hook Form
- Zod
- Recharts
- Lucide Icons

## Estrutura

```text
web/
├── app/
│   ├── (public)/
│   ├── (auth)/
│   ├── admin/
│   ├── app/
│   └── cliente/
├── components/
│   ├── ui/
│   ├── layout/
│   ├── charts/
│   └── feedback/
├── features/
│   ├── auth/
│   ├── dashboard/
│   ├── empresas/
│   ├── planos/
│   ├── assinaturas/
│   ├── clientes/
│   ├── agenda/
│   ├── servicos/
│   ├── custos/
│   ├── precificacao/
│   ├── estoque/
│   ├── equipamentos/
│   ├── sublocacao/
│   ├── documentos/
│   ├── suporte/
│   └── verticais/
└── lib/
    ├── api/
    ├── auth/
    ├── formatadores/
    └── utils/
```

## Regras de UI

- Mobile-first.
- Design premium, limpo, profundo e moderno.
- Componentes reutilizáveis.
- Toda lista tem busca, scroll/paginação e estado vazio.
- Toda tela tem loading/error/empty state.
- Inputs monetários usam formato R$ Brasil.
