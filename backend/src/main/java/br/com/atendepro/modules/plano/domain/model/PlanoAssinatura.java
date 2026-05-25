package br.com.atendepro.modules.plano.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PlanoAssinatura(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        BigDecimal valorMensal,
        int limiteUsuarios,
        int limiteClientes,
        int limiteProfissionais,
        boolean ativo,
        boolean estudante,
        String marcaDaguaAcademica,
        Set<ModuloPlano> modulos,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public PlanoAssinatura {
        if (id == null) {
            throw new IllegalArgumentException("id do plano e obrigatorio");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("codigo do plano e obrigatorio");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do plano e obrigatorio");
        }
        if (valorMensal == null || valorMensal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("valor mensal do plano deve ser maior ou igual a zero");
        }
        if (limiteUsuarios < 0 || limiteClientes < 0 || limiteProfissionais < 0) {
            throw new IllegalArgumentException("limites do plano devem ser maiores ou iguais a zero");
        }
        if (modulos == null || modulos.isEmpty()) {
            throw new IllegalArgumentException("plano deve possuir ao menos um modulo");
        }
        if (estudante) {
            validarPlanoEstudante(limiteUsuarios, limiteClientes, limiteProfissionais, marcaDaguaAcademica);
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("data de criacao do plano e obrigatoria");
        }
        if (atualizadoEm == null) {
            throw new IllegalArgumentException("data de atualizacao do plano e obrigatoria");
        }
        codigo = normalizarCodigo(codigo);
        nome = nome.trim();
        descricao = textoOpcional(descricao);
        marcaDaguaAcademica = textoOpcional(marcaDaguaAcademica);
        valorMensal = valorMensal.setScale(2, java.math.RoundingMode.HALF_UP);
        modulos = Set.copyOf(modulos);
    }

    public static String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase().replace("-", "_").replace(" ", "_");
    }

    private static String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private static void validarPlanoEstudante(
            int limiteUsuarios,
            int limiteClientes,
            int limiteProfissionais,
            String marcaDaguaAcademica
    ) {
        if (limiteUsuarios > 1 || limiteClientes > 30 || limiteProfissionais > 1) {
            throw new IllegalArgumentException("plano estudante excede limites academicos");
        }
        if (marcaDaguaAcademica == null || marcaDaguaAcademica.isBlank()) {
            throw new IllegalArgumentException("plano estudante exige marca d'agua academica");
        }
    }
}
