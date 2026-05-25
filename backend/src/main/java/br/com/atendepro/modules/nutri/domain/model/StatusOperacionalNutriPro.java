package br.com.atendepro.modules.nutri.domain.model;

public enum StatusOperacionalNutriPro {
    OPERACIONAL("Operacional", "Nutri Pro pronto para acompanhar pacientes usando o nucleo comum."),
    CONFIGURACAO_PENDENTE("Configuracao pendente", "Cadastre pacientes e servicos de nutricao para ativar a rotina operacional.");

    private final String rotulo;
    private final String mensagem;

    StatusOperacionalNutriPro(String rotulo, String mensagem) {
        this.rotulo = rotulo;
        this.mensagem = mensagem;
    }

    public String rotulo() {
        return rotulo;
    }

    public String mensagem() {
        return mensagem;
    }

    public static StatusOperacionalNutriPro definir(long pacientesAtivos, long servicosAtivos) {
        if (pacientesAtivos > 0 || servicosAtivos > 0) {
            return OPERACIONAL;
        }
        return CONFIGURACAO_PENDENTE;
    }
}
