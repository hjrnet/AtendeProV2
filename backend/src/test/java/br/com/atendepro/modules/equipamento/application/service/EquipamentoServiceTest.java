package br.com.atendepro.modules.equipamento.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
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
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.equipamento.application.command.CadastrarEquipamentoCommand;
import br.com.atendepro.modules.equipamento.application.port.out.SalvarEquipamentoPort;
import br.com.atendepro.modules.equipamento.domain.model.Equipamento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class EquipamentoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("eb7f8a03-5f07-4ea2-9e4a-efac9cfc26b8");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarEquipamentoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarEquipamentoFake salvarEquipamentoFake = new SalvarEquipamentoFake();
        EquipamentoService service = service(salvarEquipamentoFake);

        var result = service.cadastrarEquipamento(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nome()).isEqualTo("Autoclave");
        assertThat(salvarEquipamentoFake.equipamentoSalvo.valorAquisicao()).isEqualByComparingTo("3500.00");
    }

    @Test
    void deveListarEquipamentosDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        Equipamento equipamento = equipamento();
        EquipamentoService service = new EquipamentoService(
                equipamentoSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, categoria, ativo, manutencaoAte) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("auto");
                    assertThat(categoria).isEqualTo("Esterilizacao");
                    assertThat(manutencaoAte).isEqualTo(LocalDate.parse("2026-06-30"));
                    return new ResultadoPaginado<>(List.of(equipamento), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarEquipamentos(
                null,
                new Paginacao(0, 20),
                "auto",
                "Esterilizacao",
                true,
                LocalDate.parse("2026-06-30")
        );

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nome").containsExactly("Autoclave");
    }

    @Test
    void naoDeveOperarEquipamentosSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        EquipamentoService service = service(equipamento -> {
        });

        assertThatThrownBy(() -> service.listarEquipamentos(null, new Paginacao(0, 20), null, null, true, null))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private EquipamentoService service(SalvarEquipamentoPort salvarPort) {
        return new EquipamentoService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, categoria, ativo, manutencaoAte) ->
                        new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarEquipamentoCommand command(UUID empresaId) {
        return new CadastrarEquipamentoCommand(
                empresaId,
                "Autoclave",
                "Esterilizacao",
                "Marca A",
                "Modelo B",
                "SN-123",
                new BigDecimal("3500.00"),
                LocalDate.parse("2026-01-10"),
                60,
                LocalDate.parse("2026-06-30"),
                "Revisao preventiva"
        );
    }

    private Equipamento equipamento() {
        return Equipamento.cadastrar(
                EMPRESA_ID,
                "Autoclave",
                "Esterilizacao",
                "Marca A",
                "Modelo B",
                "SN-123",
                new BigDecimal("3500.00"),
                LocalDate.parse("2026-01-10"),
                60,
                LocalDate.parse("2026-06-30"),
                "Revisao preventiva",
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarEquipamentoFake implements SalvarEquipamentoPort {

        private Equipamento equipamentoSalvo;

        @Override
        public void salvarEquipamento(Equipamento equipamento) {
            this.equipamentoSalvo = equipamento;
        }
    }
}
