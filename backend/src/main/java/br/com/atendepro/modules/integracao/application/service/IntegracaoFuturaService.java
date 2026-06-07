package br.com.atendepro.modules.integracao.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.integracao.application.port.in.ConsultarStatusWhatsAppUseCase;
import br.com.atendepro.modules.integracao.application.port.in.ConsultarStatusPagamentosUseCase;
import br.com.atendepro.modules.integracao.application.port.in.ConsultarStatusAssinaturaDigitalUseCase;
import br.com.atendepro.modules.integracao.application.result.IntegracaoFuturaStatusResult;
import br.com.atendepro.modules.integracao.domain.model.TipoIntegracaoFutura;

@Service
@Profile("!test")
public class IntegracaoFuturaService implements
        ConsultarStatusWhatsAppUseCase,
        ConsultarStatusPagamentosUseCase,
        ConsultarStatusAssinaturaDigitalUseCase {

    private final IntegracoesFuturasProperties properties;
    private final Clock clock;

    public IntegracaoFuturaService(IntegracoesFuturasProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public IntegracaoFuturaStatusResult consultarStatusWhatsApp() {
        var whatsapp = properties.whatsapp();
        return new IntegracaoFuturaStatusResult(
                TipoIntegracaoFutura.WHATSAPP,
                "WhatsApp oficial",
                whatsapp.configurada(),
                whatsapp.provedor(),
                whatsapp.ambiente(),
                whatsapp.configurada()
                        ? "WhatsApp preparado para ativacao operacional conforme credenciais configuradas."
                        : "WhatsApp permanece como integracao futura ate as credenciais oficiais serem configuradas.",
                List.of(
                        "Definir numero oficial e conta Business.",
                        "Configurar credenciais do provedor aprovado.",
                        "Validar opt-in e templates transacionais antes de disparos."
                ),
                Instant.now(clock)
        );
    }

    @Override
    public IntegracaoFuturaStatusResult consultarStatusPagamentos() {
        var pagamentos = properties.pagamentos();
        return new IntegracaoFuturaStatusResult(
                TipoIntegracaoFutura.PAGAMENTOS,
                "Pagamentos",
                pagamentos.configurada(),
                pagamentos.provedor(),
                pagamentos.ambiente(),
                pagamentos.configurada()
                        ? "Pagamentos preparado para ativacao operacional conforme credenciais configuradas."
                        : "Pagamentos permanece como integracao futura ate que o gateway esteja configurado.",
                List.of(
                        "Escolher gateway e validar escopo de meios suportados.",
                        "Configurar chaves, segredos e webhooks de evento de pagamento.",
                        "Validar idempotencia, conciliacao e tratamento de estornos antes da operacao."
                ),
                Instant.now(clock)
        );
    }

    @Override
    public IntegracaoFuturaStatusResult consultarStatusAssinaturaDigital() {
        var assinaturaDigital = properties.assinaturaDigital();
        return new IntegracaoFuturaStatusResult(
                TipoIntegracaoFutura.ASSINATURA_DIGITAL,
                "Assinatura digital avancada",
                assinaturaDigital.configurada(),
                assinaturaDigital.provedor(),
                assinaturaDigital.ambiente(),
                assinaturaDigital.configurada()
                        ? "Assinatura digital preparada para ativacao operacional com homologacao em andamento."
                        : "Assinatura digital avancada permanece como integracao futura ate parceiro e credenciais estarem prontos.",
                List.of(
                        "Selecionar parceiro de assinatura com suporte ICP-Brasil.",
                        "Configurar credenciais e certificados exigidos pelo parceiro.",
                        "Validar trilha de evidencias e auditoria antes da conclusao operacional."
                ),
                Instant.now(clock)
        );
    }
}
