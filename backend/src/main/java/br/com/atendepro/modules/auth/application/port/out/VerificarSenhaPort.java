package br.com.atendepro.modules.auth.application.port.out;

public interface VerificarSenhaPort {

    boolean senhaConfere(String senhaEmTexto, String senhaHash);
}
