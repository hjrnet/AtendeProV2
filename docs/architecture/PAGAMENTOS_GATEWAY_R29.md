# Pagamentos Gateway R29

## Decisao

O provedor aprovado para a primeira integracao real de assinaturas e pagamentos do AtendePro e o **Asaas**, em modo sandbox como padrao inicial.

Justificativa tecnica:

- atende ao mercado brasileiro do AtendePro com Pix, boleto e cartao;
- possui API para cobrancas e assinaturas recorrentes;
- opera por webhooks de cobrancas, permitindo reconciliacao assíncrona e auditavel;
- permite manter o gateway isolado em adapter externo, sem contaminar o dominio de assinaturas.

Fontes oficiais consultadas:

- Asaas visao geral: `https://docs.asaas.com/docs/visao-geral`
- Asaas assinaturas: `https://docs.asaas.com/docs/subscriptions`
- Asaas criar assinatura: `https://docs.asaas.com/docs/creating-a-subscription`
- Asaas webhooks de cobrancas: `https://docs.asaas.com/docs/webhook-para-cobrancas`

## Modo de operacao

Ambientes:

- `local`: sem chamada externa, usando contratos e stubs controlados.
- `sandbox`: chamadas reais ao sandbox Asaas, sem efeito financeiro real.
- `producao`: bloqueado ate aprovacao explicita, credenciais reais e checklist de seguranca.

Variaveis:

- `PAGAMENTOS_CONFIGURADA`
- `PAGAMENTOS_PROVEDOR=asaas`
- `PAGAMENTOS_AMBIENTE`
- `PAGAMENTOS_ASAAS_BASE_URL`
- `PAGAMENTOS_ASAAS_API_KEY`
- `PAGAMENTOS_ASAAS_WEBHOOK_TOKEN`

## Contratos de aplicacao

### Checkout

Entrada minima:

- `empresaId`
- `planoId`
- `emailResponsavel`
- `nomeResponsavel`
- `documentoResponsavel`
- `telefoneResponsavel`
- `formaPagamentoPreferida`

Saida minima:

- `checkoutId`
- `assinaturaId`
- `status`
- `urlPagamento`, quando aplicavel
- `ambiente`
- `provedor`

Regra: o checkout cria ou reutiliza cliente externo e prepara assinatura/cobranca no provedor apenas quando a integracao estiver configurada.

### Assinatura

Estados internos:

- `PREPARADA`
- `AGUARDANDO_PAGAMENTO`
- `ATIVA`
- `BLOQUEADA`
- `CANCELADA`
- `FALHA_PAGAMENTO`

Mapeamento esperado:

- criacao de assinatura no Asaas gera primeira cobranca;
- confirmacao de pagamento por webhook ativa ou mantem assinatura ativa;
- falha, atraso ou cancelamento deve atualizar a assinatura interna sem apagar historico.

### Pagamento

Estados internos:

- `PENDENTE`
- `RECEBIDO`
- `ATRASADO`
- `ESTORNADO`
- `CANCELADO`
- `FALHOU`

Campos auditaveis:

- id externo da cobranca;
- id externo da assinatura;
- valor;
- vencimento;
- forma de pagamento;
- evento recebido;
- payload bruto sanitizado;
- correlationId.

### Cancelamento e upgrade/downgrade

Cancelamento:

- registra intencao interna;
- chama adapter externo quando configurado;
- aguarda confirmacao ou registra falha recuperavel.

Upgrade/downgrade:

- cria transicao de plano interna;
- define se a mudanca e imediata ou proximo ciclo;
- evita recalculo financeiro silencioso sem regra explicita.

### Webhook

Endpoint planejado:

- `POST /api/admin-saas/pagamentos/webhooks/asaas`

Regras:

- validar segredo/token configurado;
- gravar evento bruto sanitizado antes de processar regra;
- processar de forma idempotente por id externo + tipo de evento;
- responder 2xx apenas apos persistir o recebimento;
- nunca confiar em valor, empresa ou plano sem reconciliar com assinatura interna.

Eventos iniciais:

- cobranca criada;
- pagamento recebido/confirmado;
- pagamento atrasado;
- pagamento cancelado/removido;
- estorno.

## Arquitetura

Modulo recomendado: `pagamento`.

Camadas:

- `domain`: `PagamentoAssinatura`, `StatusPagamento`, `EventoPagamentoGateway`.
- `application`: commands/results e use cases para checkout, reconciliacao e webhooks.
- `port/out`: `GatewayPagamentoPort`, `RegistrarEventoPagamentoPort`, `CarregarAssinaturaParaPagamentoPort`.
- `adapter/out/asaas`: cliente HTTP Asaas isolado.
- `adapter/in/web`: controller fino de checkout e webhook.

O modulo `assinatura` permanece dono da assinatura SaaS interna. O modulo `pagamento` nao deve expor detalhes do Asaas para controllers ou web.

## Seguranca

- Credenciais reais somente por variavel de ambiente/secret manager.
- Logs nunca devem conter API key, token de webhook, documento completo ou payload bruto sensivel.
- Webhook deve ter validacao de segredo e idempotencia.
- Produção exige `PAGAMENTOS_CONFIGURADA=true`, `PAGAMENTOS_AMBIENTE=producao` e decisao humana registrada.
- Falha de gateway nao deve derrubar login, Admin SaaS ou operacao de empresas existentes.

## Auditoria

Eventos Admin SaaS esperados:

- checkout preparado;
- assinatura externa criada;
- pagamento recebido;
- pagamento atrasado;
- cancelamento solicitado;
- webhook rejeitado;
- falha de reconciliacao.

Cada evento deve registrar empresa, plano, assinatura interna, identificador externo, status anterior, status novo e origem da acao.

## Plano tecnico proximo

1. Criar base do modulo `pagamento` com dominio, use cases e portas.
2. Criar migrations para eventos de gateway e pagamentos reconciliados.
3. Implementar adapter Asaas em sandbox com client HTTP resiliente.
4. Implementar webhook idempotente.
5. Conectar checkout Admin SaaS ao modo sandbox.
6. Validar com testes unitarios, contrato de webhook e smoke local sem producao.
