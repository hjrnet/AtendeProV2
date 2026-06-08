package br.com.atendepro.modules.relacionamento.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
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
import br.com.atendepro.modules.relacionamento.application.command.CriarTarefaRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarContatoRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarPesquisaNpsRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.port.in.RelacionamentoPosVendaUseCase;
import br.com.atendepro.modules.relacionamento.application.result.PainelPosVendaResult;
import br.com.atendepro.modules.relacionamento.domain.model.CanalContatoRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.StatusTarefaRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.TipoTarefaRelacionamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/relacionamento")
@Profile("!test")
public class RelacionamentoPosVendaController {

    private final RelacionamentoPosVendaUseCase relacionamentoPosVendaUseCase;

    public RelacionamentoPosVendaController(RelacionamentoPosVendaUseCase relacionamentoPosVendaUseCase) {
        this.relacionamentoPosVendaUseCase = relacionamentoPosVendaUseCase;
    }

    @GetMapping("/pos-venda")
    public ResponseEntity<PainelPosVendaResponse> consultarPainel(
            @RequestParam UUID empresaId,
            @RequestParam(required = false) AreaCliente area,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(PainelPosVendaResponse.de(
                relacionamentoPosVendaUseCase.consultarPainel(empresaId, area, busca)
        ));
    }

    @PostMapping("/contatos")
    public ResponseEntity<ContatoPosVendaResponse> registrarContato(@Valid @RequestBody RegistrarContatoPosVendaRequest request) {
        ContatoPosVendaResponse response = ContatoPosVendaResponse.de(
                relacionamentoPosVendaUseCase.registrarContato(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/relacionamento/contatos/" + response.id())).body(response);
    }

    @PostMapping("/nps")
    public ResponseEntity<PesquisaNpsPosVendaResponse> registrarNps(@Valid @RequestBody RegistrarNpsPosVendaRequest request) {
        PesquisaNpsPosVendaResponse response = PesquisaNpsPosVendaResponse.de(
                relacionamentoPosVendaUseCase.registrarPesquisaNps(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/relacionamento/nps/" + response.id())).body(response);
    }

    @PostMapping("/tarefas")
    public ResponseEntity<TarefaPosVendaResponse> criarTarefa(@Valid @RequestBody CriarTarefaPosVendaRequest request) {
        TarefaPosVendaResponse response = TarefaPosVendaResponse.de(
                relacionamentoPosVendaUseCase.criarTarefa(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/relacionamento/tarefas/" + response.id())).body(response);
    }

    @PatchMapping("/tarefas/{tarefaId}/concluir")
    public ResponseEntity<TarefaPosVendaResponse> concluirTarefa(
            @PathVariable UUID tarefaId,
            @RequestParam UUID empresaId
    ) {
        return relacionamentoPosVendaUseCase.concluirTarefa(empresaId, tarefaId)
                .map(TarefaPosVendaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record RegistrarContatoPosVendaRequest(
            @NotNull UUID empresaId,
            @NotNull UUID clienteId,
            @NotNull AreaCliente area,
            CanalContatoRelacionamento canal,
            String templateCodigo,
            @NotBlank String mensagem,
            String observacoes
    ) {
        RegistrarContatoRelacionamentoCommand paraCommand() {
            return new RegistrarContatoRelacionamentoCommand(empresaId, clienteId, area, canal, templateCodigo, mensagem, observacoes);
        }
    }

    public record RegistrarNpsPosVendaRequest(
            @NotNull UUID empresaId,
            @NotNull UUID clienteId,
            @NotNull AreaCliente area,
            @Min(0) @Max(10) int nota,
            String comentario,
            String origem
    ) {
        RegistrarPesquisaNpsRelacionamentoCommand paraCommand() {
            return new RegistrarPesquisaNpsRelacionamentoCommand(empresaId, clienteId, area, nota, comentario, origem);
        }
    }

    public record CriarTarefaPosVendaRequest(
            @NotNull UUID empresaId,
            @NotNull UUID clienteId,
            @NotNull AreaCliente area,
            TipoTarefaRelacionamento tipo,
            @NotBlank String titulo,
            String descricao,
            LocalDate dataRecomendada,
            String origem
    ) {
        CriarTarefaRelacionamentoCommand paraCommand() {
            return new CriarTarefaRelacionamentoCommand(empresaId, clienteId, area, tipo, titulo, descricao, dataRecomendada, origem);
        }
    }

    public record PainelPosVendaResponse(
            UUID empresaId,
            AreaCliente area,
            MetricasPosVendaResponse metricas,
            List<ClientePosVendaResponse> clientes,
            List<TarefaPosVendaResponse> tarefas,
            List<TemplateMensagemPosVendaResponse> templates,
            List<ContatoPosVendaResponse> contatosRecentes,
            List<PesquisaNpsPosVendaResponse> npsRecentes,
            List<SegmentoCampanhaPosVendaResponse> segmentos,
            Instant atualizadoEm
    ) {
        static PainelPosVendaResponse de(PainelPosVendaResult result) {
            return new PainelPosVendaResponse(
                    result.empresaId(),
                    result.area(),
                    MetricasPosVendaResponse.de(result.metricas()),
                    result.clientes().stream().map(ClientePosVendaResponse::de).toList(),
                    result.tarefas().stream().map(TarefaPosVendaResponse::de).toList(),
                    result.templates().stream().map(TemplateMensagemPosVendaResponse::de).toList(),
                    result.contatosRecentes().stream().map(ContatoPosVendaResponse::de).toList(),
                    result.npsRecentes().stream().map(PesquisaNpsPosVendaResponse::de).toList(),
                    result.segmentos().stream().map(SegmentoCampanhaPosVendaResponse::de).toList(),
                    result.atualizadoEm()
            );
        }
    }

    public record MetricasPosVendaResponse(
            int clientesMonitorados,
            int retornosPendentes,
            int clientesInativos,
            int faltasRecentes,
            int clientesSemContato,
            int oportunidadesRecorrencia,
            double npsMedio,
            int detratores
    ) {
        static MetricasPosVendaResponse de(PainelPosVendaResult.Metricas metricas) {
            return new MetricasPosVendaResponse(
                    metricas.clientesMonitorados(),
                    metricas.retornosPendentes(),
                    metricas.clientesInativos(),
                    metricas.faltasRecentes(),
                    metricas.clientesSemContato(),
                    metricas.oportunidadesRecorrencia(),
                    metricas.npsMedio(),
                    metricas.detratores()
            );
        }
    }

    public record ClientePosVendaResponse(
            UUID id,
            String nome,
            AreaCliente area,
            String email,
            String telefone,
            LocalDate dataNascimento,
            Instant ultimaConsultaEm,
            Instant proximaConsultaEm,
            Instant ultimoContatoEm,
            int faltasRecentes,
            Integer ultimaNotaNps,
            String statusAcompanhamento,
            String statusRotulo,
            LocalDate retornoRecomendadoEm,
            String motivoRetorno,
            String riscoAbandono,
            boolean aniversarioProximo,
            boolean oportunidadeRecorrencia
    ) {
        static ClientePosVendaResponse de(PainelPosVendaResult.Cliente cliente) {
            return new ClientePosVendaResponse(
                    cliente.id(),
                    cliente.nome(),
                    cliente.area(),
                    cliente.email(),
                    cliente.telefone(),
                    cliente.dataNascimento(),
                    cliente.ultimaConsultaEm(),
                    cliente.proximaConsultaEm(),
                    cliente.ultimoContatoEm(),
                    cliente.faltasRecentes(),
                    cliente.ultimaNotaNps(),
                    cliente.statusAcompanhamento(),
                    cliente.statusRotulo(),
                    cliente.retornoRecomendadoEm(),
                    cliente.motivoRetorno(),
                    cliente.riscoAbandono(),
                    cliente.aniversarioProximo(),
                    cliente.oportunidadeRecorrencia()
            );
        }
    }

    public record TarefaPosVendaResponse(
            UUID id,
            UUID clienteId,
            String clienteNome,
            AreaCliente area,
            TipoTarefaRelacionamento tipo,
            String titulo,
            String descricao,
            LocalDate dataRecomendada,
            StatusTarefaRelacionamento status,
            String origem,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        static TarefaPosVendaResponse de(PainelPosVendaResult.Tarefa tarefa) {
            return new TarefaPosVendaResponse(
                    tarefa.id(),
                    tarefa.clienteId(),
                    tarefa.clienteNome(),
                    tarefa.area(),
                    tarefa.tipo(),
                    tarefa.titulo(),
                    tarefa.descricao(),
                    tarefa.dataRecomendada(),
                    tarefa.status(),
                    tarefa.origem(),
                    tarefa.criadoEm(),
                    tarefa.atualizadoEm()
            );
        }
    }

    public record TemplateMensagemPosVendaResponse(
            String codigo,
            AreaCliente area,
            String titulo,
            String objetivo,
            String mensagem,
            List<String> variaveis
    ) {
        static TemplateMensagemPosVendaResponse de(PainelPosVendaResult.TemplateMensagem template) {
            return new TemplateMensagemPosVendaResponse(
                    template.codigo(),
                    template.area(),
                    template.titulo(),
                    template.objetivo(),
                    template.mensagem(),
                    template.variaveis()
            );
        }
    }

    public record ContatoPosVendaResponse(
            UUID id,
            UUID clienteId,
            String clienteNome,
            AreaCliente area,
            CanalContatoRelacionamento canal,
            String templateCodigo,
            String mensagem,
            String observacoes,
            Instant criadoEm
    ) {
        static ContatoPosVendaResponse de(PainelPosVendaResult.Contato contato) {
            return new ContatoPosVendaResponse(
                    contato.id(),
                    contato.clienteId(),
                    contato.clienteNome(),
                    contato.area(),
                    contato.canal(),
                    contato.templateCodigo(),
                    contato.mensagem(),
                    contato.observacoes(),
                    contato.criadoEm()
            );
        }
    }

    public record PesquisaNpsPosVendaResponse(
            UUID id,
            UUID clienteId,
            String clienteNome,
            AreaCliente area,
            int nota,
            String comentario,
            String origem,
            Instant criadoEm
    ) {
        static PesquisaNpsPosVendaResponse de(PainelPosVendaResult.PesquisaNps pesquisa) {
            return new PesquisaNpsPosVendaResponse(
                    pesquisa.id(),
                    pesquisa.clienteId(),
                    pesquisa.clienteNome(),
                    pesquisa.area(),
                    pesquisa.nota(),
                    pesquisa.comentario(),
                    pesquisa.origem(),
                    pesquisa.criadoEm()
            );
        }
    }

    public record SegmentoCampanhaPosVendaResponse(
            String codigo,
            String titulo,
            String descricao,
            int quantidadeClientes,
            String acaoRecomendada
    ) {
        static SegmentoCampanhaPosVendaResponse de(PainelPosVendaResult.SegmentoCampanha segmento) {
            return new SegmentoCampanhaPosVendaResponse(
                    segmento.codigo(),
                    segmento.titulo(),
                    segmento.descricao(),
                    segmento.quantidadeClientes(),
                    segmento.acaoRecomendada()
            );
        }
    }
}
