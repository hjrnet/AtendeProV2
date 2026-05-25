package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoPrecificacaoResult(
        UUID id,
        UUID empresaId,
        String nome,
        int duracaoMinutos,
        BigDecimal precoBase
) {
}
