package br.com.atendepro.modules.auth.application.port.out;

public interface CriptografarSenhaPort {

    String criptografarSenha(String senhaEmTexto);
}
