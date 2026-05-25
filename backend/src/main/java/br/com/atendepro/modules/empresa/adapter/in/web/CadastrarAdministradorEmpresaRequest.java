package br.com.atendepro.modules.empresa.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.auth.application.command.CadastrarAdministradorEmpresaCommand;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastrarAdministradorEmpresaRequest(
        @NotBlank(message = "nome e obrigatorio")
        String nome,
        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email,
        @NotBlank(message = "senha e obrigatoria")
        String senha
) {

    CadastrarAdministradorEmpresaCommand paraCommand(UUID empresaId) {
        return new CadastrarAdministradorEmpresaCommand(
                empresaId,
                nome,
                EmailUsuario.de(email),
                senha
        );
    }
}
