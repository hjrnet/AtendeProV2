package br.com.atendepro.modules.mobile.application.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClienteVinculadoMobileResult(
        UUID id,
        UUID empresaId,
        String nome,
        String tipo,
        String area,
        String documento,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String observacoes,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {
}
