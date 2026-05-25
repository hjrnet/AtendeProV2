package br.com.atendepro.modules.auth.domain.exception;

public class PermissaoNegadaException extends RuntimeException {

    private final String codigo;

    public PermissaoNegadaException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }
}
