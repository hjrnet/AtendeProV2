package br.com.atendepro.modules.auth.application.command;

import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PoliticaSenha;

public record CadastrarAdministradorEmpresaCommand(
        UUID empresaId,
        String nome,
        EmailUsuario email,
        String senha
) {

    public CadastrarAdministradorEmpresaCommand {
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (email == null) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
        PoliticaSenha.validarSenhaForte(senha, "senha do administrador");
    }
}
