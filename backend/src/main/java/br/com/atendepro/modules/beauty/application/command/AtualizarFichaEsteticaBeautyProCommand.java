package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.ObjetivoEsteticoBeautyPro;

public record AtualizarFichaEsteticaBeautyProCommand(
        UUID empresaId,
        UUID clienteId,
        UUID fichaId,
        ObjetivoEsteticoBeautyPro objetivo,
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
        String observacoes
) {
}
