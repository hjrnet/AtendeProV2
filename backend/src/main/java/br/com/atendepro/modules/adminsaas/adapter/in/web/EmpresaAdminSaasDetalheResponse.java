package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;

public record EmpresaAdminSaasDetalheResponse(
        UUID id,
        String nomeFantasia,
        String razaoSocial,
        String documento,
        String email,
        String telefone,
        boolean ativo,
        Instant criadoEm
) {

    static EmpresaAdminSaasDetalheResponse de(EmpresaAdminSaasDetalheResult result) {
        return new EmpresaAdminSaasDetalheResponse(
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
