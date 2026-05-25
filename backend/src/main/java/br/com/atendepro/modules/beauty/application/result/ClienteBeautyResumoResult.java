package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.UUID;

public record ClienteBeautyResumoResult(
        UUID id,
        String nome,
        String telefone,
        String observacoes,
        boolean ativo,
        Instant atualizadoEm
) {
}
