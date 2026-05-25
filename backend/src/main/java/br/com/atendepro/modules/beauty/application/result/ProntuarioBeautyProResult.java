package br.com.atendepro.modules.beauty.application.result;

public record ProntuarioBeautyProResult(
        ClienteBeautyProntuarioResult cliente,
        ResumoProntuarioBeautyProResult resumo,
        FichaEsteticaBeautyProResult fichaAtual
) {
}
