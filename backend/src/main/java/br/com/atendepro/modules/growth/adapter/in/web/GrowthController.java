package br.com.atendepro.modules.growth.adapter.in.web;

import java.net.URI;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.AtualizarEtapaLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.AtualizarVinculosLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.RegistrarLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.port.in.GrowthUseCase;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ApresentacaoDemoGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.IndicadorVerticalGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.LeadGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.PainelGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.SugestaoPosVendaIAResult;
import br.com.atendepro.modules.growth.domain.model.EtapaLeadGrowth;
import br.com.atendepro.modules.growth.domain.model.PerfilDemoGrowth;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/growth")
@Profile("!test")
public class GrowthController {

    private final GrowthUseCase growthUseCase;

    public GrowthController(GrowthUseCase growthUseCase) {
        this.growthUseCase = growthUseCase;
    }

    @GetMapping("/painel")
    public ResponseEntity<PainelGrowthResult> consultarPainel(@RequestParam UUID empresaId) {
        return ResponseEntity.ok(growthUseCase.consultarPainel(empresaId));
    }

    @GetMapping("/leads")
    public ResponseEntity<List<LeadGrowthResult>> listarLeads(
            @RequestParam UUID empresaId,
            @RequestParam(required = false) AreaCliente vertical,
            @RequestParam(required = false) EtapaLeadGrowth etapa,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(growthUseCase.listarLeads(empresaId, vertical, etapa, busca));
    }

    @PostMapping("/leads")
    public ResponseEntity<LeadGrowthResult> registrarLead(@Valid @RequestBody RegistrarLeadGrowthRequest request) {
        LeadGrowthResult response = growthUseCase.registrarLead(request.paraCommand());
        return ResponseEntity.created(URI.create("/api/growth/leads/" + response.id())).body(response);
    }

    @PatchMapping("/leads/{leadId}/etapa")
    public ResponseEntity<LeadGrowthResult> atualizarEtapa(
            @PathVariable UUID leadId,
            @Valid @RequestBody AtualizarEtapaLeadGrowthRequest request
    ) {
        return growthUseCase.atualizarEtapa(request.paraCommand(leadId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/leads/{leadId}/vinculos")
    public ResponseEntity<LeadGrowthResult> atualizarVinculos(
            @PathVariable UUID leadId,
            @Valid @RequestBody AtualizarVinculosLeadGrowthRequest request
    ) {
        return growthUseCase.atualizarVinculos(request.paraCommand(leadId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pos-venda/ia-sugestoes")
    public ResponseEntity<List<SugestaoPosVendaIAResult>> listarSugestoesPosVenda(
            @RequestParam UUID empresaId,
            @RequestParam(required = false) AreaCliente vertical
    ) {
        return ResponseEntity.ok(growthUseCase.listarSugestoesPosVenda(empresaId, vertical));
    }

    @GetMapping("/indicadores-verticais")
    public ResponseEntity<List<IndicadorVerticalGrowthResult>> listarIndicadoresVerticais(@RequestParam UUID empresaId) {
        return ResponseEntity.ok(growthUseCase.listarIndicadoresVerticais(empresaId));
    }

    @GetMapping("/apresentacoes-demo")
    public ResponseEntity<List<ApresentacaoDemoGrowthResult>> listarApresentacoesDemo(
            @RequestParam UUID empresaId,
            @RequestParam(required = false) PerfilDemoGrowth perfil
    ) {
        return ResponseEntity.ok(growthUseCase.listarApresentacoesDemo(empresaId, perfil));
    }

    public record RegistrarLeadGrowthRequest(
            @NotNull UUID empresaId,
            @NotBlank String nome,
            @NotBlank String email,
            String telefone,
            @NotNull AreaCliente vertical,
            @NotBlank String origem,
            EtapaLeadGrowth etapa,
            BigDecimal potencialMensal,
            UUID clientePacienteId,
            UUID compromissoAgendaId,
            String observacoes
    ) {
        RegistrarLeadGrowthCommand paraCommand() {
            return new RegistrarLeadGrowthCommand(empresaId, nome, email, telefone, vertical, origem, etapa, potencialMensal, clientePacienteId, compromissoAgendaId, observacoes);
        }
    }

    public record AtualizarEtapaLeadGrowthRequest(
            @NotNull UUID empresaId,
            @NotNull EtapaLeadGrowth etapa
    ) {
        AtualizarEtapaLeadGrowthCommand paraCommand(UUID leadId) {
            return new AtualizarEtapaLeadGrowthCommand(empresaId, leadId, etapa);
        }
    }

    public record AtualizarVinculosLeadGrowthRequest(
            @NotNull UUID empresaId,
            UUID clientePacienteId,
            UUID compromissoAgendaId
    ) {
        AtualizarVinculosLeadGrowthCommand paraCommand(UUID leadId) {
            return new AtualizarVinculosLeadGrowthCommand(empresaId, leadId, clientePacienteId, compromissoAgendaId);
        }
    }
}
