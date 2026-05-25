package br.com.atendepro.modules.nutri.application.result;

import java.time.Instant;
import java.util.UUID;

public record PacienteNutriResumoResult(
        UUID id,
        String nome,
        String telefone,
        String observacoes,
        boolean ativo,
        Instant atualizadoEm
) {
}
