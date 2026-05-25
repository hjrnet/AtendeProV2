package br.com.atendepro.modules.custo.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public record CustoAlimentacaoTransporte(
        UUID id,
        UUID empresaId,
        UUID profissionalId,
        String descricao,
        TipoCustoPessoal tipo,
        PeriodicidadeCustoPessoal periodicidade,
        BigDecimal valor,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public CustoAlimentacaoTransporte {
        if (id == null) {
            throw new IllegalArgumentException("id do custo pessoal e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do custo pessoal e obrigatoria");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao do custo pessoal e obrigatoria");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do custo pessoal e obrigatorio");
        }
        if (periodicidade == null) {
            throw new IllegalArgumentException("periodicidade do custo pessoal e obrigatoria");
        }
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException("valor do custo pessoal nao pode ser negativo");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do custo pessoal sao obrigatorias");
        }
        descricao = descricao.trim();
        valor = valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static CustoAlimentacaoTransporte cadastrar(
            UUID empresaId,
            UUID profissionalId,
            String descricao,
            TipoCustoPessoal tipo,
            PeriodicidadeCustoPessoal periodicidade,
            BigDecimal valor,
            Instant agora
    ) {
        return new CustoAlimentacaoTransporte(
                UUID.randomUUID(),
                empresaId,
                profissionalId,
                descricao,
                tipo,
                periodicidade,
                valor,
                true,
                agora,
                agora
        );
    }
}
