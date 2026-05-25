package br.com.atendepro.modules.adminsaas.application.result;

import java.time.Instant;
import java.util.UUID;

public record EmpresaAdminSaasDetalheResult(
        UUID id,
        String nomeFantasia,
        String razaoSocial,
        String documento,
        String email,
        String telefone,
        boolean ativo,
        Instant criadoEm
) {
}
