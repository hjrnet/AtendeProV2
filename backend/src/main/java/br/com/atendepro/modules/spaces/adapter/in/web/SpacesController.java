package br.com.atendepro.modules.spaces.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.spaces.application.port.in.CadastrarRecursoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.CalcularCustoHoraSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.CadastrarPacoteSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.AgendarOcupacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ConsultarDisponibilidadeSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ConsultarIndicadoresSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ConsultarSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.DetalharPacoteSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.DetalharOcupacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.DetalharRecursoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.GerarRelatorioSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ListarOcupacoesSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ListarPacotesSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ListarRecursosSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.SimularParceiroSpacesUseCase;
import br.com.atendepro.modules.spaces.application.result.RelatorioSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/spaces")
@Profile("!test")
public class SpacesController {

    private final ConsultarSpacesUseCase consultarSpacesUseCase;
    private final CalcularCustoHoraSpacesUseCase calcularCustoHoraSpacesUseCase;
    private final CadastrarRecursoSpacesUseCase cadastrarRecursoSpacesUseCase;
    private final DetalharRecursoSpacesUseCase detalharRecursoSpacesUseCase;
    private final ListarRecursosSpacesUseCase listarRecursosSpacesUseCase;
    private final CadastrarPacoteSublocacaoSpacesUseCase cadastrarPacoteSublocacaoSpacesUseCase;
    private final DetalharPacoteSublocacaoSpacesUseCase detalharPacoteSublocacaoSpacesUseCase;
    private final ListarPacotesSublocacaoSpacesUseCase listarPacotesSublocacaoSpacesUseCase;
    private final SimularParceiroSpacesUseCase simularParceiroSpacesUseCase;
    private final AgendarOcupacaoSpacesUseCase agendarOcupacaoSpacesUseCase;
    private final DetalharOcupacaoSpacesUseCase detalharOcupacaoSpacesUseCase;
    private final ListarOcupacoesSpacesUseCase listarOcupacoesSpacesUseCase;
    private final ConsultarDisponibilidadeSpacesUseCase consultarDisponibilidadeSpacesUseCase;
    private final ConsultarIndicadoresSublocacaoSpacesUseCase consultarIndicadoresSublocacaoSpacesUseCase;
    private final GerarRelatorioSublocacaoSpacesUseCase gerarRelatorioSublocacaoSpacesUseCase;

    public SpacesController(
            ConsultarSpacesUseCase consultarSpacesUseCase,
            CalcularCustoHoraSpacesUseCase calcularCustoHoraSpacesUseCase,
            CadastrarRecursoSpacesUseCase cadastrarRecursoSpacesUseCase,
            DetalharRecursoSpacesUseCase detalharRecursoSpacesUseCase,
            ListarRecursosSpacesUseCase listarRecursosSpacesUseCase,
            CadastrarPacoteSublocacaoSpacesUseCase cadastrarPacoteSublocacaoSpacesUseCase,
            DetalharPacoteSublocacaoSpacesUseCase detalharPacoteSublocacaoSpacesUseCase,
            ListarPacotesSublocacaoSpacesUseCase listarPacotesSublocacaoSpacesUseCase,
            SimularParceiroSpacesUseCase simularParceiroSpacesUseCase,
            AgendarOcupacaoSpacesUseCase agendarOcupacaoSpacesUseCase,
            DetalharOcupacaoSpacesUseCase detalharOcupacaoSpacesUseCase,
            ListarOcupacoesSpacesUseCase listarOcupacoesSpacesUseCase,
            ConsultarDisponibilidadeSpacesUseCase consultarDisponibilidadeSpacesUseCase,
            ConsultarIndicadoresSublocacaoSpacesUseCase consultarIndicadoresSublocacaoSpacesUseCase,
            GerarRelatorioSublocacaoSpacesUseCase gerarRelatorioSublocacaoSpacesUseCase
    ) {
        this.consultarSpacesUseCase = consultarSpacesUseCase;
        this.calcularCustoHoraSpacesUseCase = calcularCustoHoraSpacesUseCase;
        this.cadastrarRecursoSpacesUseCase = cadastrarRecursoSpacesUseCase;
        this.detalharRecursoSpacesUseCase = detalharRecursoSpacesUseCase;
        this.listarRecursosSpacesUseCase = listarRecursosSpacesUseCase;
        this.cadastrarPacoteSublocacaoSpacesUseCase = cadastrarPacoteSublocacaoSpacesUseCase;
        this.detalharPacoteSublocacaoSpacesUseCase = detalharPacoteSublocacaoSpacesUseCase;
        this.listarPacotesSublocacaoSpacesUseCase = listarPacotesSublocacaoSpacesUseCase;
        this.simularParceiroSpacesUseCase = simularParceiroSpacesUseCase;
        this.agendarOcupacaoSpacesUseCase = agendarOcupacaoSpacesUseCase;
        this.detalharOcupacaoSpacesUseCase = detalharOcupacaoSpacesUseCase;
        this.listarOcupacoesSpacesUseCase = listarOcupacoesSpacesUseCase;
        this.consultarDisponibilidadeSpacesUseCase = consultarDisponibilidadeSpacesUseCase;
        this.consultarIndicadoresSublocacaoSpacesUseCase = consultarIndicadoresSublocacaoSpacesUseCase;
        this.gerarRelatorioSublocacaoSpacesUseCase = gerarRelatorioSublocacaoSpacesUseCase;
    }

    @GetMapping("/status")
    public ResponseEntity<SpacesStatusResponse> consultarStatus() {
        return ResponseEntity.ok(SpacesStatusResponse.de(consultarSpacesUseCase.consultarStatus()));
    }

    @PostMapping("/calculos/custo-hora")
    public ResponseEntity<CustoHoraSpacesResponse> calcularCustoHora(
            @Valid @RequestBody CalcularCustoHoraSpacesRequest request
    ) {
        return ResponseEntity.ok(CustoHoraSpacesResponse.de(
                calcularCustoHoraSpacesUseCase.calcularCustoHora(request.paraCommand())
        ));
    }

    @PostMapping("/recursos")
    public ResponseEntity<RecursoSpacesResponse> cadastrarRecurso(@Valid @RequestBody CadastrarRecursoSpacesRequest request) {
        RecursoSpacesResponse response = RecursoSpacesResponse.de(
                cadastrarRecursoSpacesUseCase.cadastrarRecurso(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/spaces/recursos/" + response.id())).body(response);
    }

    @GetMapping("/recursos/{recursoId}")
    public ResponseEntity<RecursoSpacesResponse> detalharRecurso(@PathVariable UUID recursoId) {
        return detalharRecursoSpacesUseCase.detalharRecurso(recursoId)
                .map(RecursoSpacesResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/recursos")
    public ResponseEntity<RecursosSpacesPaginadosResponse> listarRecursos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoRecursoSpaces tipo,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(RecursosSpacesPaginadosResponse.de(
                listarRecursosSpacesUseCase.listarRecursos(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        tipo,
                        ativo
                )
        ));
    }

    @PostMapping("/pacotes")
    public ResponseEntity<PacoteSublocacaoSpacesResponse> cadastrarPacote(
            @Valid @RequestBody CadastrarPacoteSublocacaoSpacesRequest request
    ) {
        PacoteSublocacaoSpacesResponse response = PacoteSublocacaoSpacesResponse.de(
                cadastrarPacoteSublocacaoSpacesUseCase.cadastrarPacote(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/spaces/pacotes/" + response.id())).body(response);
    }

    @GetMapping("/pacotes/{pacoteId}")
    public ResponseEntity<PacoteSublocacaoSpacesResponse> detalharPacote(@PathVariable UUID pacoteId) {
        return detalharPacoteSublocacaoSpacesUseCase.detalharPacote(pacoteId)
                .map(PacoteSublocacaoSpacesResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pacotes")
    public ResponseEntity<PacotesSublocacaoSpacesPaginadosResponse> listarPacotes(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) UUID recursoId,
            @RequestParam(required = false) TipoPacoteSublocacaoSpaces tipo,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(PacotesSublocacaoSpacesPaginadosResponse.de(
                listarPacotesSublocacaoSpacesUseCase.listarPacotes(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        recursoId,
                        tipo,
                        ativo
                )
        ));
    }

    @PostMapping("/simulacoes/parceiro")
    public ResponseEntity<SimulacaoParceiroSpacesResponse> simularParceiro(
            @Valid @RequestBody SimularParceiroSpacesRequest request
    ) {
        return ResponseEntity.ok(SimulacaoParceiroSpacesResponse.de(
                simularParceiroSpacesUseCase.simularParceiro(request.paraCommand())
        ));
    }

    @PostMapping("/ocupacoes")
    public ResponseEntity<OcupacaoSpacesResponse> agendarOcupacao(
            @Valid @RequestBody AgendarOcupacaoSpacesRequest request
    ) {
        OcupacaoSpacesResponse response = OcupacaoSpacesResponse.de(
                agendarOcupacaoSpacesUseCase.agendarOcupacao(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/spaces/ocupacoes/" + response.id())).body(response);
    }

    @GetMapping("/ocupacoes/{ocupacaoId}")
    public ResponseEntity<OcupacaoSpacesResponse> detalharOcupacao(@PathVariable UUID ocupacaoId) {
        return detalharOcupacaoSpacesUseCase.detalharOcupacao(ocupacaoId)
                .map(OcupacaoSpacesResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ocupacoes")
    public ResponseEntity<OcupacoesSpacesPaginadasResponse> listarOcupacoes(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) UUID recursoId,
            @RequestParam(required = false) Instant inicioEm,
            @RequestParam(required = false) Instant fimEm,
            @RequestParam(required = false) StatusOcupacaoSpaces status
    ) {
        return ResponseEntity.ok(OcupacoesSpacesPaginadasResponse.de(
                listarOcupacoesSpacesUseCase.listarOcupacoes(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        recursoId,
                        inicioEm,
                        fimEm,
                        status
                )
        ));
    }

    @PostMapping("/ocupacoes/disponibilidade")
    public ResponseEntity<DisponibilidadeSpacesResponse> consultarDisponibilidade(
            @Valid @RequestBody ConsultarDisponibilidadeSpacesRequest request
    ) {
        return ResponseEntity.ok(DisponibilidadeSpacesResponse.de(
                consultarDisponibilidadeSpacesUseCase.consultarDisponibilidade(request.paraCommand())
        ));
    }

    @GetMapping("/indicadores/sublocacao")
    public ResponseEntity<IndicadoresSublocacaoSpacesResponse> consultarIndicadoresSublocacao(
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(IndicadoresSublocacaoSpacesResponse.de(
                consultarIndicadoresSublocacaoSpacesUseCase.consultarIndicadores(empresaId)
        ));
    }

    @GetMapping(value = "/relatorios/sublocacao.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarRelatorioSublocacao(@RequestParam(required = false) UUID empresaId) {
        RelatorioSublocacaoSpacesResult relatorio = gerarRelatorioSublocacaoSpacesUseCase.gerarRelatorio(empresaId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(relatorio.contentType()))
                .contentLength(relatorio.conteudo().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(relatorio.nomeArquivo())
                        .build()
                        .toString())
                .body(relatorio.conteudo());
    }
}
