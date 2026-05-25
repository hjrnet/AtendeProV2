package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.EvidenciaEvolucaoBeautyProResult;
import br.com.atendepro.modules.beauty.domain.model.TipoPlaceholderEvolucaoBeautyPro;

public record EvidenciaEvolucaoBeautyProResponse(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        UUID sessaoId,
        TipoPlaceholderEvolucaoBeautyPro tipoPlaceholder,
        String tipoPlaceholderRotulo,
        String titulo,
        String descricao,
        String observacoesPrivacidade,
        String avisoPrivacidade,
        Instant criadoEm
) {
    public static EvidenciaEvolucaoBeautyProResponse de(EvidenciaEvolucaoBeautyProResult result) {
        return new EvidenciaEvolucaoBeautyProResponse(
                result.id(),
                result.empresaId(),
                result.clienteId(),
                result.protocoloId(),
                result.sessaoId(),
                result.tipoPlaceholder(),
                result.tipoPlaceholderRotulo(),
                result.titulo(),
                result.descricao(),
                result.observacoesPrivacidade(),
                result.avisoPrivacidade(),
                result.criadoEm()
        );
    }
}
