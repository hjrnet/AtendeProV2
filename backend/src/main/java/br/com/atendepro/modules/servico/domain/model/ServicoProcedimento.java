package br.com.atendepro.modules.servico.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;

public record ServicoProcedimento(
        UUID id,
        UUID empresaId,
        String nome,
        String descricao,
        AreaCliente area,
        int duracaoMinutos,
        BigDecimal precoBase,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public ServicoProcedimento {
        if (id == null) {
            throw new IllegalArgumentException("id do servico e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do servico e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do servico e obrigatorio");
        }
        if (area == null) {
            throw new IllegalArgumentException("area do servico e obrigatoria");
        }
        if (duracaoMinutos < 1) {
            throw new IllegalArgumentException("duracao do servico deve ser positiva");
        }
        if (precoBase == null || precoBase.signum() < 0) {
            throw new IllegalArgumentException("preco base do servico nao pode ser negativo");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do servico sao obrigatorias");
        }
        nome = nome.trim();
        descricao = textoOpcional(descricao);
        precoBase = precoBase.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static ServicoProcedimento cadastrar(
            UUID empresaId,
            String nome,
            String descricao,
            AreaCliente area,
            int duracaoMinutos,
            BigDecimal precoBase,
            Instant agora
    ) {
        return new ServicoProcedimento(
                UUID.randomUUID(),
                empresaId,
                nome,
                descricao,
                area,
                duracaoMinutos,
                precoBase,
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
