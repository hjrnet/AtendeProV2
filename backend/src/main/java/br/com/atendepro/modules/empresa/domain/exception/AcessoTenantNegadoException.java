package br.com.atendepro.modules.empresa.domain.exception;

public class AcessoTenantNegadoException extends RuntimeException {

    private final String codigo;

    public AcessoTenantNegadoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }
}
