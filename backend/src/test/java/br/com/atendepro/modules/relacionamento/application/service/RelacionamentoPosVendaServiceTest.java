package br.com.atendepro.modules.relacionamento.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.relacionamento.application.command.CriarTarefaRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarContatoRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarPesquisaNpsRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.port.out.DadosPosVenda;
import br.com.atendepro.modules.relacionamento.application.port.out.RelacionamentoPosVendaPort;
import br.com.atendepro.modules.relacionamento.domain.model.CanalContatoRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.StatusTarefaRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.TipoTarefaRelacionamento;
import br.com.atendepro.shared.domain.exception.BusinessException;

class RelacionamentoPosVendaServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("69368f09-f981-4a1d-8d3d-4d9f999ce507");
    private static final UUID CLIENTE_ID = UUID.fromString("0d1fd885-30de-4573-982f-b9c6f85a66d4");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-08T12:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveMontarPainelPosVendaComRetornoNpsTemplatesESegmentos() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        RelacionamentoPosVendaService service = service(new FakeRelacionamentoPort());

        var painel = service.consultarPainel(null, AreaCliente.NUTRI, "maria");

        assertThat(painel.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(painel.metricas().clientesMonitorados()).isEqualTo(1);
        assertThat(painel.metricas().retornosPendentes()).isEqualTo(1);
        assertThat(painel.metricas().detratores()).isEqualTo(1);
        assertThat(painel.clientes()).first().extracting("statusAcompanhamento").isEqualTo("RETORNO_PENDENTE");
        assertThat(painel.tarefas()).anySatisfy(tarefa -> {
            assertThat(tarefa.id()).isNull();
            assertThat(tarefa.tipo()).isEqualTo(TipoTarefaRelacionamento.RETORNO);
        });
        assertThat(painel.templates()).extracting("codigo").contains("nutri-checkin", "nutri-retorno", "nps-satisfacao");
        assertThat(painel.segmentos()).extracting("codigo").contains("retorno-pendente", "nps-detrator");
    }

    @Test
    void deveRegistrarContatoComEmpresaRestritaDoTenant() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        RelacionamentoPosVendaService service = service(new FakeRelacionamentoPort());

        var contato = service.registrarContato(new RegistrarContatoRelacionamentoCommand(
                null,
                CLIENTE_ID,
                AreaCliente.BEAUTY,
                null,
                "beauty-pos-procedimento",
                "Oi, Ana! Como esta sua pele hoje?",
                "WhatsApp manual"
        ));

        assertThat(contato.clienteId()).isEqualTo(CLIENTE_ID);
        assertThat(contato.canal()).isEqualTo(CanalContatoRelacionamento.WHATSAPP);
        assertThat(contato.templateCodigo()).isEqualTo("beauty-pos-procedimento");
    }

    @Test
    void naoDeveAceitarAreaForaDeNutriEBeautyNaR16() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        RelacionamentoPosVendaService service = service(new FakeRelacionamentoPort());

        assertThatThrownBy(() -> service.consultarPainel(null, AreaCliente.FISIO, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Pos-venda R16 atende primeiro Nutri e Beauty.");
    }

    private RelacionamentoPosVendaService service(RelacionamentoPosVendaPort port) {
        return new RelacionamentoPosVendaService(
                port,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private static class FakeRelacionamentoPort implements RelacionamentoPosVendaPort {

        @Override
        public DadosPosVenda carregarDadosPosVenda(UUID empresaId, AreaCliente area, String busca, LocalDate hoje) {
            assertThat(empresaId).isEqualTo(EMPRESA_ID);
            assertThat(area).isEqualTo(AreaCliente.NUTRI);
            assertThat(busca).isEqualTo("maria");
            assertThat(hoje).isEqualTo(LocalDate.parse("2026-06-08"));
            return new DadosPosVenda(
                    List.of(new DadosPosVenda.Cliente(
                            CLIENTE_ID,
                            EMPRESA_ID,
                            "Maria Nutri",
                            AreaCliente.NUTRI,
                            "maria@example.com",
                            "11999999999",
                            LocalDate.parse("1990-06-20"),
                            Instant.parse("2026-04-20T10:00:00Z"),
                            null,
                            Instant.parse("2026-04-21T10:00:00Z"),
                            0,
                            5,
                            0,
                            1,
                            true,
                            Instant.parse("2026-04-20T10:00:00Z")
                    )),
                    List.of(),
                    List.of(),
                    List.of()
            );
        }

        @Override
        public DadosPosVenda.Contato salvarContato(RegistrarContatoRelacionamentoCommand command, Instant criadoEm) {
            assertThat(command.empresaId()).isEqualTo(EMPRESA_ID);
            assertThat(command.canal()).isEqualTo(CanalContatoRelacionamento.WHATSAPP);
            assertThat(criadoEm).isEqualTo(Instant.parse("2026-06-08T12:00:00Z"));
            return new DadosPosVenda.Contato(
                    UUID.randomUUID(),
                    command.empresaId(),
                    command.clienteId(),
                    "Ana Beauty",
                    command.area(),
                    command.canal(),
                    command.templateCodigo(),
                    command.mensagem(),
                    command.observacoes(),
                    criadoEm
            );
        }

        @Override
        public DadosPosVenda.PesquisaNps salvarPesquisaNps(RegistrarPesquisaNpsRelacionamentoCommand command, Instant criadoEm) {
            return new DadosPosVenda.PesquisaNps(UUID.randomUUID(), command.empresaId(), command.clienteId(), "Cliente", command.area(), command.nota(), command.comentario(), command.origem(), criadoEm);
        }

        @Override
        public DadosPosVenda.Tarefa salvarTarefa(CriarTarefaRelacionamentoCommand command, Instant criadoEm) {
            return new DadosPosVenda.Tarefa(UUID.randomUUID(), command.empresaId(), command.clienteId(), "Cliente", command.area(), command.tipo(), command.titulo(), command.descricao(), command.dataRecomendada(), StatusTarefaRelacionamento.PENDENTE, command.origem(), criadoEm, criadoEm);
        }

        @Override
        public Optional<DadosPosVenda.Tarefa> concluirTarefa(UUID empresaId, UUID tarefaId, Instant atualizadoEm) {
            return Optional.empty();
        }
    }
}
