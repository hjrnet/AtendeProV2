package br.com.atendepro.modules.empresa.application.command;

import br.com.atendepro.modules.empresa.domain.model.DocumentoEmpresa;

public record CadastrarEmpresaCommand(
        String nomeFantasia,
        String razaoSocial,
        DocumentoEmpresa documento,
        String email,
        String telefone
) {

    public CadastrarEmpresaCommand {
        if (nomeFantasia == null || nomeFantasia.isBlank()) {
            throw new IllegalArgumentException("nome fantasia e obrigatorio");
        }
        if (documento == null) {
            throw new IllegalArgumentException("documento da empresa e obrigatorio");
        }
    }
}
