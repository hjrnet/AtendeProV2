package br.com.atendepro.modules.equipamento.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.equipamento.application.command.CadastrarEquipamentoCommand;
import br.com.atendepro.modules.equipamento.application.port.in.BuscarEquipamentoUseCase;
import br.com.atendepro.modules.equipamento.application.port.in.CadastrarEquipamentoUseCase;
import br.com.atendepro.modules.equipamento.application.port.in.ListarEquipamentosUseCase;
import br.com.atendepro.modules.equipamento.application.port.out.CarregarEquipamentoPorIdPort;
import br.com.atendepro.modules.equipamento.application.port.out.ListarEquipamentosPort;
import br.com.atendepro.modules.equipamento.application.port.out.SalvarEquipamentoPort;
import br.com.atendepro.modules.equipamento.application.result.EquipamentoResult;
import br.com.atendepro.modules.equipamento.domain.model.Equipamento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class EquipamentoService implements
        CadastrarEquipamentoUseCase,
        BuscarEquipamentoUseCase,
        ListarEquipamentosUseCase {

    private final SalvarEquipamentoPort salvarEquipamentoPort;
    private final CarregarEquipamentoPorIdPort carregarEquipamentoPorIdPort;
    private final ListarEquipamentosPort listarEquipamentosPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public EquipamentoService(
            SalvarEquipamentoPort salvarEquipamentoPort,
            CarregarEquipamentoPorIdPort carregarEquipamentoPorIdPort,
            ListarEquipamentosPort listarEquipamentosPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarEquipamentoPort = salvarEquipamentoPort;
        this.carregarEquipamentoPorIdPort = carregarEquipamentoPorIdPort;
        this.listarEquipamentosPort = listarEquipamentosPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public EquipamentoResult cadastrarEquipamento(CadastrarEquipamentoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        Equipamento equipamento = Equipamento.cadastrar(
                empresaId,
                command.nome(),
                command.categoria(),
                command.marca(),
                command.modelo(),
                command.numeroSerie(),
                command.valorAquisicao(),
                command.dataAquisicao(),
                command.vidaUtilMeses(),
                command.proximaManutencaoEm(),
                command.descricaoManutencao(),
                Instant.now(clock)
        );
        salvarEquipamentoPort.salvarEquipamento(equipamento);
        return EquipamentoResult.de(equipamento);
    }

    @Override
    public Optional<EquipamentoResult> buscarEquipamentoPorId(UUID equipamentoId) {
        validarPermissao();
        return carregarEquipamentoPorIdPort.carregarEquipamentoPorId(equipamentoId)
                .filter(equipamento -> {
                    tenantAccessService.validarAcessoEmpresa(equipamento.empresaId());
                    return true;
                })
                .map(EquipamentoResult::de);
    }

    @Override
    public ResultadoPaginado<EquipamentoResult> listarEquipamentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate manutencaoAte
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var equipamentos = listarEquipamentosPort.listarEquipamentos(
                empresaResolvida,
                paginacao,
                busca,
                categoria,
                ativo,
                manutencaoAte
        );
        return new ResultadoPaginado<>(
                equipamentos.itens().stream().map(EquipamentoResult::de).toList(),
                equipamentos.totalItens(),
                equipamentos.pagina(),
                equipamentos.tamanho()
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
            throw new BusinessException("EQUIPAMENTO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar equipamentos.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_EQUIPAMENTOS);
    }
}
