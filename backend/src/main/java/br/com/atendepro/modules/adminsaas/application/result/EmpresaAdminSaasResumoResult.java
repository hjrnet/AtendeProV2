package br.com.atendepro.modules.adminsaas.application.result;

import java.time.Instant;
import java.util.UUID;

public record EmpresaAdminSaasResumoResult(
        UUID id,
        String nomeFantasia,
        String documento,
        String email,
        boolean ativo,
        Instant criadoEm
) {
}
