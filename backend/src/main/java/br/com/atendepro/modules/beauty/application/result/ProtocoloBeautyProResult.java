package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.ProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.SessaoProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.StatusPacoteBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TipoProtocoloBeautyPro;

public record ProtocoloBeautyProResult(
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
        List<SessaoProtocoloBeautyProResult> sessoes,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ProtocoloBeautyProResult de(ProtocoloBeautyPro protocolo, List<SessaoProtocoloBeautyPro> sessoes) {
        return new ProtocoloBeautyProResult(
                protocolo.id(),
                protocolo.empresaId(),
                protocolo.clienteId(),
                protocolo.servicoProcedimentoId(),
                protocolo.nome(),
                protocolo.tipo(),
                protocolo.tipo().rotulo(),
                protocolo.objetivo(),
                protocolo.quantidadeSessoesPrevistas(),
                protocolo.sessoesRealizadas(),
                Math.max(protocolo.quantidadeSessoesPrevistas() - protocolo.sessoesRealizadas(), 0),
                protocolo.status(),
                protocolo.status().rotulo(),
                protocolo.observacoes(),
                sessoes.stream().map(SessaoProtocoloBeautyProResult::de).toList(),
                protocolo.criadoEm(),
                protocolo.atualizadoEm()
        );
    }
}
