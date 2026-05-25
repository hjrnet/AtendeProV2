package br.com.atendepro.modules.servico.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.servico.application.command.CadastrarServicoProcedimentoCommand;
import br.com.atendepro.modules.servico.application.port.out.SalvarServicoProcedimentoPort;
import br.com.atendepro.modules.servico.domain.model.ServicoProcedimento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class ServicoProcedimentoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarServicoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarServicoFake salvarServicoFake = new SalvarServicoFake();
        ServicoProcedimentoService service = service(salvarServicoFake);

        var result = service.cadastrarServicoProcedimento(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nome()).isEqualTo("Consulta Nutricional");
        assertThat(salvarServicoFake.servicoSalvo.precoBase()).isEqualByComparingTo("250.00");
    }

    @Test
    void deveListarServicosDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        ServicoProcedimento servico = servico();
        ServicoProcedimentoService service = new ServicoProcedimentoService(
                servicoSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, area, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("consulta");
                    assertThat(area).isEqualTo(AreaCliente.NUTRI);
                    return new ResultadoPaginado<>(List.of(servico), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarServicosProcedimentos(null, new Paginacao(0, 20), "consulta", AreaCliente.NUTRI, true);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nome").containsExactly("Consulta Nutricional");
    }

    @Test
    void naoDeveOperarServicosSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        ServicoProcedimentoService service = service(servico -> {
        });

        assertThatThrownBy(() -> service.listarServicosProcedimentos(null, new Paginacao(0, 20), null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private ServicoProcedimentoService service(SalvarServicoProcedimentoPort salvarPort) {
        return new ServicoProcedimentoService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, area, ativo) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarServicoProcedimentoCommand command(UUID empresaId) {
        return new CadastrarServicoProcedimentoCommand(
                empresaId,
                "Consulta Nutricional",
                "Consulta inicial",
                AreaCliente.NUTRI,
                60,
                new BigDecimal("250.00")
        );
    }

    private ServicoProcedimento servico() {
        return ServicoProcedimento.cadastrar(
                EMPRESA_ID,
                "Consulta Nutricional",
                "Consulta inicial",
                AreaCliente.NUTRI,
                60,
                new BigDecimal("250.00"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarServicoFake implements SalvarServicoProcedimentoPort {

        private ServicoProcedimento servicoSalvo;

        @Override
        public void salvarServicoProcedimento(ServicoProcedimento servico) {
            this.servicoSalvo = servico;
        }
    }
}
