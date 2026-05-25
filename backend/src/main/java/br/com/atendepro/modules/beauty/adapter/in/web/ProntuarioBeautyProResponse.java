package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ClienteBeautyProntuarioResult;
import br.com.atendepro.modules.beauty.application.result.ProntuarioBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ResumoProntuarioBeautyProResult;

public record ProntuarioBeautyProResponse(
        ClienteBeautyProntuarioResponse cliente,
        ResumoProntuarioBeautyProResponse resumo,
        FichaEsteticaBeautyProResponse fichaAtual
) {
    public static ProntuarioBeautyProResponse de(ProntuarioBeautyProResult result) {
        return new ProntuarioBeautyProResponse(
                ClienteBeautyProntuarioResponse.de(result.cliente()),
                ResumoProntuarioBeautyProResponse.de(result.resumo()),
                result.fichaAtual() == null ? null : FichaEsteticaBeautyProResponse.de(result.fichaAtual())
        );
    }

    public record ClienteBeautyProntuarioResponse(
            UUID id,
            UUID empresaId,
            String nome,
            String email,
            String telefone,
            LocalDate dataNascimento,
            Integer idade,
            String observacoes,
            boolean ativo,
            Instant atualizadoEm
    ) {
        public static ClienteBeautyProntuarioResponse de(ClienteBeautyProntuarioResult result) {
            return new ClienteBeautyProntuarioResponse(
                    result.id(),
                    result.empresaId(),
                    result.nome(),
                    result.email(),
                    result.telefone(),
                    result.dataNascimento(),
                    result.idade(),
                    result.observacoes(),
                    result.ativo(),
                    result.atualizadoEm()
            );
        }
    }

    public record ResumoProntuarioBeautyProResponse(
            long fichasEsteticas,
            long consultasFuturas,
            long documentos,
            String statusFichaEstetica,
            String statusContraindicacoes,
            Instant ultimaConsultaEm
    ) {
        public static ResumoProntuarioBeautyProResponse de(ResumoProntuarioBeautyProResult result) {
            return new ResumoProntuarioBeautyProResponse(
                    result.fichasEsteticas(),
                    result.consultasFuturas(),
                    result.documentos(),
                    result.statusFichaEstetica(),
                    result.statusContraindicacoes(),
                    result.ultimaConsultaEm()
            );
        }
    }
}
