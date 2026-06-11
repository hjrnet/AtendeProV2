package br.com.atendepro.modules.agenda.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.agenda.application.command.AgendarCompromissoCommand;
import br.com.atendepro.modules.agenda.application.port.out.SalvarCompromissoAgendaPort;
import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.modules.agenda.domain.model.CompromissoAgenda;
import br.com.atendepro.modules.agenda.domain.model.TipoCompromisso;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class AgendaServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveAgendarCompromissoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.RECEPCIONISTA)));
        SalvarAgendaFake salvarAgendaFake = new SalvarAgendaFake();
        AgendaService service = service(salvarAgendaFake, false);

        var result = service.agendarCompromisso(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.status()).isEqualTo(AgendaStatus.AGENDADO);
        assertThat(salvarAgendaFake.compromissoSalvo.sala()).isEqualTo("Sala 1");
    }

    @Test
    void naoDeveAgendarComConflito() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        AgendaService service = service(compromisso -> {
        }, true);

        assertThatThrownBy(() -> service.agendarCompromisso(command(null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ja existe compromisso para este profissional ou sala no horario informado.");
    }

    @Test
    void deveListarAgendaPorEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        CompromissoAgenda compromisso = compromisso();
        AgendaService service = new AgendaService(
                compromissoSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, clientePacienteId, inicio, fim, profissionalId, sala, status) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(clientePacienteId).isNull();
                    assertThat(sala).isEqualTo("Sala 1");
                    return new ResultadoPaginado<>(List.of(compromisso), 1, paginacao.pagina(), paginacao.tamanho());
                },
                (empresaId, profissionalId, sala, inicio, fim) -> false,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarAgenda(null, new Paginacao(0, 20), null, null, null, null, "Sala 1", AgendaStatus.AGENDADO);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("sala").containsExactly("Sala 1");
    }

    @Test
    void naoDeveOperarAgendaSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        AgendaService service = service(compromisso -> {
        }, false);

        assertThatThrownBy(() -> service.listarAgenda(null, new Paginacao(0, 20), null, null, null, null, null, null))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private AgendaService service(SalvarCompromissoAgendaPort salvarPort, boolean existeConflito) {
        return new AgendaService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, clientePacienteId, inicio, fim, profissionalId, sala, status) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                (empresaId, profissionalId, sala, inicio, fim) -> existeConflito,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private AgendarCompromissoCommand command(UUID empresaId) {
        return new AgendarCompromissoCommand(
                empresaId,
                UUID.randomUUID(),
                UUID.fromString("4b8d2b0d-f226-42b3-a645-b91e36e5b77b"),
                "Dra Ana",
                "Sala 1",
                TipoCompromisso.ATENDIMENTO,
                Instant.parse("2026-05-25T12:00:00Z"),
                Instant.parse("2026-05-25T13:00:00Z"),
                "Agenda de teste"
        );
    }

    private CompromissoAgenda compromisso() {
        return CompromissoAgenda.agendar(
                EMPRESA_ID,
                UUID.randomUUID(),
                UUID.fromString("4b8d2b0d-f226-42b3-a645-b91e36e5b77b"),
                "Dra Ana",
                "Sala 1",
                TipoCompromisso.ATENDIMENTO,
                Instant.parse("2026-05-25T12:00:00Z"),
                Instant.parse("2026-05-25T13:00:00Z"),
                null,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarAgendaFake implements SalvarCompromissoAgendaPort {

        private CompromissoAgenda compromissoSalvo;

        @Override
        public void salvarCompromisso(CompromissoAgenda compromisso) {
            this.compromissoSalvo = compromisso;
        }
    }
}
