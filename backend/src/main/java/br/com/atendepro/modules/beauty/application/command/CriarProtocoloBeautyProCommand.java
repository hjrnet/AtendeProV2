package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.TipoProtocoloBeautyPro;

public record CriarProtocoloBeautyProCommand(
        UUID empresaId,
        UUID clienteId,
        UUID servicoProcedimentoId,
        String nome,
        TipoProtocoloBeautyPro tipo,
        String objetivo,
        int quantidadeSessoesPrevistas,
        String observacoes
) {
}
