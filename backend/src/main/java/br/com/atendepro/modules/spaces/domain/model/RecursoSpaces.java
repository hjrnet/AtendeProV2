package br.com.atendepro.modules.spaces.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RecursoSpaces(
        UUID id,
        UUID empresaId,
        String nome,
        TipoRecursoSpaces tipo,
        String descricao,
        int capacidadePessoas,
        String localizacao,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public RecursoSpaces {
        if (id == null) {
            throw new IllegalArgumentException("id do recurso spaces e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do recurso spaces e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do recurso spaces e obrigatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do recurso spaces e obrigatorio");
        }
        if (capacidadePessoas <= 0) {
            throw new IllegalArgumentException("capacidade do recurso spaces deve ser positiva");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do recurso spaces sao obrigatorias");
        }
    }

    public static RecursoSpaces cadastrar(
            UUID empresaId,
            String nome,
            TipoRecursoSpaces tipo,
            String descricao,
            int capacidadePessoas,
            String localizacao,
            Instant agora
    ) {
        return new RecursoSpaces(
                UUID.randomUUID(),
                empresaId,
                nome.trim(),
                tipo,
                textoOpcional(descricao),
                capacidadePessoas,
                textoOpcional(localizacao),
                true,
                agora,
                agora
        );
    }

    private static String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
