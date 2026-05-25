# Nutri Pro — Regras de Negócio

## Identidade e Referências

- Referências visuais externas são apenas inspiração funcional e de UX.
- Não copiar layout, marca, textos, identidade visual, composição visual ou design de terceiros.
- O AtendePro deve manter identidade própria, premium, limpa, moderna e adequada à saúde.

## Responsabilidade Técnica

- O sistema apoia o nutricionista, mas não decide conduta clínica automaticamente.
- Prescrições, planos, solicitações e documentos são responsabilidade técnica do nutricionista.
- Cálculos de TMB, GEB, GET, IMC, energia e macronutrientes são estimativos e devem ser validados pelo profissional.

## Tenant e Propriedade dos Dados

- Dados de pacientes, planos, avaliações, alimentos personalizados, suplementos personalizados e documentos pertencem à empresa/tenant.
- Usuários restritos só podem acessar dados da própria empresa.
- Bases globais, quando existirem, não podem ser sobrescritas por cadastros personalizados.
- Itens personalizados devem ficar marcados como personalizados e reutilizáveis dentro do tenant.

## Paciente e Prontuário

- O paciente deve ter status ativo/inativo.
- O prontuário nutricional deve centralizar histórico de planos, avaliações, documentos, exames, prescrições e diário.
- Dados clínicos e nutricionais devem ser tratados como sensíveis.
- O Menu Rápido Nutri Pro deve apenas iniciar fluxos oficiais ou indicar recursos planejados; ele não deve criar registro incompleto nem pular validações do fluxo final.
- Ações prioritárias do menu devem destacar gastos energéticos, exames laboratoriais e plano alimentar.
- Ações futuras devem aparecer como `Em breve` ou `Planejada`, sem copiar experiência visual de terceiros nem prometer execução indisponível.

## Plano Alimentar

- Um paciente pode ter múltiplos planos alimentares.
- Apenas um plano deve ser tratado como ativo principal por paciente em uma mesma estratégia, salvo regra futura de ciclos simultâneos.
- Planos devem possuir status rascunho, ativo, substituído ou arquivado.
- Duplicação e carregamento de plano anterior devem preservar histórico e criar nova versão/registro conforme desenho técnico futuro.
- Plano arquivado não deve aparecer como plano ativo do paciente.

## Refeições e Itens

- Refeições possuem ordem, horário, nome e composição.
- Alimentos e suplementos devem calcular nutrientes proporcionalmente à quantidade informada quando houver composição nutricional.
- Refeições personalizadas devem ser permitidas.
- Substituições devem deixar claro que são alternativas orientadas pelo profissional.

## Cálculos Nutricionais

- Energia total diária é soma das refeições e itens com valor energético.
- Macronutrientes totais são soma proporcional dos itens do plano.
- Percentuais de proteína, carboidrato e lipídio devem ser calculados a partir da distribuição energética quando houver dados suficientes.
- Gramas por kg dependem do peso do paciente em avaliação vigente.
- O nutricionista deve escolher se macros/calorias aparecem no PDF do paciente.

## Avaliação Antropométrica

- IMC deve ser calculado a partir de peso e altura quando ambos existirem.
- Avaliações devem manter histórico por paciente.
- Comparações e gráficos devem considerar datas e origem dos dados.
- Campos avançados como dobras, bioimpedância e fotos ficam para fases futuras.

## Gasto Energético

- Fórmulas iniciais devem ser documentadas quando implementadas.
- Fórmulas devem ser configuráveis em fase futura.
- O sistema não deve prescrever meta energética sem confirmação do nutricionista.

## Solicitação de Exames

- Solicitações devem manter histórico por paciente.
- Duplicar solicitação anterior deve gerar novo registro, não alterar a anterior.
- PDF deve usar carimbo, CRN e assinatura quando disponíveis.

## Prescrições

- Prescrição fitoterápica só deve ser habilitada quando o profissional estiver apto/habilitado conforme configuração futura.
- Documentos nutricionais devem usar o módulo de documentos profissionais quando possível.
- QR Code e assinatura digital avançada ficam para fases futuras.

## Plano Estudante

- Documentos do Plano Estudante devem conter marca d'água acadêmica.
- Deve aparecer indicação de documento para fins acadêmicos.
- Plano Estudante deve limitar pacientes, planos e documentos conforme política comercial.
- Documento oficial sem CRN não deve ser permitido quando a regra profissional exigir CRN.
- Comprovação de estudante fica para fase comercial futura.

## App do Paciente

- O paciente deve visualizar somente seus próprios dados.
- Plano, lista de compras, diário, lembretes, metas e chat devem respeitar consentimento e escopo habilitado.
- Fotos de refeições e diário alimentar devem ser tratados como dados sensíveis.

## Relatórios

- Relatórios devem respeitar tenant.
- Exportações futuras devem evitar vazamento de dados entre empresas.
- Dados agregados devem preservar privacidade do paciente sempre que possível.
