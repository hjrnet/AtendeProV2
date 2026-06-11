package br.com.atendepro.modules.growth.application.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.AtualizarEtapaLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.AtualizarVinculosLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.RegistrarLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ApresentacaoDemoGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.IndicadorVerticalGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.LeadGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.PainelGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.SugestaoPosVendaIAResult;
import br.com.atendepro.modules.growth.domain.model.EtapaLeadGrowth;
import br.com.atendepro.modules.growth.domain.model.PerfilDemoGrowth;

public interface GrowthUseCase {

    PainelGrowthResult consultarPainel(UUID empresaId);

    List<LeadGrowthResult> listarLeads(UUID empresaId, AreaCliente vertical, EtapaLeadGrowth etapa, String busca);

    LeadGrowthResult registrarLead(RegistrarLeadGrowthCommand command);

    Optional<LeadGrowthResult> atualizarEtapa(AtualizarEtapaLeadGrowthCommand command);

    Optional<LeadGrowthResult> atualizarVinculos(AtualizarVinculosLeadGrowthCommand command);

    List<SugestaoPosVendaIAResult> listarSugestoesPosVenda(UUID empresaId, AreaCliente vertical);

    List<IndicadorVerticalGrowthResult> listarIndicadoresVerticais(UUID empresaId);

    List<ApresentacaoDemoGrowthResult> listarApresentacoesDemo(UUID empresaId, PerfilDemoGrowth perfil);
}
