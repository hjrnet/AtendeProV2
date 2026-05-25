package br.com.atendepro.modules.auth.application.command;

import java.util.Set;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.PoliticaSenha;

public record CadastrarUsuarioBootstrapCommand(
        String nome,
        EmailUsuario email,
        String senhaEmTexto,
        Set<PerfilAcesso> perfis
) {

    public CadastrarUsuarioBootstrapCommand {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (email == null) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
        PoliticaSenha.validarSenhaForte(senhaEmTexto, "senha bootstrap");
        perfis = Set.copyOf(perfis);
        if (perfis.isEmpty()) {
            throw new IllegalArgumentException("ao menos um perfil e obrigatorio");
        }
    }
}
