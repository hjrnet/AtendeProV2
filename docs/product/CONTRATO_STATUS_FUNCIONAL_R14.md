# Contrato de status funcional - R14

A R14 formaliza a linguagem usada para dizer o que esta pronto, o que e demonstracao e o que ainda e backlog. Isso evita vender como pronto algo que ainda e preparacao tecnica.

## Estados oficiais

- OPERACIONAL_REAL: fluxo com tela ou endpoint real, persistencia, tenant e validacao basica executada.
- PREPARADO_TECNICAMENTE: base tecnica existe, mas ainda depende de integracao, UX final ou validacao operacional.
- CATALOGO_DEMO: dado, tela ou fluxo usado para demonstracao, sem promessa de operacao completa.
- ESPECIFICACAO: requisito documentado e priorizado, ainda sem implementacao.
- FUTURO: ideia reconhecida, fora do roadmap imediato.

## Aplicacao atual

- Auth web/backend/mobile: OPERACIONAL_REAL.
- Multiempresa/tenant nos endpoints principais: OPERACIONAL_REAL com evolucao futura para auditoria mais profunda.
- Portal mobile paciente/profissional: PREPARADO_TECNICAMENTE, com telas reais e dependencias reduzidas de mock.
- Nutri Pro pos-venda: ESPECIFICACAO priorizada para R21.
- Beauty Pro estoque/validade/pos-venda: ESPECIFICACAO priorizada para R22.
- Integracoes externas futuras: FUTURO ate que exista contrato tecnico e credenciais reais.

## Regra de release

Toda nova release deve registrar o estado funcional dos entregaveis usando estes termos no status da task e no resumo da release.