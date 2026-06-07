package br.com.atendepro.modules.precificacao.application.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.precificacao.application.port.in.GerarSugestoesPrecificacaoAssistidaUseCase;
import br.com.atendepro.modules.precificacao.application.port.out.CarregarSimulacaoPrecificacaoPorIdPort;
import br.com.atendepro.modules.precificacao.application.result.SugestaoPrecificacaoAssistidaResult;
import br.com.atendepro.modules.precificacao.application.result.SugestoesPrecificacaoAssistidaResult;
import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class PrecificacaoAssistidaService implements GerarSugestoesPrecificacaoAssistidaUseCase {

    private final CarregarSimulacaoPrecificacaoPorIdPort carregarSimulacaoPrecificacaoPorIdPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public PrecificacaoAssistidaService(
            CarregarSimulacaoPrecificacaoPorIdPort carregarSimulacaoPrecificacaoPorIdPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarSimulacaoPrecificacaoPorIdPort = carregarSimulacaoPrecificacaoPorIdPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public SugestoesPrecificacaoAssistidaResult gerarSugestoes(UUID simulacaoId) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_PRECIFICACAO);
        SimulacaoPrecificacao simulacao = carregarSimulacaoPrecificacaoPorIdPort.carregarSimulacaoPorId(simulacaoId)
                .orElseThrow(() -> new BusinessException(
                        "PRECIFICACAO_SIMULACAO_NAO_ENCONTRADA",
                        "Simulacao de precificacao nao encontrada."
                ));
        tenantAccessService.validarAcessoEmpresa(simulacao.empresaId());

        return new SugestoesPrecificacaoAssistidaResult(
                simulacao.id(),
                simulacao.empresaId(),
                simulacao.nomeProcedimento(),
                simulacao.statusMargem(),
                simulacao.custoTotal(),
                simulacao.precoMinimo(),
                simulacao.precoRecomendado(),
                simulacao.precoVenda(),
                simulacao.margemRealPercentual(),
                resumo(simulacao),
                sugestoes(simulacao),
                Instant.now(clock)
        );
    }

    private String resumo(SimulacaoPrecificacao simulacao) {
        if (simulacao.statusMargem() == StatusMargemPrecificacao.PREJUIZO) {
            return "Preco atual abaixo do minimo: a venda tende a gerar prejuizo operacional.";
        }
        if (simulacao.statusMargem() == StatusMargemPrecificacao.EQUILIBRIO) {
            return "Preco atual cobre custos, mas ainda nao cria margem saudavel para o negocio.";
        }
        if (simulacao.statusMargem() == StatusMargemPrecificacao.MARGEM_BAIXA) {
            return "Preco atual tem margem positiva, porem apertada para absorver variacoes de custo.";
        }
        return "Preco atual esta saudavel em relacao ao custo real e a margem simulada.";
    }

    private List<SugestaoPrecificacaoAssistidaResult> sugestoes(SimulacaoPrecificacao simulacao) {
        List<SugestaoPrecificacaoAssistidaResult> sugestoes = new ArrayList<>();
        if (simulacao.precoVenda().compareTo(simulacao.precoMinimo()) < 0) {
            sugestoes.add(new SugestaoPrecificacaoAssistidaResult(
                    "AJUSTE_PRECO",
                    "Subir ao menos ate o preco minimo",
                    "O preco de venda deve cobrir o custo real antes de qualquer margem comercial."
            ));
        }
        if (simulacao.precoVenda().compareTo(simulacao.precoRecomendado()) < 0) {
            sugestoes.add(new SugestaoPrecificacaoAssistidaResult(
                    "AJUSTE_PRECO",
                    "Aproximar do preco recomendado",
                    "Use o preco recomendado como referencia para proteger a margem desejada."
            ));
        }
        if (simulacao.margemRealPercentual().compareTo(new BigDecimal("20.00")) < 0) {
            sugestoes.add(new SugestaoPrecificacaoAssistidaResult(
                    "REVISAO_CUSTOS",
                    "Revisar insumos, sala e tempo profissional",
                    "Margens abaixo de 20% ficam sensiveis a descontos, atrasos e custos nao previstos."
            ));
        }
        if (simulacao.statusMargem() == StatusMargemPrecificacao.SAUDAVEL) {
            sugestoes.add(new SugestaoPrecificacaoAssistidaResult(
                    "COMERCIAL",
                    "Manter preco como referencia comercial",
                    "A simulacao indica margem saudavel; use este valor como base para pacotes e negociacoes."
            ));
        }
        return sugestoes;
    }
}
