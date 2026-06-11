# Review Checklist

## Escopo
- [ ] Task correta executada.
- [ ] Nenhuma task futura implementada.
- [ ] Arquivos fora do escopo justificados.

## Backend
- [ ] Controller fino.
- [ ] Request/Response DTO.
- [ ] Command/Result.
- [ ] UseCase/InputPort.
- [ ] OutputPort/Adapter.
- [ ] Entidade JPA não exposta.
- [ ] BigDecimal para dinheiro.
- [ ] Testes passam.

## Frontend
- [ ] Mobile-first.
- [ ] Sem layout quebrado.
- [ ] Loading/empty/error state.
- [ ] Busca/scroll em listas.
- [ ] Design premium e limpo.

## DevOps
- [ ] Docker/local validado.
- [ ] README/runbook atualizado se necessário.

## Observability
- [ ] Eventos principais registrados quando a task/release for longa.
- [ ] Falhas, bloqueios e causas registradas em `docs/codex/observability/failures.jsonl` quando aplicável.
- [ ] Checklist automático da release gerado ou atualizado quando aplicável.
- [ ] Relatório de observabilidade gerado ao final de blocos de autopilot.

## Git
- [ ] Commit local criado se passou.
- [ ] Push realizado ou justificativa registrada.
