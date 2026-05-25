package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClienteBeautyProntuarioResult(
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
