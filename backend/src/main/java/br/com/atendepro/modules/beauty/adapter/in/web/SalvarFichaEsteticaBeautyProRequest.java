package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.beauty.application.command.AtualizarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.domain.model.ObjetivoEsteticoBeautyPro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SalvarFichaEsteticaBeautyProRequest(
        @NotNull ObjetivoEsteticoBeautyPro objetivo,
        @NotBlank @Size(max = 500) String queixaPrincipal,
        @Size(max = 1000) String historicoEstetico,
        @Size(max = 700) String alergias,
        @Size(max = 700) String medicamentos,
        boolean gestante,
        boolean lactante,
        boolean sensibilidadePele,
        boolean usaAcidos,
        boolean exposicaoSolarIntensa,
        @Size(max = 1000) String procedimentosRecentes,
        @Size(max = 1000) String contraindicacoes,
        @Size(max = 1200) String observacoes
) {

    public CriarFichaEsteticaBeautyProCommand paraCriacaoCommand(UUID empresaId, UUID clienteId) {
        return new CriarFichaEsteticaBeautyProCommand(
                empresaId,
                clienteId,
                objetivo,
                queixaPrincipal,
                historicoEstetico,
                alergias,
                medicamentos,
                gestante,
                lactante,
                sensibilidadePele,
                usaAcidos,
                exposicaoSolarIntensa,
                procedimentosRecentes,
                contraindicacoes,
                observacoes
        );
    }

    public AtualizarFichaEsteticaBeautyProCommand paraAtualizacaoCommand(UUID empresaId, UUID clienteId, UUID fichaId) {
        return new AtualizarFichaEsteticaBeautyProCommand(
                empresaId,
                clienteId,
                fichaId,
                objetivo,
                queixaPrincipal,
                historicoEstetico,
                alergias,
                medicamentos,
                gestante,
                lactante,
                sensibilidadePele,
                usaAcidos,
                exposicaoSolarIntensa,
                procedimentosRecentes,
                contraindicacoes,
                observacoes
        );
    }
}
