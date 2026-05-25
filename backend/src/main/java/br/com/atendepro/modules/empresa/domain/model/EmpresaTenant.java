package br.com.atendepro.modules.empresa.domain.model;

import java.time.Instant;
import java.util.UUID;

public record EmpresaTenant(
        UUID id,
        String nomeFantasia,
        String razaoSocial,
        DocumentoEmpresa documento,
        String email,
        String telefone,
        boolean ativo,
        Instant criadoEm
) {

    public EmpresaTenant {
        if (id == null) {
            throw new IllegalArgumentException("id da empresa e obrigatorio");
        }
        if (nomeFantasia == null || nomeFantasia.isBlank()) {
            throw new IllegalArgumentException("nome fantasia e obrigatorio");
        }
        if (documento == null) {
            throw new IllegalArgumentException("documento da empresa e obrigatorio");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("data de criacao da empresa e obrigatoria");
        }
        nomeFantasia = nomeFantasia.trim();
        razaoSocial = textoOpcional(razaoSocial);
        email = textoOpcional(email);
        telefone = textoOpcional(telefone);
    }

    private static String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
