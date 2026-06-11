package br.com.atendepro.modules.adminsaas.domain.model;

import java.util.List;

public enum PerfilDemoAdminSaas {
    NUTRI(
            "Nutri Pro",
            List.of(
                    "Repopular empresa demo Nutri e assinatura ativa.",
                    "Recriar pacientes, agenda, servicos e simulacoes de precificacao.",
                    "Atualizar credencial Karol Nutricionista Demo."
            ),
            List.of("karol.nutri@atendepro.local / AtendePro@123")
    ),
    BEAUTY(
            "Beauty Pro",
            List.of(
                    "Repopular empresa demo Beauty e assinatura ativa.",
                    "Recriar clientes, protocolos, estoque, produtos com validade e margem.",
                    "Atualizar credencial Ana Esteticista Demo."
            ),
            List.of("ana.estetica@atendepro.local / AtendePro@123")
    ),
    GESTOR(
            "Gestor",
            List.of(
                    "Repopular tenants demo multi-area para comparacao executiva.",
                    "Recriar agenda, custos, servicos e indicadores de operacao.",
                    "Atualizar credenciais de gestores por vertical."
            ),
            List.of(
                    "admin@atendepro.local / AtendePro@123",
                    "paula.spaces@atendepro.local / AtendePro@123"
            )
    ),
    INVESTIDOR(
            "Investidor",
            List.of(
                    "Repopular planos, assinaturas e trials demo.",
                    "Recalcular dados para MRR, churn, planos vendidos e tracao por vertical.",
                    "Preservar aviso de que dados financeiros sao simulados."
            ),
            List.of("admin@atendepro.local / AtendePro@123")
    ),
    SUPORTE(
            "Suporte",
            List.of(
                    "Repopular base demo para diagnostico e suporte.",
                    "Reativar empresas demo bloqueadas indevidamente.",
                    "Atualizar usuarios e permissoes locais de apresentacao."
            ),
            List.of("admin@atendepro.local / AtendePro@123")
    );

    private final String rotulo;
    private final List<String> etapas;
    private final List<String> credenciais;

    PerfilDemoAdminSaas(String rotulo, List<String> etapas, List<String> credenciais) {
        this.rotulo = rotulo;
        this.etapas = List.copyOf(etapas);
        this.credenciais = List.copyOf(credenciais);
    }

    public String rotulo() {
        return rotulo;
    }

    public List<String> etapas() {
        return etapas;
    }

    public List<String> credenciais() {
        return credenciais;
    }
}
