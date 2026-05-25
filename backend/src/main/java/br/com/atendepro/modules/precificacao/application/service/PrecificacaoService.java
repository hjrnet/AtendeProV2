package br.com.atendepro.modules.precificacao.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.precificacao.application.command.CalcularCustoRealCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularMargemLucroCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularPrecoMinimoCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularPrecoRecomendadoCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularPrecificacaoBaseCommand;
import br.com.atendepro.modules.precificacao.application.command.ItemCustoPrecificacaoCommand;
import br.com.atendepro.modules.precificacao.application.command.SalvarSimulacaoPrecificacaoCommand;
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
import br.com.atendepro.modules.precificacao.application.port.out.AtualizarSimulacaoPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.CarregarSimulacaoPrecificacaoPorIdPort;
import br.com.atendepro.modules.precificacao.application.port.out.CarregarServicoParaPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.GerarRelatorioPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.ListarSimulacoesPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.SalvarSimulacaoPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.result.CalculoPrecificacaoBaseResult;
import br.com.atendepro.modules.precificacao.application.result.CustoRealPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.MargemLucroPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.PrecoMinimoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.PrecoRecomendadoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.RelatorioPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.ServicoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.SimulacaoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.AnaliseMargemLucroPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.CalculoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.CustoRealPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.ItemCustoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoMinimoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoRecomendadoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class PrecificacaoService implements
        CalcularPrecificacaoBaseUseCase,
        CalcularCustoRealUseCase,
        CalcularPrecoMinimoUseCase,
        CalcularPrecoRecomendadoUseCase,
        CalcularMargemLucroUseCase,
        SalvarSimulacaoPrecificacaoUseCase,
        AtualizarSimulacaoPrecificacaoUseCase,
        BuscarSimulacaoPrecificacaoUseCase,
        ListarSimulacoesPrecificacaoUseCase,
        GerarRelatorioPrecificacaoUseCase {

    private final CarregarServicoParaPrecificacaoPort carregarServicoParaPrecificacaoPort;
    private final SalvarSimulacaoPrecificacaoPort salvarSimulacaoPrecificacaoPort;
    private final AtualizarSimulacaoPrecificacaoPort atualizarSimulacaoPrecificacaoPort;
    private final CarregarSimulacaoPrecificacaoPorIdPort carregarSimulacaoPrecificacaoPorIdPort;
    private final ListarSimulacoesPrecificacaoPort listarSimulacoesPrecificacaoPort;
    private final GerarRelatorioPrecificacaoPort gerarRelatorioPrecificacaoPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public PrecificacaoService(
            CarregarServicoParaPrecificacaoPort carregarServicoParaPrecificacaoPort,
            SalvarSimulacaoPrecificacaoPort salvarSimulacaoPrecificacaoPort,
            AtualizarSimulacaoPrecificacaoPort atualizarSimulacaoPrecificacaoPort,
            CarregarSimulacaoPrecificacaoPorIdPort carregarSimulacaoPrecificacaoPorIdPort,
            ListarSimulacoesPrecificacaoPort listarSimulacoesPrecificacaoPort,
            GerarRelatorioPrecificacaoPort gerarRelatorioPrecificacaoPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarServicoParaPrecificacaoPort = carregarServicoParaPrecificacaoPort;
        this.salvarSimulacaoPrecificacaoPort = salvarSimulacaoPrecificacaoPort;
        this.atualizarSimulacaoPrecificacaoPort = atualizarSimulacaoPrecificacaoPort;
        this.carregarSimulacaoPrecificacaoPorIdPort = carregarSimulacaoPrecificacaoPorIdPort;
        this.listarSimulacoesPrecificacaoPort = listarSimulacoesPrecificacaoPort;
        this.gerarRelatorioPrecificacaoPort = gerarRelatorioPrecificacaoPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public CalculoPrecificacaoBaseResult calcularPrecificacaoBase(CalcularPrecificacaoBaseCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        String nomeProcedimento = nomeProcedimento(command.nomeProcedimento(), servico);
        List<ItemCustoPrecificacao> itensCusto = itensCusto(command.itensCusto());
        CalculoPrecificacao calculo = CalculoPrecificacao.calcular(
                empresaId,
                command.servicoProcedimentoId(),
                nomeProcedimento,
                itensCusto,
                Instant.now(clock)
        );
        return CalculoPrecificacaoBaseResult.de(calculo, servico);
    }

    @Override
    public CustoRealPrecificacaoResult calcularCustoReal(CalcularCustoRealCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        String nomeProcedimento = nomeProcedimento(command.nomeProcedimento(), servico);
        int duracaoMinutos = duracaoMinutos(command.duracaoMinutos(), servico);
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                empresaId,
                command.servicoProcedimentoId(),
                nomeProcedimento,
                duracaoMinutos,
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                Instant.now(clock)
        );
        return CustoRealPrecificacaoResult.de(custoReal, servico);
    }

    @Override
    public PrecoMinimoPrecificacaoResult calcularPrecoMinimo(CalcularPrecoMinimoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        String nomeProcedimento = nomeProcedimento(command.nomeProcedimento(), servico);
        int duracaoMinutos = duracaoMinutos(command.duracaoMinutos(), servico);
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                empresaId,
                command.servicoProcedimentoId(),
                nomeProcedimento,
                duracaoMinutos,
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                Instant.now(clock)
        );
        return PrecoMinimoPrecificacaoResult.de(PrecoMinimoPrecificacao.calcular(custoReal), servico);
    }

    @Override
    public PrecoRecomendadoPrecificacaoResult calcularPrecoRecomendado(CalcularPrecoRecomendadoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        String nomeProcedimento = nomeProcedimento(command.nomeProcedimento(), servico);
        int duracaoMinutos = duracaoMinutos(command.duracaoMinutos(), servico);
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                empresaId,
                command.servicoProcedimentoId(),
                nomeProcedimento,
                duracaoMinutos,
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                Instant.now(clock)
        );
        PrecoMinimoPrecificacao precoMinimo = PrecoMinimoPrecificacao.calcular(custoReal);
        PrecoRecomendadoPrecificacao precoRecomendado = PrecoRecomendadoPrecificacao.calcular(
                precoMinimo,
                command.margemDesejadaPercentual()
        );
        return PrecoRecomendadoPrecificacaoResult.de(precoRecomendado, servico);
    }

    @Override
    public MargemLucroPrecificacaoResult calcularMargemLucro(CalcularMargemLucroCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        String nomeProcedimento = nomeProcedimento(command.nomeProcedimento(), servico);
        int duracaoMinutos = duracaoMinutos(command.duracaoMinutos(), servico);
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                empresaId,
                command.servicoProcedimentoId(),
                nomeProcedimento,
                duracaoMinutos,
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                Instant.now(clock)
        );
        PrecoMinimoPrecificacao precoMinimo = PrecoMinimoPrecificacao.calcular(custoReal);
        AnaliseMargemLucroPrecificacao analise = AnaliseMargemLucroPrecificacao.analisar(
                precoMinimo,
                command.precoVenda()
        );
        return MargemLucroPrecificacaoResult.de(analise, servico);
    }

    @Override
    public SimulacaoPrecificacaoResult salvarSimulacao(SalvarSimulacaoPrecificacaoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        SimulacaoPrecificacao simulacao = montarSimulacao(command, empresaId, Instant.now(clock));
        salvarSimulacaoPrecificacaoPort.salvarSimulacao(simulacao);
        return SimulacaoPrecificacaoResult.de(simulacao);
    }

    @Override
    public SimulacaoPrecificacaoResult atualizarSimulacao(UUID simulacaoId, SalvarSimulacaoPrecificacaoCommand command) {
        validarPermissao();
        SimulacaoPrecificacao simulacaoAtual = carregarSimulacaoPrecificacaoPorIdPort.carregarSimulacaoPorId(simulacaoId)
                .orElseThrow(() -> new BusinessException(
                        "PRECIFICACAO_SIMULACAO_NAO_ENCONTRADA",
                        "Simulacao de precificacao nao encontrada."
        ));
        tenantAccessService.validarAcessoEmpresa(simulacaoAtual.empresaId());
        ServicoPrecificacaoResult servico = carregarServico(simulacaoAtual.servicoProcedimentoId(), simulacaoAtual.empresaId());
        int duracaoMinutos = duracaoMinutos(command.duracaoMinutos(), servico);
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                simulacaoAtual.empresaId(),
                simulacaoAtual.servicoProcedimentoId(),
                nomeProcedimento(command.nomeProcedimento(), servico),
                duracaoMinutos,
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                Instant.now(clock)
        );
        PrecoMinimoPrecificacao precoMinimo = PrecoMinimoPrecificacao.calcular(custoReal);
        PrecoRecomendadoPrecificacao precoRecomendado = PrecoRecomendadoPrecificacao.calcular(
                precoMinimo,
                command.margemDesejadaPercentual()
        );
        AnaliseMargemLucroPrecificacao analise = AnaliseMargemLucroPrecificacao.analisar(precoMinimo, command.precoVenda());
        SimulacaoPrecificacao simulacao = simulacaoAtual.editar(
                nomeProcedimento(command.nomeProcedimento(), servico),
                duracaoMinutos,
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                command.margemDesejadaPercentual(),
                command.precoVenda(),
                precoRecomendado,
                analise,
                Instant.now(clock)
        );
        atualizarSimulacaoPrecificacaoPort.atualizarSimulacao(simulacao);
        return SimulacaoPrecificacaoResult.de(simulacao);
    }

    @Override
    public Optional<SimulacaoPrecificacaoResult> buscarSimulacaoPorId(UUID simulacaoId) {
        validarPermissao();
        return carregarSimulacaoPrecificacaoPorIdPort.carregarSimulacaoPorId(simulacaoId)
                .filter(simulacao -> {
                    tenantAccessService.validarAcessoEmpresa(simulacao.empresaId());
                    return true;
                })
                .map(SimulacaoPrecificacaoResult::de);
    }

    @Override
    public ResultadoPaginado<SimulacaoPrecificacaoResult> listarSimulacoes(
            UUID empresaId,
            Paginacao paginacao,
            String busca
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var simulacoes = listarSimulacoesPrecificacaoPort.listarSimulacoes(empresaResolvida, paginacao, busca);
        return new ResultadoPaginado<>(
                simulacoes.itens().stream().map(SimulacaoPrecificacaoResult::de).toList(),
                simulacoes.totalItens(),
                simulacoes.pagina(),
                simulacoes.tamanho()
        );
    }

    @Override
    public RelatorioPrecificacaoResult gerarRelatorio(UUID simulacaoId) {
        validarPermissao();
        SimulacaoPrecificacao simulacao = carregarSimulacaoPrecificacaoPorIdPort.carregarSimulacaoPorId(simulacaoId)
                .orElseThrow(() -> new BusinessException(
                        "PRECIFICACAO_SIMULACAO_NAO_ENCONTRADA",
                        "Simulacao de precificacao nao encontrada."
        ));
        tenantAccessService.validarAcessoEmpresa(simulacao.empresaId());
        return gerarRelatorioPrecificacaoPort.gerarRelatorio(simulacao);
    }

    private SimulacaoPrecificacao montarSimulacao(
            SalvarSimulacaoPrecificacaoCommand command,
            UUID empresaId,
            Instant agora
    ) {
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        int duracaoMinutos = duracaoMinutos(command.duracaoMinutos(), servico);
        PrecoRecomendadoPrecificacao precoRecomendado = precoRecomendado(command, empresaId);
        AnaliseMargemLucroPrecificacao analise = analiseMargemLucro(command, empresaId);
        return SimulacaoPrecificacao.registrar(
                empresaId,
                command.servicoProcedimentoId(),
                nomeProcedimento(command.nomeProcedimento(), servico),
                duracaoMinutos,
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                command.margemDesejadaPercentual(),
                command.precoVenda(),
                precoRecomendado,
                analise,
                agora
        );
    }

    private PrecoRecomendadoPrecificacao precoRecomendado(SalvarSimulacaoPrecificacaoCommand command, UUID empresaId) {
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        CustoRealPrecificacao custoReal = custoReal(command, empresaId, servico);
        return PrecoRecomendadoPrecificacao.calcular(
                PrecoMinimoPrecificacao.calcular(custoReal),
                command.margemDesejadaPercentual()
        );
    }

    private AnaliseMargemLucroPrecificacao analiseMargemLucro(SalvarSimulacaoPrecificacaoCommand command, UUID empresaId) {
        ServicoPrecificacaoResult servico = carregarServico(command.servicoProcedimentoId(), empresaId);
        CustoRealPrecificacao custoReal = custoReal(command, empresaId, servico);
        return AnaliseMargemLucroPrecificacao.analisar(
                PrecoMinimoPrecificacao.calcular(custoReal),
                command.precoVenda()
        );
    }

    private CustoRealPrecificacao custoReal(
            SalvarSimulacaoPrecificacaoCommand command,
            UUID empresaId,
            ServicoPrecificacaoResult servico
    ) {
        return CustoRealPrecificacao.calcular(
                empresaId,
                command.servicoProcedimentoId(),
                nomeProcedimento(command.nomeProcedimento(), servico),
                duracaoMinutos(command.duracaoMinutos(), servico),
                command.custoInsumos(),
                command.custoSalaPorHora(),
                command.valorHoraProfissional(),
                command.custoDeslocamento(),
                command.custoAlimentacao(),
                command.taxas(),
                Instant.now(clock)
        );
    }

    private ServicoPrecificacaoResult carregarServico(UUID servicoProcedimentoId, UUID empresaId) {
        if (servicoProcedimentoId == null) {
            return null;
        }
        return carregarServicoParaPrecificacaoPort
                .carregarServicoParaPrecificacao(empresaId, servicoProcedimentoId)
                .orElseThrow(() -> new BusinessException(
                        "PRECIFICACAO_SERVICO_NAO_ENCONTRADO",
                        "Servico ou procedimento nao encontrado para precificacao."
                ));
    }

    private String nomeProcedimento(String nomeSolicitado, ServicoPrecificacaoResult servico) {
        if (nomeSolicitado != null && !nomeSolicitado.isBlank()) {
            return nomeSolicitado;
        }
        if (servico != null) {
            return servico.nome();
        }
        return nomeSolicitado;
    }

    private int duracaoMinutos(Integer duracaoSolicitada, ServicoPrecificacaoResult servico) {
        if (duracaoSolicitada != null) {
            return duracaoSolicitada;
        }
        if (servico != null) {
            return servico.duracaoMinutos();
        }
        throw new BusinessException(
                "PRECIFICACAO_DURACAO_OBRIGATORIA",
                "Duracao do procedimento e obrigatoria para calcular custo real."
        );
    }

    private List<ItemCustoPrecificacao> itensCusto(List<ItemCustoPrecificacaoCommand> itensCusto) {
        if (itensCusto == null) {
            return List.of();
        }
        return itensCusto.stream()
                .map(item -> new ItemCustoPrecificacao(item.descricao(), item.categoria(), item.valor()))
                .toList();
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException(
                    "PRECIFICACAO_EMPRESA_OBRIGATORIA",
                    "Empresa e obrigatoria para operar precificacao."
            );
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_PRECIFICACAO);
    }
}
