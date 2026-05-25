package br.com.atendepro.modules.adminsaas.application.result;

import java.time.Instant;
import java.util.UUID;

public record EmpresaAdminSaasObservacaoResult(
        UUID id,
        String nomeFantasia,
        boolean ativo,
        String statusOperacional,
        long usuariosVinculados,
        Instant observadoEm
) {
}
