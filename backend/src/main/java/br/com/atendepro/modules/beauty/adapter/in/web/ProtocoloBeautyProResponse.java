package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ProtocoloBeautyProResult;
import br.com.atendepro.modules.beauty.domain.model.StatusPacoteBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TipoProtocoloBeautyPro;

public record ProtocoloBeautyProResponse(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        UUID servicoProcedimentoId,
        String nome,
        TipoProtocoloBeautyPro tipo,
        String tipoRotulo,
        String objetivo,
        int quantidadeSessoesPrevistas,
        int sessoesRealizadas,
        int sessoesRestantes,
        StatusPacoteBeautyPro status,
        String statusRotulo,
        String observacoes,
        List<SessaoProtocoloBeautyProResponse> sessoes,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ProtocoloBeautyProResponse de(ProtocoloBeautyProResult result) {
        return new ProtocoloBeautyProResponse(
                result.id(),
                result.empresaId(),
                result.clienteId(),
                result.servicoProcedimentoId(),
                result.nome(),
                result.tipo(),
                result.tipoRotulo(),
                result.objetivo(),
                result.quantidadeSessoesPrevistas(),
                result.sessoesRealizadas(),
                result.sessoesRestantes(),
                result.status(),
                result.statusRotulo(),
                result.observacoes(),
                result.sessoes().stream().map(SessaoProtocoloBeautyProResponse::de).toList(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
