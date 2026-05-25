package br.com.atendepro.modules.agenda.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.agenda.application.command.AgendarCompromissoCommand;
import br.com.atendepro.modules.agenda.application.port.in.AgendarCompromissoUseCase;
import br.com.atendepro.modules.agenda.application.port.in.BuscarCompromissoAgendaUseCase;
import br.com.atendepro.modules.agenda.application.port.in.ListarAgendaUseCase;
import br.com.atendepro.modules.agenda.application.port.out.CarregarCompromissoAgendaPorIdPort;
import br.com.atendepro.modules.agenda.application.port.out.ListarAgendaPort;
import br.com.atendepro.modules.agenda.application.port.out.SalvarCompromissoAgendaPort;
import br.com.atendepro.modules.agenda.application.port.out.VerificarConflitoAgendaPort;
import br.com.atendepro.modules.agenda.application.result.CompromissoAgendaResult;
import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.modules.agenda.domain.model.CompromissoAgenda;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class AgendaService implements AgendarCompromissoUseCase, BuscarCompromissoAgendaUseCase, ListarAgendaUseCase {

    private final SalvarCompromissoAgendaPort salvarCompromissoAgendaPort;
    private final CarregarCompromissoAgendaPorIdPort carregarCompromissoAgendaPorIdPort;
    private final ListarAgendaPort listarAgendaPort;
    private final VerificarConflitoAgendaPort verificarConflitoAgendaPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public AgendaService(
            SalvarCompromissoAgendaPort salvarCompromissoAgendaPort,
            CarregarCompromissoAgendaPorIdPort carregarCompromissoAgendaPorIdPort,
            ListarAgendaPort listarAgendaPort,
            VerificarConflitoAgendaPort verificarConflitoAgendaPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarCompromissoAgendaPort = salvarCompromissoAgendaPort;
        this.carregarCompromissoAgendaPorIdPort = carregarCompromissoAgendaPorIdPort;
        this.listarAgendaPort = listarAgendaPort;
        this.verificarConflitoAgendaPort = verificarConflitoAgendaPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public CompromissoAgendaResult agendarCompromisso(AgendarCompromissoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        if (verificarConflitoAgendaPort.existeConflitoAgenda(
                empresaId,
                command.profissionalId(),
                command.sala(),
                command.inicio(),
                command.fim()
        )) {
            throw new BusinessException("AGENDA_CONFLITO_HORARIO", "Ja existe compromisso para este profissional ou sala no horario informado.");
        }

        CompromissoAgenda compromisso = CompromissoAgenda.agendar(
                empresaId,
                command.clientePacienteId(),
                command.profissionalId(),
                command.profissionalNome(),
                command.sala(),
                command.tipo(),
                command.inicio(),
                command.fim(),
                command.observacoes(),
                Instant.now(clock)
        );
        salvarCompromissoAgendaPort.salvarCompromisso(compromisso);
        return CompromissoAgendaResult.de(compromisso);
    }

    @Override
    public Optional<CompromissoAgendaResult> buscarCompromissoPorId(UUID compromissoId) {
        validarPermissao();
        return carregarCompromissoAgendaPorIdPort.carregarCompromissoPorId(compromissoId)
                .filter(compromisso -> {
                    tenantAccessService.validarAcessoEmpresa(compromisso.empresaId());
                    return true;
                })
                .map(CompromissoAgendaResult::de);
    }

    @Override
    public ResultadoPaginado<CompromissoAgendaResult> listarAgenda(
            UUID empresaId,
            Paginacao paginacao,
            Instant inicio,
            Instant fim,
            UUID profissionalId,
            String sala,
            AgendaStatus status
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        ResultadoPaginado<CompromissoAgenda> agenda = listarAgendaPort.listarAgenda(
                empresaResolvida,
                paginacao,
                inicio,
                fim,
                profissionalId,
                sala,
                status
        );
        return new ResultadoPaginado<>(
                agenda.itens().stream().map(CompromissoAgendaResult::de).toList(),
                agenda.totalItens(),
                agenda.pagina(),
                agenda.tamanho()
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
            throw new BusinessException("AGENDA_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar agenda.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_AGENDA);
    }
}
