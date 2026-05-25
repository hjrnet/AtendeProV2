package br.com.atendepro.shared.domain.exception;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<CampoErro> campos;

    public ValidationException(String mensagem) {
        this(mensagem, List.of());
    }

    public ValidationException(String mensagem, List<CampoErro> campos) {
        super(mensagem);
        this.campos = List.copyOf(campos);
    }

    public List<CampoErro> campos() {
        return campos;
    }

    public record CampoErro(String campo, String mensagem) {
    }
}
