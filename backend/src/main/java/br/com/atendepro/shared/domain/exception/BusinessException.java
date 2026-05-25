package br.com.atendepro.shared.domain.exception;

public class BusinessException extends RuntimeException {

    private final String codigo;

    public BusinessException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }
}
