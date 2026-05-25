package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ObjetivoEsteticoBeautyPro;

public record FichaEsteticaBeautyProResult(
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

    public static FichaEsteticaBeautyProResult de(FichaEsteticaBeautyPro ficha) {
        return new FichaEsteticaBeautyProResult(
                ficha.id(),
                ficha.empresaId(),
                ficha.clienteId(),
                ficha.objetivo(),
                ficha.objetivo().rotulo(),
                ficha.queixaPrincipal(),
                ficha.historicoEstetico(),
                ficha.alergias(),
                ficha.medicamentos(),
                ficha.gestante(),
                ficha.lactante(),
                ficha.sensibilidadePele(),
                ficha.usaAcidos(),
                ficha.exposicaoSolarIntensa(),
                ficha.procedimentosRecentes(),
                ficha.contraindicacoes(),
                ficha.observacoes(),
                ficha.possuiAlertaContraindicacao(),
                ficha.alertaContraindicacoes(),
                ficha.criadoEm(),
                ficha.atualizadoEm()
        );
    }
}
