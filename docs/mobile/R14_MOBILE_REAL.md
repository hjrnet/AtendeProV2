# R14 - Mobile real e governanca

A R14 consolida a transicao do mobile de demonstracao para base operacional incremental.

## Entregas tecnicas

- Sessao autenticada persistida no app mobile.
- Endpoint backend `/api/auth/me` para validar token vigente.
- Home mobile com estado de sessao e saida segura.
- Painel profissional reduzindo dependencias de mock para agenda, clientes, documentos e mensagens reais.
- Evolucao profissional baseada em carteira de pacientes/clientes real.
- Agenda preparada para filtrar por `clientePacienteId`.

## Limites conhecidos

- Refresh token automatico no mobile fica para R20.
- Guard global de rotas protegidas fica para R20.
- UX final de jornada paciente Nutri fica para R21.
- Beauty estoque/validade/pos-procedimento fica para R22.