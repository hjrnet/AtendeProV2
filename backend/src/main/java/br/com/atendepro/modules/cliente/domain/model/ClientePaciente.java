package br.com.atendepro.modules.cliente.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClientePaciente(
        UUID id,
        UUID empresaId,
        String nome,
        TipoCliente tipo,
        AreaCliente area,
        String documento,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String observacoes,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public ClientePaciente {
        if (id == null) {
            throw new IllegalArgumentException("id do cliente e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do cliente e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do cliente e obrigatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do cliente e obrigatorio");
        }
        if (area == null) {
            throw new IllegalArgumentException("area do cliente e obrigatoria");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do cliente sao obrigatorias");
        }
        nome = nome.trim();
        documento = textoOpcional(documento);
        email = textoOpcional(email);
        telefone = textoOpcional(telefone);
        observacoes = textoOpcional(observacoes);
    }

    public static ClientePaciente cadastrar(
            UUID empresaId,
            String nome,
            TipoCliente tipo,
            AreaCliente area,
            String documento,
            String email,
            String telefone,
            LocalDate dataNascimento,
            String observacoes,
            Instant agora
    ) {
        return new ClientePaciente(
                UUID.randomUUID(),
                empresaId,
                nome,
                tipo,
                area,
                normalizarDocumento(documento),
                email,
                telefone,
                dataNascimento,
                observacoes,
                true,
                agora,
                agora
        );
    }

    private static String normalizarDocumento(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.replaceAll("\\D", "");
    }

    private static String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
