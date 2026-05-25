package br.com.atendepro.modules.empresa.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.empresa.application.result.EmpresaResult;

public record EmpresaResponse(
        UUID id,
        String nomeFantasia,
        String razaoSocial,
        String documento,
        String email,
        String telefone,
        boolean ativo,
        Instant criadoEm
) {

    static EmpresaResponse de(EmpresaResult result) {
        return new EmpresaResponse(
                result.id(),
                result.nomeFantasia(),
                result.razaoSocial(),
                result.documento(),
                result.email(),
                result.telefone(),
                result.ativo(),
                result.criadoEm()
        );
    }
}
