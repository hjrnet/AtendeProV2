package br.com.atendepro.modules.spaces.domain.model;

import java.time.Instant;
import java.util.UUID;

public record OcupacaoSpaces(
        UUID id,
        UUID empresaId,
        UUID recursoId,
        UUID pacoteId,
        String nomeParceiro,
        Instant inicioEm,
        Instant fimEm,
        StatusOcupacaoSpaces status,
        String observacao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public OcupacaoSpaces {
        if (id == null) {
            throw new IllegalArgumentException("id da ocupacao spaces e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da ocupacao spaces e obrigatoria");
        }
        if (recursoId == null) {
            throw new IllegalArgumentException("recurso da ocupacao spaces e obrigatorio");
        }
        if (nomeParceiro == null || nomeParceiro.isBlank()) {
            throw new IllegalArgumentException("nome do parceiro da ocupacao spaces e obrigatorio");
        }
        if (inicioEm == null || fimEm == null) {
            throw new IllegalArgumentException("periodo da ocupacao spaces e obrigatorio");
        }
        if (!fimEm.isAfter(inicioEm)) {
            throw new IllegalArgumentException("fim da ocupacao spaces deve ser posterior ao inicio");
        }
        if (status == null) {
            throw new IllegalArgumentException("status da ocupacao spaces e obrigatorio");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas da ocupacao spaces sao obrigatorias");
        }
        nomeParceiro = nomeParceiro.trim();
        observacao = textoOpcional(observacao);
    }

    public static OcupacaoSpaces agendar(
            UUID empresaId,
            UUID recursoId,
            UUID pacoteId,
            String nomeParceiro,
            Instant inicioEm,
            Instant fimEm,
            StatusOcupacaoSpaces status,
            String observacao,
            Instant agora
    ) {
        StatusOcupacaoSpaces statusInicial = status == null ? StatusOcupacaoSpaces.RESERVADA : status;
        if (statusInicial == StatusOcupacaoSpaces.CANCELADA) {
            throw new IllegalArgumentException("ocupacao spaces nao pode nascer cancelada");
        }
        return new OcupacaoSpaces(
                UUID.randomUUID(),
                empresaId,
                recursoId,
                pacoteId,
                nomeParceiro,
                inicioEm,
                fimEm,
                statusInicial,
                observacao,
                agora,
                agora
        );
    }

    private static String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
