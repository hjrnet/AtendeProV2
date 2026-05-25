package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.FichaEsteticaBeautyProResult;
import br.com.atendepro.modules.beauty.domain.model.ObjetivoEsteticoBeautyPro;

public record FichaEsteticaBeautyProResponse(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        ObjetivoEsteticoBeautyPro objetivo,
        String objetivoRotulo,
        String queixaPrincipal,
        String historicoEstetico,
        String alergias,
        String medicamentos,
        boolean gestante,
        boolean lactante,
        boolean sensibilidadePele,
        boolean usaAcidos,
        boolean exposicaoSolarIntensa,
        String procedimentosRecentes,
        String contraindicacoes,
        String observacoes,
        boolean possuiAlertaContraindicacao,
        String alertaContraindicacoes,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static FichaEsteticaBeautyProResponse de(FichaEsteticaBeautyProResult result) {
        return new FichaEsteticaBeautyProResponse(
                result.id(),
                result.empresaId(),
                result.clienteId(),
                result.objetivo(),
                result.objetivoRotulo(),
                result.queixaPrincipal(),
                result.historicoEstetico(),
                result.alergias(),
                result.medicamentos(),
                result.gestante(),
                result.lactante(),
                result.sensibilidadePele(),
                result.usaAcidos(),
                result.exposicaoSolarIntensa(),
                result.procedimentosRecentes(),
                result.contraindicacoes(),
                result.observacoes(),
                result.possuiAlertaContraindicacao(),
                result.alertaContraindicacoes(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
