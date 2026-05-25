package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.beauty.application.command.CriarProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.domain.model.TipoProtocoloBeautyPro;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarProtocoloBeautyProRequest(
        UUID servicoProcedimentoId,
        @NotBlank @Size(max = 160) String nome,
        @NotNull TipoProtocoloBeautyPro tipo,
        @NotBlank @Size(max = 1000) String objetivo,
        @Min(1) @Max(60) int quantidadeSessoesPrevistas,
        @Size(max = 1200) String observacoes
) {
    public CriarProtocoloBeautyProCommand paraCommand(UUID empresaId, UUID clienteId) {
        return new CriarProtocoloBeautyProCommand(
                empresaId,
                clienteId,
                servicoProcedimentoId,
                nome,
                tipo,
                objetivo,
                quantidadeSessoesPrevistas,
                observacoes
        );
    }
}
