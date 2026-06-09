package br.com.atendepro.modules.nutri.adapter.in.web;

import java.net.URI;
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

import br.com.atendepro.modules.nutri.application.command.DetalharAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.DetalharPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ConsultarPacienteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.MarcarMensagensLidasCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.PublicarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ListarAvaliacoesAntropometricasNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPlanosAlimentaresNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.CriarAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.CriarPlanoAlimentarNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.DetalharAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.DetalharPlanoAlimentarNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.GerenciarExperienciaPacienteNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarProntuarioNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarAvaliacoesAntropometricasNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPlanosAlimentaresNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPacientesNutriProUseCase;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.CriarLembreteNutriProRequest;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.CriarMetaNutriProRequest;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.CriarRegistroDiarioNutriProRequest;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.EnviarMensagemNutriProRequest;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.EvolucoesNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.LembreteNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.LembretesNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.ListaComprasNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.MensagemNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.MensagensNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.MetaNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.MetasNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.RegistroDiarioNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.RegistrosDiarioNutriProResponse;
import br.com.atendepro.modules.nutri.adapter.in.web.ExperienciaPacienteNutriProWeb.RevisarRegistroDiarioNutriProRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/nutri-pro")
@Profile("!test")
public class NutriProController {

    private final ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase;
    private final ListarPacientesNutriProUseCase listarPacientesNutriProUseCase;
    private final ConsultarProntuarioNutriProUseCase consultarProntuarioNutriProUseCase;
    private final CriarAvaliacaoAntropometricaNutriProUseCase criarAvaliacaoAntropometricaNutriProUseCase;
    private final ListarAvaliacoesAntropometricasNutriProUseCase listarAvaliacoesAntropometricasNutriProUseCase;
    private final DetalharAvaliacaoAntropometricaNutriProUseCase detalharAvaliacaoAntropometricaNutriProUseCase;
    private final CriarPlanoAlimentarNutriProUseCase criarPlanoAlimentarNutriProUseCase;
    private final ListarPlanosAlimentaresNutriProUseCase listarPlanosAlimentaresNutriProUseCase;
    private final DetalharPlanoAlimentarNutriProUseCase detalharPlanoAlimentarNutriProUseCase;
    private final GerenciarExperienciaPacienteNutriProUseCase gerenciarExperienciaPacienteNutriProUseCase;

    public NutriProController(
            ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase,
            ListarPacientesNutriProUseCase listarPacientesNutriProUseCase,
            ConsultarProntuarioNutriProUseCase consultarProntuarioNutriProUseCase,
            CriarAvaliacaoAntropometricaNutriProUseCase criarAvaliacaoAntropometricaNutriProUseCase,
            ListarAvaliacoesAntropometricasNutriProUseCase listarAvaliacoesAntropometricasNutriProUseCase,
            DetalharAvaliacaoAntropometricaNutriProUseCase detalharAvaliacaoAntropometricaNutriProUseCase,
            CriarPlanoAlimentarNutriProUseCase criarPlanoAlimentarNutriProUseCase,
            ListarPlanosAlimentaresNutriProUseCase listarPlanosAlimentaresNutriProUseCase,
            DetalharPlanoAlimentarNutriProUseCase detalharPlanoAlimentarNutriProUseCase,
            GerenciarExperienciaPacienteNutriProUseCase gerenciarExperienciaPacienteNutriProUseCase
    ) {
        this.consultarVisaoNutriProUseCase = consultarVisaoNutriProUseCase;
        this.listarPacientesNutriProUseCase = listarPacientesNutriProUseCase;
        this.consultarProntuarioNutriProUseCase = consultarProntuarioNutriProUseCase;
        this.criarAvaliacaoAntropometricaNutriProUseCase = criarAvaliacaoAntropometricaNutriProUseCase;
        this.listarAvaliacoesAntropometricasNutriProUseCase = listarAvaliacoesAntropometricasNutriProUseCase;
        this.detalharAvaliacaoAntropometricaNutriProUseCase = detalharAvaliacaoAntropometricaNutriProUseCase;
        this.criarPlanoAlimentarNutriProUseCase = criarPlanoAlimentarNutriProUseCase;
        this.listarPlanosAlimentaresNutriProUseCase = listarPlanosAlimentaresNutriProUseCase;
        this.detalharPlanoAlimentarNutriProUseCase = detalharPlanoAlimentarNutriProUseCase;
        this.gerenciarExperienciaPacienteNutriProUseCase = gerenciarExperienciaPacienteNutriProUseCase;
    }

    @GetMapping("/visao")
    public ResponseEntity<VisaoNutriProResponse> consultarVisaoNutriPro(
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(VisaoNutriProResponse.de(
                consultarVisaoNutriProUseCase.consultarVisaoNutriPro(new ConsultarVisaoNutriProCommand(empresaId))
        ));
    }

    @GetMapping("/pacientes")
    public ResponseEntity<PacientesNutriProResponse> listarPacientesNutriPro(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(PacientesNutriProResponse.de(
                listarPacientesNutriProUseCase.listarPacientesNutriPro(new ListarPacientesNutriProCommand(empresaId, busca))
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/prontuario")
    public ResponseEntity<ProntuarioNutriProResponse> consultarProntuarioNutriPro(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return consultarProntuarioNutriProUseCase
                .consultarProntuarioNutriPro(new ConsultarProntuarioNutriProCommand(empresaId, pacienteId))
                .map(ProntuarioNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/pacientes/{pacienteId}/avaliacoes-antropometricas")
    public ResponseEntity<AvaliacaoAntropometricaNutriProResponse> criarAvaliacaoAntropometrica(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody CriarAvaliacaoAntropometricaNutriProRequest request
    ) {
        AvaliacaoAntropometricaNutriProResponse response = AvaliacaoAntropometricaNutriProResponse.de(
                criarAvaliacaoAntropometricaNutriProUseCase.criarAvaliacaoAntropometrica(request.paraCommand(empresaId, pacienteId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/pacientes/" + pacienteId + "/avaliacoes-antropometricas/" + response.id()))
                .body(response);
    }

    @GetMapping("/pacientes/{pacienteId}/avaliacoes-antropometricas")
    public ResponseEntity<AvaliacoesAntropometricasNutriProResponse> listarAvaliacoesAntropometricas(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(AvaliacoesAntropometricasNutriProResponse.de(
                listarAvaliacoesAntropometricasNutriProUseCase.listarAvaliacoesAntropometricas(
                        new ListarAvaliacoesAntropometricasNutriProCommand(empresaId, pacienteId)
                )
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/avaliacoes-antropometricas/{avaliacaoId}")
    public ResponseEntity<AvaliacaoAntropometricaNutriProResponse> detalharAvaliacaoAntropometrica(
            @PathVariable UUID pacienteId,
            @PathVariable UUID avaliacaoId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return detalharAvaliacaoAntropometricaNutriProUseCase
                .detalharAvaliacaoAntropometrica(new DetalharAvaliacaoAntropometricaNutriProCommand(empresaId, pacienteId, avaliacaoId))
                .map(AvaliacaoAntropometricaNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/pacientes/{pacienteId}/planos-alimentares")
    public ResponseEntity<PlanoAlimentarNutriProResponse> criarPlanoAlimentar(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody CriarPlanoAlimentarNutriProRequest request
    ) {
        PlanoAlimentarNutriProResponse response = PlanoAlimentarNutriProResponse.de(
                criarPlanoAlimentarNutriProUseCase.criarPlanoAlimentar(request.paraCommand(empresaId, pacienteId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/pacientes/" + pacienteId + "/planos-alimentares/" + response.id()))
                .body(response);
    }

    @GetMapping("/pacientes/{pacienteId}/planos-alimentares")
    public ResponseEntity<PlanosAlimentaresNutriProResponse> listarPlanosAlimentares(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(PlanosAlimentaresNutriProResponse.de(
                listarPlanosAlimentaresNutriProUseCase.listarPlanosAlimentares(
                        new ListarPlanosAlimentaresNutriProCommand(empresaId, pacienteId)
                )
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/planos-alimentares/{planoId}")
    public ResponseEntity<PlanoAlimentarNutriProResponse> detalharPlanoAlimentar(
            @PathVariable UUID pacienteId,
            @PathVariable UUID planoId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return detalharPlanoAlimentarNutriProUseCase
                .detalharPlanoAlimentar(new DetalharPlanoAlimentarNutriProCommand(empresaId, pacienteId, planoId))
                .map(PlanoAlimentarNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/pacientes/{pacienteId}/planos-alimentares/{planoId}/publicar")
    public ResponseEntity<PlanoAlimentarNutriProResponse> publicarPlanoAlimentar(
            @PathVariable UUID pacienteId,
            @PathVariable UUID planoId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return gerenciarExperienciaPacienteNutriProUseCase
                .publicarPlanoAlimentar(new PublicarPlanoAlimentarCommand(empresaId, pacienteId, planoId))
                .map(PlanoAlimentarNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pacientes/{pacienteId}/plano-publicado")
    public ResponseEntity<PlanoAlimentarNutriProResponse> consultarPlanoPublicado(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return gerenciarExperienciaPacienteNutriProUseCase
                .consultarPlanoPublicado(new ConsultarPacienteCommand(empresaId, pacienteId))
                .map(PlanoAlimentarNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pacientes/{pacienteId}/lista-compras")
    public ResponseEntity<ListaComprasNutriProResponse> consultarListaCompras(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return gerenciarExperienciaPacienteNutriProUseCase
                .consultarListaCompras(new ConsultarPacienteCommand(empresaId, pacienteId))
                .map(ListaComprasNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pacientes/{pacienteId}/diario-alimentar")
    public ResponseEntity<RegistrosDiarioNutriProResponse> listarDiarioAlimentar(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(RegistrosDiarioNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.listarDiarioAlimentar(new ConsultarPacienteCommand(empresaId, pacienteId))
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/diario-alimentar")
    public ResponseEntity<RegistroDiarioNutriProResponse> criarRegistroDiario(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @RequestBody CriarRegistroDiarioNutriProRequest request
    ) {
        RegistroDiarioNutriProResponse response = RegistroDiarioNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.criarRegistroDiario(request.paraCommand(empresaId, pacienteId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/pacientes/" + pacienteId + "/diario-alimentar/" + response.id()))
                .body(response);
    }

    @PostMapping("/pacientes/{pacienteId}/diario-alimentar/{registroId}/revisar")
    public ResponseEntity<RegistroDiarioNutriProResponse> revisarRegistroDiario(
            @PathVariable UUID pacienteId,
            @PathVariable UUID registroId,
            @RequestParam(required = false) UUID empresaId,
            @RequestBody RevisarRegistroDiarioNutriProRequest request
    ) {
        return gerenciarExperienciaPacienteNutriProUseCase
                .revisarRegistroDiario(request.paraCommand(empresaId, pacienteId, registroId))
                .map(RegistroDiarioNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pacientes/{pacienteId}/metas")
    public ResponseEntity<MetasNutriProResponse> listarMetas(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(MetasNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.listarMetas(new ConsultarPacienteCommand(empresaId, pacienteId))
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/metas")
    public ResponseEntity<MetaNutriProResponse> criarMeta(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @RequestBody CriarMetaNutriProRequest request
    ) {
        MetaNutriProResponse response = MetaNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.criarMeta(request.paraCommand(empresaId, pacienteId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/pacientes/" + pacienteId + "/metas/" + response.id()))
                .body(response);
    }

    @GetMapping("/pacientes/{pacienteId}/lembretes")
    public ResponseEntity<LembretesNutriProResponse> listarLembretes(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(LembretesNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.listarLembretes(new ConsultarPacienteCommand(empresaId, pacienteId))
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/lembretes")
    public ResponseEntity<LembreteNutriProResponse> criarLembrete(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @RequestBody CriarLembreteNutriProRequest request
    ) {
        LembreteNutriProResponse response = LembreteNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.criarLembrete(request.paraCommand(empresaId, pacienteId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/pacientes/" + pacienteId + "/lembretes/" + response.id()))
                .body(response);
    }

    @GetMapping("/pacientes/{pacienteId}/mensagens")
    public ResponseEntity<MensagensNutriProResponse> listarMensagens(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(MensagensNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.listarMensagens(new ConsultarPacienteCommand(empresaId, pacienteId))
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/mensagens")
    public ResponseEntity<MensagemNutriProResponse> enviarMensagem(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @RequestBody EnviarMensagemNutriProRequest request
    ) {
        MensagemNutriProResponse response = MensagemNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.enviarMensagem(request.paraCommand(empresaId, pacienteId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/pacientes/" + pacienteId + "/mensagens/" + response.id()))
                .body(response);
    }

    @PatchMapping("/pacientes/{pacienteId}/mensagens/lidas")
    public ResponseEntity<Void> marcarMensagensLidas(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @RequestParam String leitor
    ) {
        gerenciarExperienciaPacienteNutriProUseCase.marcarMensagensLidas(new MarcarMensagensLidasCommand(empresaId, pacienteId, leitor));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pacientes/{pacienteId}/evolucao")
    public ResponseEntity<EvolucoesNutriProResponse> listarEvolucaoPaciente(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(EvolucoesNutriProResponse.de(
                gerenciarExperienciaPacienteNutriProUseCase.listarEvolucao(new ConsultarPacienteCommand(empresaId, pacienteId))
        ));
    }
}
