package br.com.atendepro.modules.servico.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.servico.application.result.ServicoProcedimentoResult;

public record ServicoProcedimentoResponse(
        UUID id,
        UUID empresaId,
        String nome,
        String descricao,
        AreaCliente area,
        int duracaoMinutos,
        BigDecimal precoBase,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ServicoProcedimentoResponse de(ServicoProcedimentoResult result) {
        return new ServicoProcedimentoResponse(
                result.id(),
                result.empresaId(),
                result.nome(),
                result.descricao(),
                result.area(),
                result.duracaoMinutos(),
                result.precoBase(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
