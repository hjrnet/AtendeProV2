package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.EvidenciaEvolucaoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TipoPlaceholderEvolucaoBeautyPro;

public record EvidenciaEvolucaoBeautyProResult(
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
    public static EvidenciaEvolucaoBeautyProResult de(EvidenciaEvolucaoBeautyPro evidencia) {
        return new EvidenciaEvolucaoBeautyProResult(
                evidencia.id(),
                evidencia.empresaId(),
                evidencia.clienteId(),
                evidencia.protocoloId(),
                evidencia.sessaoId(),
                evidencia.tipoPlaceholder(),
                evidencia.tipoPlaceholder().rotulo(),
                evidencia.titulo(),
                evidencia.descricao(),
                evidencia.observacoesPrivacidade(),
                evidencia.avisoPrivacidade(),
                evidencia.criadoEm()
        );
    }
}
