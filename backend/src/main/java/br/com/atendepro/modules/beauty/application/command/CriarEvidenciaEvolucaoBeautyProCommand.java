package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.TipoPlaceholderEvolucaoBeautyPro;

public record CriarEvidenciaEvolucaoBeautyProCommand(
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        UUID sessaoId,
        TipoPlaceholderEvolucaoBeautyPro tipoPlaceholder,
        String titulo,
        String descricao,
        String observacoesPrivacidade
) {
}
