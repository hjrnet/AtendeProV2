package br.com.atendepro.modules.nutri.application.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PacienteProntuarioNutriProResult(
        UUID id,
        UUID empresaId,
        String nome,
        String email,
        String telefone,
        LocalDate dataNascimento,
        Integer idade,
        String observacoes,
        boolean ativo,
        Instant atualizadoEm
) {
}
