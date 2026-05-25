package br.com.atendepro.modules.servico.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.servico.domain.model.ServicoProcedimento;

public record ServicoProcedimentoResult(
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

    public static ServicoProcedimentoResult de(ServicoProcedimento servico) {
        return new ServicoProcedimentoResult(
                servico.id(),
                servico.empresaId(),
                servico.nome(),
                servico.descricao(),
                servico.area(),
                servico.duracaoMinutos(),
                servico.precoBase(),
                servico.ativo(),
                servico.criadoEm(),
                servico.atualizadoEm()
        );
    }
}
