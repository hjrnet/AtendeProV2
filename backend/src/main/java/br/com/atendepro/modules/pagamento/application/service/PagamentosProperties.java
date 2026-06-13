package br.com.atendepro.modules.pagamento.application.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.pagamentos")
public record PagamentosProperties(
        boolean configurada,
        String provedor,
        String ambiente,
        String asaasBaseUrl,
        String asaasApiKey,
        String asaasWebhookToken
) {

    public boolean producaoSolicitada() {
        return "producao".equalsIgnoreCase(ambiente);
    }

    public boolean sandboxSolicitado() {
        return "sandbox".equalsIgnoreCase(ambiente);
    }
}
