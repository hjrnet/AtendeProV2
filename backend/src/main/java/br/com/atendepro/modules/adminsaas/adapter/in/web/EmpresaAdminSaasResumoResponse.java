package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasResumoResult;

public record EmpresaAdminSaasResumoResponse(
        UUID id,
        String nomeFantasia,
        String documento,
        String email,
        boolean ativo,
        Instant criadoEm
) {

    static EmpresaAdminSaasResumoResponse de(EmpresaAdminSaasResumoResult result) {
        return new EmpresaAdminSaasResumoResponse(
                result.id(),
                result.nomeFantasia(),
                result.documento(),
                result.email(),
                result.ativo(),
                result.criadoEm()
        );
    }
}
