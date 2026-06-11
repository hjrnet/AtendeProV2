package br.com.atendepro.modules.mobile.application.result;

import java.util.UUID;

import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;

public record EmpresaMobileResult(
        UUID id,
        String nomeFantasia,
        String razaoSocial,
        String email,
        String telefone,
        boolean ativa
) {

    public static EmpresaMobileResult de(EmpresaTenant empresa) {
        return new EmpresaMobileResult(
                empresa.id(),
                empresa.nomeFantasia(),
                empresa.razaoSocial(),
                empresa.email(),
                empresa.telefone(),
                empresa.ativo()
        );
    }
}
