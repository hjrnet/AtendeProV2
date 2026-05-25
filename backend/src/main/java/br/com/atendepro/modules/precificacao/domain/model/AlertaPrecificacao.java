package br.com.atendepro.modules.precificacao.domain.model;

public record AlertaPrecificacao(
        String codigo,
        NivelAlertaPrecificacao nivel,
        String mensagem
) {

    public AlertaPrecificacao {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("codigo do alerta de precificacao e obrigatorio");
        }
        if (nivel == null) {
            throw new IllegalArgumentException("nivel do alerta de precificacao e obrigatorio");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("mensagem do alerta de precificacao e obrigatoria");
        }
        codigo = codigo.trim();
        mensagem = mensagem.trim();
    }
}
