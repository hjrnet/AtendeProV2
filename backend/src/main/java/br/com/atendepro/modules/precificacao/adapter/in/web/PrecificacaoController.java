package br.com.atendepro.modules.precificacao.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.port.in.AtualizarSimulacaoPrecificacaoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.BuscarSimulacaoPrecificacaoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularCustoRealUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularMargemLucroUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecoMinimoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecoRecomendadoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecificacaoBaseUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.GerarRelatorioPrecificacaoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.ListarSimulacoesPrecificacaoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.SalvarSimulacaoPrecificacaoUseCase;
import br.com.atendepro.modules.precificacao.application.result.RelatorioPrecificacaoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/precificacao")
@Profile("!test")
public class PrecificacaoController {

    private final CalcularPrecificacaoBaseUseCase calcularPrecificacaoBaseUseCase;
    private final CalcularCustoRealUseCase calcularCustoRealUseCase;
    private final CalcularPrecoMinimoUseCase calcularPrecoMinimoUseCase;
    private final CalcularPrecoRecomendadoUseCase calcularPrecoRecomendadoUseCase;
    private final CalcularMargemLucroUseCase calcularMargemLucroUseCase;
    private final SalvarSimulacaoPrecificacaoUseCase salvarSimulacaoPrecificacaoUseCase;
    private final AtualizarSimulacaoPrecificacaoUseCase atualizarSimulacaoPrecificacaoUseCase;
    private final BuscarSimulacaoPrecificacaoUseCase buscarSimulacaoPrecificacaoUseCase;
    private final ListarSimulacoesPrecificacaoUseCase listarSimulacoesPrecificacaoUseCase;
    private final GerarRelatorioPrecificacaoUseCase gerarRelatorioPrecificacaoUseCase;

    public PrecificacaoController(
            CalcularPrecificacaoBaseUseCase calcularPrecificacaoBaseUseCase,
            CalcularCustoRealUseCase calcularCustoRealUseCase,
            CalcularPrecoMinimoUseCase calcularPrecoMinimoUseCase,
            CalcularPrecoRecomendadoUseCase calcularPrecoRecomendadoUseCase,
            CalcularMargemLucroUseCase calcularMargemLucroUseCase,
            SalvarSimulacaoPrecificacaoUseCase salvarSimulacaoPrecificacaoUseCase,
            AtualizarSimulacaoPrecificacaoUseCase atualizarSimulacaoPrecificacaoUseCase,
            BuscarSimulacaoPrecificacaoUseCase buscarSimulacaoPrecificacaoUseCase,
            ListarSimulacoesPrecificacaoUseCase listarSimulacoesPrecificacaoUseCase,
            GerarRelatorioPrecificacaoUseCase gerarRelatorioPrecificacaoUseCase
    ) {
        this.calcularPrecificacaoBaseUseCase = calcularPrecificacaoBaseUseCase;
        this.calcularCustoRealUseCase = calcularCustoRealUseCase;
        this.calcularPrecoMinimoUseCase = calcularPrecoMinimoUseCase;
        this.calcularPrecoRecomendadoUseCase = calcularPrecoRecomendadoUseCase;
        this.calcularMargemLucroUseCase = calcularMargemLucroUseCase;
        this.salvarSimulacaoPrecificacaoUseCase = salvarSimulacaoPrecificacaoUseCase;
        this.atualizarSimulacaoPrecificacaoUseCase = atualizarSimulacaoPrecificacaoUseCase;
        this.buscarSimulacaoPrecificacaoUseCase = buscarSimulacaoPrecificacaoUseCase;
        this.listarSimulacoesPrecificacaoUseCase = listarSimulacoesPrecificacaoUseCase;
        this.gerarRelatorioPrecificacaoUseCase = gerarRelatorioPrecificacaoUseCase;
    }

    @PostMapping("/calculos/base")
    public ResponseEntity<CalculoPrecificacaoBaseResponse> calcularPrecificacaoBase(
            @Valid @RequestBody CalcularPrecificacaoBaseRequest request
    ) {
        return ResponseEntity.ok(CalculoPrecificacaoBaseResponse.de(
                calcularPrecificacaoBaseUseCase.calcularPrecificacaoBase(request.paraCommand())
        ));
    }

    @PostMapping("/calculos/custo-real")
    public ResponseEntity<CustoRealPrecificacaoResponse> calcularCustoReal(
            @Valid @RequestBody CalcularCustoRealRequest request
    ) {
        return ResponseEntity.ok(CustoRealPrecificacaoResponse.de(
                calcularCustoRealUseCase.calcularCustoReal(request.paraCommand())
        ));
    }

    @PostMapping("/calculos/preco-minimo")
    public ResponseEntity<PrecoMinimoPrecificacaoResponse> calcularPrecoMinimo(
            @Valid @RequestBody CalcularPrecoMinimoRequest request
    ) {
        return ResponseEntity.ok(PrecoMinimoPrecificacaoResponse.de(
                calcularPrecoMinimoUseCase.calcularPrecoMinimo(request.paraCommand())
        ));
    }

    @PostMapping("/calculos/preco-recomendado")
    public ResponseEntity<PrecoRecomendadoPrecificacaoResponse> calcularPrecoRecomendado(
            @Valid @RequestBody CalcularPrecoRecomendadoRequest request
    ) {
        return ResponseEntity.ok(PrecoRecomendadoPrecificacaoResponse.de(
                calcularPrecoRecomendadoUseCase.calcularPrecoRecomendado(request.paraCommand())
        ));
    }

    @PostMapping("/calculos/margem-lucro")
    public ResponseEntity<MargemLucroPrecificacaoResponse> calcularMargemLucro(
            @Valid @RequestBody CalcularMargemLucroRequest request
    ) {
        return ResponseEntity.ok(MargemLucroPrecificacaoResponse.de(
                calcularMargemLucroUseCase.calcularMargemLucro(request.paraCommand())
        ));
    }

    @PostMapping("/simulacoes")
    public ResponseEntity<SimulacaoPrecificacaoResponse> salvarSimulacao(
            @Valid @RequestBody SalvarSimulacaoPrecificacaoRequest request
    ) {
        SimulacaoPrecificacaoResponse response = SimulacaoPrecificacaoResponse.de(
                salvarSimulacaoPrecificacaoUseCase.salvarSimulacao(request.paraCommand())
        );
        return ResponseEntity
                .created(URI.create("/api/precificacao/simulacoes/" + response.id()))
                .header(HttpHeaders.LOCATION, "/api/precificacao/simulacoes/" + response.id())
                .body(response);
    }

    @PutMapping("/simulacoes/{simulacaoId}")
    public ResponseEntity<SimulacaoPrecificacaoResponse> atualizarSimulacao(
            @PathVariable UUID simulacaoId,
            @Valid @RequestBody SalvarSimulacaoPrecificacaoRequest request
    ) {
        return ResponseEntity.ok(SimulacaoPrecificacaoResponse.de(
                atualizarSimulacaoPrecificacaoUseCase.atualizarSimulacao(simulacaoId, request.paraCommand())
        ));
    }

    @GetMapping("/simulacoes/{simulacaoId}")
    public ResponseEntity<SimulacaoPrecificacaoResponse> buscarSimulacao(@PathVariable UUID simulacaoId) {
        return buscarSimulacaoPrecificacaoUseCase.buscarSimulacaoPorId(simulacaoId)
                .map(SimulacaoPrecificacaoResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/simulacoes/{simulacaoId}/relatorio.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarRelatorio(@PathVariable UUID simulacaoId) {
        RelatorioPrecificacaoResult relatorio = gerarRelatorioPrecificacaoUseCase.gerarRelatorio(simulacaoId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(relatorio.contentType()))
                .contentLength(relatorio.conteudo().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(relatorio.nomeArquivo())
                        .build()
                        .toString())
                .body(relatorio.conteudo());
    }

    @GetMapping("/simulacoes")
    public ResponseEntity<SimulacoesPrecificacaoPaginadasResponse> listarSimulacoes(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(SimulacoesPrecificacaoPaginadasResponse.de(
                listarSimulacoesPrecificacaoUseCase.listarSimulacoes(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca
                )
        ));
    }
}
