package br.com.atendepro.modules.growth.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.growth.domain.model.EtapaLeadGrowth;

public final class GrowthCommands {

    private GrowthCommands() {
    }

    public record RegistrarLeadGrowthCommand(
            UUID empresaId,
            String nome,
            String email,
            String telefone,
            AreaCliente vertical,
            String origem,
            EtapaLeadGrowth etapa,
            BigDecimal potencialMensal,
            UUID clientePacienteId,
            UUID compromissoAgendaId,
            String observacoes
    ) {
    }

    public record AtualizarEtapaLeadGrowthCommand(
            UUID empresaId,
            UUID leadId,
            EtapaLeadGrowth etapa
    ) {
    }

    public record AtualizarVinculosLeadGrowthCommand(
            UUID empresaId,
            UUID leadId,
            UUID clientePacienteId,
            UUID compromissoAgendaId
    ) {
    }
}
