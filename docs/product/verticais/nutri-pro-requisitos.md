# Nutri Pro — Requisitos Funcionais

## RF-NUTRI-001 — Paciente Nutricional

O Nutri Pro deve estender o cadastro de clientes/pacientes do núcleo comum para suportar dados nutricionais.

Deve permitir registrar:

- dados pessoais e contatos;
- sexo;
- data de nascimento;
- local de atendimento;
- observações;
- status ativo/inativo;
- tags;
- histórico de atendimentos;
- plano alimentar ativo;
- avaliações antropométricas;
- gastos energéticos;
- documentos;
- solicitações de exames;
- prescrições;
- anamnese;
- diário alimentar;
- metas;
- vínculo com app do paciente.

A lista de pacientes deve possuir busca por nome, filtro por status, filtro por local de atendimento, ordenação, tags, ação de adicionar paciente, exportação futura e envio de recado futuro.

## RF-NUTRI-002 — Perfil e Prontuário Nutricional

O perfil do paciente deve atuar como central do atendimento nutricional.

Deve exibir:

- nome;
- idade;
- altura;
- peso;
- IMC;
- status;
- resumo nutricional;
- plano alimentar ativo;
- anamnese;
- avaliação antropométrica;
- gastos energéticos;
- histórico de documentos;
- histórico de solicitações de exames;
- histórico de prescrições;
- links para agenda, financeiro, diário, chat e materiais.

## RF-NUTRI-003 — Plano Alimentar

O nutricionista deve poder criar um ou mais planos alimentares por paciente.

Cada plano deve conter:

- paciente;
- nutricionista responsável;
- data de criação;
- objetivo do plano;
- descrição;
- dias da semana;
- status: rascunho, ativo, substituído, arquivado;
- refeições;
- horários;
- alimentos;
- suplementos/formulações;
- cálculo energético;
- distribuição de macronutrientes;
- configurações de exibição;
- versão imprimível/PDF;
- carimbo profissional;
- assinatura virtual;
- CRN.

O nutricionista deve conseguir criar, editar, duplicar, carregar plano anterior, salvar como modelo/cardápio, arquivar, inativar, imprimir, salvar em PDF, enviar por e-mail e visualizar como paciente.

## RF-NUTRI-004 — Refeições

O plano alimentar deve organizar refeições por dia, horário e composição.

Refeições padrão:

- café da manhã;
- colação;
- almoço;
- lanche da tarde;
- jantar;
- ceia;
- pré-treino;
- pós-treino;
- refeição personalizada.

Cada refeição deve conter nome, horário, alimentos, suplementos/formulações, observações, substituições, nutrientes da refeição e ações de editar, duplicar, excluir e ver nutrientes.

## RF-NUTRI-005 — Banco de Alimentos

O Nutri Pro deve possuir conceito de banco de alimentos.

Cada alimento deve conter:

- nome;
- grupo alimentar;
- unidade de medida;
- quantidade base;
- valor energético;
- proteínas;
- carboidratos;
- lipídios;
- fibras, quando aplicável;
- micronutrientes em fase futura;
- fonte/tabela, quando aplicável;
- origem padrão ou personalizada;
- status ativo/inativo.

Funcionalidades previstas: buscar, filtrar, selecionar, incluir na refeição, informar quantidade, calcular energia e macros proporcionalmente, cadastrar alimento personalizado, editar alimento personalizado e reutilizar em novos planos.

## RF-NUTRI-006 — Suplementos e Formulações

O sistema deve permitir cadastro e uso de suplementos/formulações como whey protein, creatina, ômega 3, vitamina D, multivitamínico, proteína vegetal, suplemento manipulado, formulação personalizada e fitoterápico quando habilitado.

Cada item deve conter nome, tipo, dose, unidade, energia quando aplicável, proteínas, carboidratos, lipídios, composição, posologia/orientação de uso, horário sugerido, observações e origem padrão ou personalizada.

## RF-NUTRI-007 — Itens Personalizados

Quando o nutricionista não encontrar alimento, suplemento ou formulação na base, deve poder cadastrar um novo item.

Regras:

- item personalizado pertence à empresa/profissional;
- pode ser reutilizado;
- fica marcado como personalizado;
- pode ser editado;
- não sobrescreve base global;
- futuramente pode haver aprovação/curadoria para base compartilhada.

## RF-NUTRI-008 — Energia e Macronutrientes

O plano alimentar deve calcular energia total diária, energia por refeição, proteínas, carboidratos, lipídios, percentuais de macros, gramas por kg quando aplicável, resumo diário e resumo por refeição.

A tela pode exibir barra horizontal de macros, gráfico de pizza/donut, tabela de nutrientes e resumo em kcal.

Configurações:

- exibir ou ocultar macros no PDF;
- exibir ou ocultar calorias no PDF;
- mostrar versão simplificada;
- mostrar versão técnica.

## RF-NUTRI-009 — Avaliação Antropométrica

O Nutri Pro deve registrar avaliações antropométricas do paciente.

Campos iniciais:

- peso;
- altura;
- idade;
- sexo;
- IMC;
- objetivo: perda de peso, ganho de massa, manutenção, performance, saúde;
- observações.

Campos futuros: circunferências, dobras cutâneas, bioimpedância, percentual de gordura, massa magra, massa gorda, água corporal, gordura visceral e fotos de evolução.

Funcionalidades: criar, editar, comparar, manter histórico e exibir gráficos de evolução.

## RF-NUTRI-010 — Gasto Energético

Com base na avaliação antropométrica, o sistema deve estimar taxa metabólica basal, gasto energético basal, gasto energético total e necessidades energéticas para perda de peso, ganho de massa ou manutenção.

Os cálculos são estimativos, fórmulas devem ser configuráveis futuramente e o nutricionista valida o resultado antes de usar.

## RF-NUTRI-011 — Solicitação de Exames

O sistema deve criar solicitações de exames laboratoriais por paciente.

Cada solicitação deve conter paciente, nutricionista responsável, data, lista de exames, justificativa/observações, status, versão PDF, carimbo, CRN e assinatura virtual.

Deve manter histórico de primeira, segunda, terceira e demais solicitações.

## RF-NUTRI-012 — Prescrições e Documentos

O Nutri Pro deve prever documentos específicos:

- prescrição dietética;
- prescrição de suplementação;
- prescrição de formulações;
- prescrição fitoterápica quando o profissional estiver habilitado;
- orientações nutricionais.

Cada documento deve sair com dados do paciente, dados do nutricionista, data, carimbo profissional, CRN, assinatura virtual, observações e QR Code em fase futura.

## RF-NUTRI-013 — PDF do Plano Alimentar

O PDF do plano alimentar deve conter nome do paciente, dados do nutricionista, objetivo, refeições, horários, alimentos, quantidades, suplementos/formulações, observações, macros e energia quando habilitados, data, carimbo profissional, CRN, assinatura virtual e logotipo da clínica se houver.

Rodapé previsto:

- nome do nutricionista;
- CRN;
- assinatura;
- dados da clínica;
- aviso de responsabilidade profissional;
- QR Code de validação em fase futura.

## RF-NUTRI-014 — Carimbo Profissional

O Nutri Pro deve usar o cadastro profissional para gerar documentos com nome completo, profissão Nutricionista, CRN, UF, assinatura virtual, logotipo quando houver, clínica e contato profissional quando configurado.

## RF-NUTRI-015 — App do Paciente

O app/portal do paciente deve prever acesso ao plano alimentar, lista de compras, diário alimentar, fotos de refeições, lembretes, metas, evolução, conversa com nutricionista, videoconferência futura, receitas e materiais complementares.

## RF-NUTRI-016 — App do Profissional

O nutricionista deve poder ver agenda, acessar pacientes, acompanhar diário alimentar, responder mensagens, ver plano alimentar, consultar documentos, acompanhar evolução e registrar observações rápidas.

## RF-NUTRI-017 — Lista de Compras

O sistema deve gerar lista de compras baseada no plano alimentar, agrupada por categoria, editável, enviável ao paciente e com histórico.

## RF-NUTRI-018 — Receitas e Materiais

O sistema deve prever receitas, materiais educativos, listas de substituição, cards/lâminas educativas em fase futura e templates de orientação nutricional.

## RF-NUTRI-019 — Agenda, Chat e Fidelização

O Nutri Pro deve integrar agenda, lembretes, recados, chat, videoconferência futura, notificações, retorno do paciente, aniversariantes e acompanhamento de faltas.

## RF-NUTRI-020 — Relatórios Nutricionais

Relatórios previstos:

- pacientes atendidos;
- planos alimentares criados;
- consultas;
- evolução de pacientes;
- perfil dos pacientes;
- sexo;
- faixa etária/ciclo de vida;
- ativos/inativos;
- ranking de pacientes;
- exportação futura.

## RF-NUTRI-021 — Plano Estudante

Se a empresa/usuário estiver no Plano Estudante:

- documentos devem sair com marca d'água;
- exibir "Documento para fins acadêmicos";
- limitar pacientes;
- limitar planos;
- limitar documentos;
- não permitir documento oficial sem CRN;
- exigir comprovação de estudante em fase comercial.
