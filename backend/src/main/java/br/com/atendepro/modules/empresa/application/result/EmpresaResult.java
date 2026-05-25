package br.com.atendepro.modules.empresa.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;

public record EmpresaResult(
        UUID id,
        String nomeFantasia,
        String razaoSocial,
        String documento,
        String email,
        String telefone,
        boolean ativo,
        Instant criadoEm
) {

    public static EmpresaResult de(EmpresaTenant empresa) {
        return new EmpresaResult(
                empresa.id(),
                empresa.nomeFantasia(),
                empresa.razaoSocial(),
                empresa.documento().valor(),
                empresa.email(),
                empresa.telefone(),
                empresa.ativo(),
                empresa.criadoEm()
        );
    }
}
