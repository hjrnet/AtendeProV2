package br.com.atendepro.modules.spaces.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.spaces.application.port.in.ConsultarIndicadoresSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.GerarRelatorioSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.out.CarregarIndicadoresSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.GerarPdfRelatorioSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.application.result.RelatorioSublocacaoSpacesResult;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class RelatorioSublocacaoSpacesService implements
        ConsultarIndicadoresSublocacaoSpacesUseCase,
        GerarRelatorioSublocacaoSpacesUseCase {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private final CarregarIndicadoresSublocacaoSpacesPort carregarIndicadoresSublocacaoSpacesPort;
    private final GerarPdfRelatorioSublocacaoSpacesPort gerarPdfRelatorioSublocacaoSpacesPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public RelatorioSublocacaoSpacesService(
            CarregarIndicadoresSublocacaoSpacesPort carregarIndicadoresSublocacaoSpacesPort,
            GerarPdfRelatorioSublocacaoSpacesPort gerarPdfRelatorioSublocacaoSpacesPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarIndicadoresSublocacaoSpacesPort = carregarIndicadoresSublocacaoSpacesPort;
        this.gerarPdfRelatorioSublocacaoSpacesPort = gerarPdfRelatorioSublocacaoSpacesPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public IndicadoresSublocacaoSpacesResult consultarIndicadores(UUID empresaId) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        PeriodoMensal periodo = periodoMensalAtual();
        return carregarIndicadoresSublocacaoSpacesPort.carregarIndicadores(
                empresaResolvida,
                periodo.inicio(),
                periodo.fim()
        );
    }

    @Override
    public RelatorioSublocacaoSpacesResult gerarRelatorio(UUID empresaId) {
        IndicadoresSublocacaoSpacesResult indicadores = consultarIndicadores(empresaId);
        return gerarPdfRelatorioSublocacaoSpacesPort.gerarPdf(indicadores);
    }

    private PeriodoMensal periodoMensalAtual() {
        LocalDate primeiroDia = LocalDate.now(clock.withZone(ZONE_ID)).withDayOfMonth(1);
        Instant inicio = primeiroDia.atStartOfDay(ZONE_ID).toInstant();
        Instant fim = primeiroDia.plusMonths(1).atStartOfDay(ZONE_ID).toInstant();
        return new PeriodoMensal(inicio, fim);
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
            throw new BusinessException("SPACES_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar Spaces.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_SPACES);
    }

    private record PeriodoMensal(Instant inicio, Instant fim) {
    }
}
