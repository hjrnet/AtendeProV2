package br.com.atendepro.modules.integracao.application.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.integracoes")
public record IntegracoesFuturasProperties(
        WhatsApp whatsapp,
        Pagamentos pagamentos,
        AssinaturaDigital assinaturaDigital
) {

    public IntegracoesFuturasProperties {
        whatsapp = whatsapp == null ? new WhatsApp(false, "oficial", "sandbox") : whatsapp;
        pagamentos = pagamentos == null ? new Pagamentos(false, "gateway-futuro", "sandbox") : pagamentos;
        assinaturaDigital = assinaturaDigital == null
                ? new AssinaturaDigital(false, "parceiro-futuro", "sandbox")
                : assinaturaDigital;
    }

    public record WhatsApp(
            boolean configurada,
            String provedor,
            String ambiente
    ) {

        public WhatsApp {
            provedor = provedor == null || provedor.isBlank() ? "oficial" : provedor.trim();
            ambiente = ambiente == null || ambiente.isBlank() ? "sandbox" : ambiente.trim();
        }
    }

    public record Pagamentos(
            boolean configurada,
            String provedor,
            String ambiente
    ) {

        public Pagamentos {
            provedor = provedor == null || provedor.isBlank() ? "gateway-futuro" : provedor.trim();
            ambiente = ambiente == null || ambiente.isBlank() ? "sandbox" : ambiente.trim();
        }
    }

    public record AssinaturaDigital(
            boolean configurada,
            String provedor,
            String ambiente
    ) {

        public AssinaturaDigital {
            provedor = provedor == null || provedor.isBlank() ? "parceiro-futuro" : provedor.trim();
            ambiente = ambiente == null || ambiente.isBlank() ? "sandbox" : ambiente.trim();
        }
    }
}
