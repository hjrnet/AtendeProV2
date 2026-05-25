package br.com.atendepro.modules.spaces.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public record PacoteSublocacaoSpaces(
        UUID id,
        UUID empresaId,
        UUID recursoId,
        String nome,
        TipoPacoteSublocacaoSpaces tipo,
        String descricao,
        BigDecimal duracaoHoras,
        BigDecimal valorFixo,
        BigDecimal percentualReceita,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    private static final BigDecimal CEM = new BigDecimal("100.00");

    public PacoteSublocacaoSpaces {
        if (id == null) {
            throw new IllegalArgumentException("id do pacote de sublocacao e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do pacote de sublocacao e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do pacote de sublocacao e obrigatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do pacote de sublocacao e obrigatorio");
        }
        validarNaoNegativo(valorFixo, "valor fixo");
        validarNaoNegativo(percentualReceita, "percentual de receita");
        if (duracaoHoras == null || duracaoHoras.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("duracao do pacote de sublocacao deve ser positiva");
        }
        if (percentualReceita.compareTo(CEM) > 0) {
            throw new IllegalArgumentException("percentual de receita nao pode passar de 100");
        }
        if (tipo != TipoPacoteSublocacaoSpaces.FIXO_PERCENTUAL && valorFixo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor fixo do pacote de sublocacao deve ser positivo");
        }
        if (tipo == TipoPacoteSublocacaoSpaces.FIXO_PERCENTUAL && percentualReceita.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("percentual de receita do pacote fixo percentual deve ser positivo");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do pacote de sublocacao sao obrigatorias");
        }
    }

    public static PacoteSublocacaoSpaces cadastrar(
            UUID empresaId,
            UUID recursoId,
            String nome,
            TipoPacoteSublocacaoSpaces tipo,
            String descricao,
            BigDecimal duracaoHoras,
            BigDecimal valorFixo,
            BigDecimal percentualReceita,
            Instant agora
    ) {
        return new PacoteSublocacaoSpaces(
                UUID.randomUUID(),
                empresaId,
                recursoId,
                nome.trim(),
                tipo,
                textoOpcional(descricao),
                escala(duracaoHoras),
                dinheiro(valorFixo),
                escala(percentualReceita),
                true,
                agora,
                agora
        );
    }

    private static BigDecimal dinheiro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN) : valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal escala(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN) : valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private static void validarNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(campo + " do pacote de sublocacao nao pode ser negativo");
        }
    }
}
