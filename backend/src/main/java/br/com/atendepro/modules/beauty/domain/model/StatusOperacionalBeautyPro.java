package br.com.atendepro.modules.beauty.domain.model;

public enum StatusOperacionalBeautyPro {
    OPERACIONAL("Operacional", "Beauty Pro pronto para acompanhar clientes, serviços, agenda e precificação."),
    CONFIGURACAO_PENDENTE("Configuração pendente", "Cadastre clientes e serviços de estética/beleza para ativar a rotina operacional.");

    private final String rotulo;
    private final String mensagem;

    StatusOperacionalBeautyPro(String rotulo, String mensagem) {
        this.rotulo = rotulo;
        this.mensagem = mensagem;
    }

    public String rotulo() {
        return rotulo;
    }

    public String mensagem() {
        return mensagem;
    }

    public static StatusOperacionalBeautyPro definir(long clientesAtivos, long servicosAtivos) {
        if (clientesAtivos > 0 || servicosAtivos > 0) {
            return OPERACIONAL;
        }
        return CONFIGURACAO_PENDENTE;
    }
}
