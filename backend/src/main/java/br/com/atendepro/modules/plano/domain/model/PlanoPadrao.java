package br.com.atendepro.modules.plano.domain.model;

import java.math.BigDecimal;
import java.util.Set;

public enum PlanoPadrao {
    ESTUDANTE(
            "Estudante",
            "Plano academico para estudos e primeiros atendimentos supervisionados.",
            new BigDecimal("29.90"),
            1,
            30,
            1,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS, ModuloPlano.DOCUMENTOS)
    ),
    START(
            "Start",
            "Plano inicial para profissionais independentes.",
            new BigDecimal("79.90"),
            2,
            100,
            1,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS)
    ),
    CARE(
            "Care",
            "Plano profissional com gestao operacional ampliada.",
            new BigDecimal("129.90"),
            5,
            500,
            5,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS,
                    ModuloPlano.CUSTOS, ModuloPlano.PRECIFICACAO, ModuloPlano.DOCUMENTOS, ModuloPlano.SUPORTE)
    ),
    NUTRI_PRO(
            "Nutri Pro",
            "Plano vertical para nutricionistas.",
            new BigDecimal("169.90"),
            5,
            1000,
            5,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS,
                    ModuloPlano.CUSTOS, ModuloPlano.PRECIFICACAO, ModuloPlano.DOCUMENTOS, ModuloPlano.NUTRI_PRO)
    ),
    BEAUTY_PRO(
            "Beauty Pro",
            "Plano vertical para estetica e beleza.",
            new BigDecimal("169.90"),
            5,
            1000,
            5,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS,
                    ModuloPlano.CUSTOS, ModuloPlano.PRECIFICACAO, ModuloPlano.ESTOQUE,
                    ModuloPlano.EQUIPAMENTOS, ModuloPlano.BEAUTY_PRO)
    ),
    BIOMED_PRO(
            "Biomed Pro",
            "Plano vertical para biomedicina estetica.",
            new BigDecimal("189.90"),
            5,
            1000,
            5,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS,
                    ModuloPlano.CUSTOS, ModuloPlano.PRECIFICACAO, ModuloPlano.ESTOQUE,
                    ModuloPlano.EQUIPAMENTOS, ModuloPlano.BIOMED_PRO)
    ),
    FISIO_PRO(
            "Fisio Pro",
            "Plano vertical para fisioterapia.",
            new BigDecimal("169.90"),
            5,
            1000,
            5,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS,
                    ModuloPlano.CUSTOS, ModuloPlano.PRECIFICACAO, ModuloPlano.DOCUMENTOS, ModuloPlano.FISIO_PRO)
    ),
    BUSINESS(
            "Business",
            "Plano para clinicas e equipes em crescimento.",
            new BigDecimal("249.90"),
            15,
            5000,
            15,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS,
                    ModuloPlano.CUSTOS, ModuloPlano.PRECIFICACAO, ModuloPlano.ESTOQUE,
                    ModuloPlano.EQUIPAMENTOS, ModuloPlano.DOCUMENTOS, ModuloPlano.SUPORTE)
    ),
    SPACES(
            "Spaces",
            "Plano para espacos compartilhados e sublocacao.",
            new BigDecimal("299.90"),
            20,
            5000,
            30,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.DASHBOARD,
                    ModuloPlano.SUBLOCACAO, ModuloPlano.SPACES, ModuloPlano.SUPORTE)
    ),
    PREMIUM(
            "Premium",
            "Plano completo para operacoes profissionais maduras.",
            new BigDecimal("399.90"),
            50,
            100000,
            50,
            modulosComuns(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS,
                    ModuloPlano.CUSTOS, ModuloPlano.PRECIFICACAO, ModuloPlano.ESTOQUE,
                    ModuloPlano.EQUIPAMENTOS, ModuloPlano.DOCUMENTOS, ModuloPlano.SUBLOCACAO,
                    ModuloPlano.NUTRI_PRO, ModuloPlano.BEAUTY_PRO, ModuloPlano.BIOMED_PRO,
                    ModuloPlano.FISIO_PRO, ModuloPlano.SPACES, ModuloPlano.SUPORTE)
    );

    private final String nome;
    private final String descricao;
    private final BigDecimal valorMensal;
    private final int limiteUsuarios;
    private final int limiteClientes;
    private final int limiteProfissionais;
    private final Set<ModuloPlano> modulos;

    PlanoPadrao(
            String nome,
            String descricao,
            BigDecimal valorMensal,
            int limiteUsuarios,
            int limiteClientes,
            int limiteProfissionais,
            Set<ModuloPlano> modulos
    ) {
        this.nome = nome;
        this.descricao = descricao;
        this.valorMensal = valorMensal;
        this.limiteUsuarios = limiteUsuarios;
        this.limiteClientes = limiteClientes;
        this.limiteProfissionais = limiteProfissionais;
        this.modulos = modulos;
    }

    public String codigo() {
        return name();
    }

    public String nome() {
        return nome;
    }

    public String descricao() {
        return descricao;
    }

    public BigDecimal valorMensal() {
        return valorMensal;
    }

    public int limiteUsuarios() {
        return limiteUsuarios;
    }

    public int limiteClientes() {
        return limiteClientes;
    }

    public int limiteProfissionais() {
        return limiteProfissionais;
    }

    public Set<ModuloPlano> modulos() {
        return modulos;
    }

    private static Set<ModuloPlano> modulosComuns(ModuloPlano... modulosEspecificos) {
        var modulos = new java.util.LinkedHashSet<ModuloPlano>();
        modulos.add(ModuloPlano.TENANT_EMPRESA);
        modulos.add(ModuloPlano.USUARIOS_PERMISSOES);
        modulos.add(ModuloPlano.DASHBOARD);
        modulos.addAll(java.util.List.of(modulosEspecificos));
        return Set.copyOf(modulos);
    }
}
