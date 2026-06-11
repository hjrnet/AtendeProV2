package br.com.atendepro.modules.growth.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.RegistrarLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ApresentacaoDemoGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ClientePosVendaGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.IndicadorVerticalGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.LeadGrowthResult;
import br.com.atendepro.modules.growth.domain.model.EtapaLeadGrowth;
import br.com.atendepro.modules.growth.domain.model.PerfilDemoGrowth;

public interface GrowthPort {

    List<LeadGrowthResult> listarLeads(UUID empresaId, AreaCliente vertical, EtapaLeadGrowth etapa, String busca);

    LeadGrowthResult salvarLead(UUID id, RegistrarLeadGrowthCommand command, Instant agora);

    Optional<LeadGrowthResult> atualizarEtapaLead(UUID empresaId, UUID leadId, EtapaLeadGrowth etapa, Instant atualizadoEm);

    Optional<LeadGrowthResult> atualizarVinculosLead(UUID empresaId, UUID leadId, UUID clientePacienteId, UUID compromissoAgendaId, Instant atualizadoEm);

    List<ClientePosVendaGrowthResult> carregarClientesPosVenda(UUID empresaId, AreaCliente vertical);

    List<IndicadorVerticalGrowthResult> carregarIndicadoresVerticais(UUID empresaId);

    List<ApresentacaoDemoGrowthResult> listarApresentacoesDemo(UUID empresaId, PerfilDemoGrowth perfil);
}
