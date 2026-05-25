package br.com.atendepro.modules.custo.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.custo.application.command.CadastrarCustoAlimentacaoTransporteCommand;
import br.com.atendepro.modules.custo.application.port.in.CadastrarCustoAlimentacaoTransporteUseCase;
import br.com.atendepro.modules.custo.application.port.in.ListarCustosAlimentacaoTransporteUseCase;
import br.com.atendepro.modules.custo.application.port.out.ListarCustosAlimentacaoTransportePort;
import br.com.atendepro.modules.custo.application.port.out.SalvarCustoAlimentacaoTransportePort;
import br.com.atendepro.modules.custo.application.result.CustoAlimentacaoTransporteResult;
import br.com.atendepro.modules.custo.domain.model.CustoAlimentacaoTransporte;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class CustoAlimentacaoTransporteService implements
        CadastrarCustoAlimentacaoTransporteUseCase,
        ListarCustosAlimentacaoTransporteUseCase {

    private final SalvarCustoAlimentacaoTransportePort salvarCustoAlimentacaoTransportePort;
    private final ListarCustosAlimentacaoTransportePort listarCustosAlimentacaoTransportePort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public CustoAlimentacaoTransporteService(
            SalvarCustoAlimentacaoTransportePort salvarCustoAlimentacaoTransportePort,
            ListarCustosAlimentacaoTransportePort listarCustosAlimentacaoTransportePort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarCustoAlimentacaoTransportePort = salvarCustoAlimentacaoTransportePort;
        this.listarCustosAlimentacaoTransportePort = listarCustosAlimentacaoTransportePort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public CustoAlimentacaoTransporteResult cadastrarCustoAlimentacaoTransporte(CadastrarCustoAlimentacaoTransporteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        CustoAlimentacaoTransporte custo = CustoAlimentacaoTransporte.cadastrar(
                empresaId,
                command.profissionalId(),
                command.descricao(),
                command.tipo(),
                command.periodicidade(),
                command.valor(),
                Instant.now(clock)
        );
        salvarCustoAlimentacaoTransportePort.salvarCustoAlimentacaoTransporte(custo);
        return CustoAlimentacaoTransporteResult.de(custo);
    }

    @Override
    public ResultadoPaginado<CustoAlimentacaoTransporteResult> listarCustosAlimentacaoTransporte(
            UUID empresaId,
            Paginacao paginacao,
            TipoCustoPessoal tipo,
            UUID profissionalId,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var custos = listarCustosAlimentacaoTransportePort.listarCustosAlimentacaoTransporte(
                empresaResolvida,
                paginacao,
                tipo,
                profissionalId,
                ativo
        );
        return new ResultadoPaginado<>(
                custos.itens().stream().map(CustoAlimentacaoTransporteResult::de).toList(),
                custos.totalItens(),
                custos.pagina(),
                custos.tamanho()
        );
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
            throw new BusinessException("CUSTO_PESSOAL_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar custos pessoais.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_CUSTOS);
    }
}
