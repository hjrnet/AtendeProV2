package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasObservacaoResult;

public record EmpresaAdminSaasObservacaoResponse(
        UUID id,
        String nomeFantasia,
        boolean ativo,
        String statusOperacional,
        long usuariosVinculados,
        Instant observadoEm
) {

    static EmpresaAdminSaasObservacaoResponse de(EmpresaAdminSaasObservacaoResult result) {
        return new EmpresaAdminSaasObservacaoResponse(
                result.id(),
                result.nomeFantasia(),
                result.ativo(),
                result.statusOperacional(),
                result.usuariosVinculados(),
                result.observadoEm()
        );
    }
}
