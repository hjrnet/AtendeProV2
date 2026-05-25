package br.com.atendepro.modules.custo.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

public record CustoGeral(
        UUID id,
        UUID empresaId,
        String descricao,
        TipoCustoGeral tipo,
        String categoria,
        BigDecimal valor,
        YearMonth competencia,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public CustoGeral {
        if (id == null) {
            throw new IllegalArgumentException("id do custo e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do custo e obrigatoria");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao do custo e obrigatoria");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do custo e obrigatorio");
        }
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException("valor do custo nao pode ser negativo");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do custo sao obrigatorias");
        }
        descricao = descricao.trim();
        categoria = textoOpcional(categoria);
        valor = valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static CustoGeral cadastrar(
            UUID empresaId,
            String descricao,
            TipoCustoGeral tipo,
            String categoria,
            BigDecimal valor,
            YearMonth competencia,
            Instant agora
    ) {
        return new CustoGeral(
                UUID.randomUUID(),
                empresaId,
                descricao,
                tipo,
                categoria,
                valor,
                competencia,
                true,
                agora,
                agora
        );
    }

    private static String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
