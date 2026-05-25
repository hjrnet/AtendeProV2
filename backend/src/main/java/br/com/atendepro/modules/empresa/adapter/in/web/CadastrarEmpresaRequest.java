package br.com.atendepro.modules.empresa.adapter.in.web;

import br.com.atendepro.modules.empresa.application.command.CadastrarEmpresaCommand;
import br.com.atendepro.modules.empresa.domain.model.DocumentoEmpresa;
import jakarta.validation.constraints.NotBlank;

public record CadastrarEmpresaRequest(
        @NotBlank(message = "nome fantasia e obrigatorio")
        String nomeFantasia,
        String razaoSocial,
        @NotBlank(message = "documento e obrigatorio")
        String documento,
        String email,
        String telefone
) {

    CadastrarEmpresaCommand paraCommand() {
        return new CadastrarEmpresaCommand(
                nomeFantasia,
                razaoSocial,
                DocumentoEmpresa.de(documento),
                email,
                telefone
        );
    }
}
