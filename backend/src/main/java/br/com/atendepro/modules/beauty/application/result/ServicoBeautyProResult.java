package br.com.atendepro.modules.beauty.application.result;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoBeautyProResult(
        UUID id,
        String nome,
        String descricao,
        String area,
        int duracaoMinutos,
        BigDecimal precoBase,
        boolean ativo
) {
}
