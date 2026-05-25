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
import br.com.atendepro.modules.precificacao.application.port.in.CalcularCustoRealUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularMargemLucroUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecoMinimoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecoRecomendadoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecificacaoBaseUseCase;
import br.com.atendepro.modules.precificacao.application.port.out.CarregarServicoParaPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.result.CalculoPrecificacaoBaseResult;
import br.com.atendepro.modules.precificacao.application.result.CustoRealPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.MargemLucroPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.PrecoMinimoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.PrecoRecomendadoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.ServicoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.AnaliseMargemLucroPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.CalculoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.CustoRealPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.ItemCustoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoMinimoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoRecomendadoPrecificacao;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class PrecificacaoService implements
        CalcularPrecificacaoBaseUseCase,
        CalcularCustoRealUseCase,
        CalcularPrecoMinimoUseCase,
        CalcularPrecoRecomendadoUseCase,
        CalcularMargemLucroUseCase {

    private final CarregarServicoParaPrecificacaoPort carregarServicoParaPrecificacaoPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public PrecificacaoService(
            CarregarServicoParaPrecificacaoPort carregarServicoParaPrecificacaoPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarServicoParaPrecificacaoPort = carregarServicoParaPrecificacaoPort;
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
