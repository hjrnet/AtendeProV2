package br.com.atendepro.modules.auth.application.port.out;

public interface HashTokenRecuperacaoSenhaPort {

    String gerarHashTokenRecuperacaoSenha(String tokenEmTexto);
}
