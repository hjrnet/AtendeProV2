package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.beauty.application.command.CriarEvidenciaEvolucaoBeautyProCommand;
import br.com.atendepro.modules.beauty.domain.model.TipoPlaceholderEvolucaoBeautyPro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarEvidenciaEvolucaoBeautyProRequest(
        UUID protocoloId,
        UUID sessaoId,
        @NotNull TipoPlaceholderEvolucaoBeautyPro tipoPlaceholder,
        @NotBlank String titulo,
        @NotBlank String descricao,
        String observacoesPrivacidade
) {
    public CriarEvidenciaEvolucaoBeautyProCommand paraCommand(UUID empresaId, UUID clienteId) {
        return new CriarEvidenciaEvolucaoBeautyProCommand(
                empresaId,
                clienteId,
                protocoloId,
                sessaoId,
                tipoPlaceholder,
                titulo,
                descricao,
                observacoesPrivacidade
        );
    }
}
