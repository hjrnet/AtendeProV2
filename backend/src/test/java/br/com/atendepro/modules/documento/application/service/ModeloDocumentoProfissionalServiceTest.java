package br.com.atendepro.modules.documento.application.service;

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
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.modules.cliente.domain.model.TipoCliente;
import br.com.atendepro.modules.documento.application.command.CriarDocumentoPorModeloCommand;
import br.com.atendepro.modules.documento.application.port.out.SalvarDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.ModeloDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class ModeloDocumentoProfissionalServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("f899c2f6-2bc1-4e08-b730-686c42e07850");
    private static final UUID CLIENTE_ID = UUID.fromString("075ec9be-6fa1-4fdc-a044-1cbcf4ce32d1");
    private static final UUID MODELO_ID = UUID.fromString("00000000-0000-0000-0000-000000006061");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveListarModelosGlobaisEDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        ModeloDocumentoProfissionalService service = new ModeloDocumentoProfissionalService(
                (empresaId, paginacao, busca, tipo, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("declaracao");
                    assertThat(tipo).isEqualTo(TipoDocumentoProfissional.DECLARACAO);
                    assertThat(ativo).isTrue();
                    return new ResultadoPaginado<>(List.of(modeloGlobal()), 1, paginacao.pagina(), paginacao.tamanho());
                },
                id -> Optional.empty(),
                documento -> {
                },
                id -> Optional.empty(),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarModelos(
                null,
                new Paginacao(0, 20),
                "declaracao",
                TipoDocumentoProfissional.DECLARACAO,
                true
        );

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nome").containsExactly("Declaracao profissional");
    }

    @Test
    void deveCriarDocumentoAPartirDeModeloGlobal() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        SalvarDocumentoFake salvarDocumentoFake = new SalvarDocumentoFake();
        ModeloDocumentoProfissionalService service = service(
                modeloGlobal(),
                salvarDocumentoFake,
                Optional.of(cliente(EMPRESA_ID))
        );

        var result = service.criarDocumentoPorModelo(command(null, CLIENTE_ID));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.tipo()).isEqualTo(TipoDocumentoProfissional.DECLARACAO);
        assertThat(result.status()).isEqualTo(StatusDocumentoProfissional.EMITIDO);
        assertThat(salvarDocumentoFake.documentoSalvo.titulo()).isEqualTo("Declaracao customizada");
        assertThat(salvarDocumentoFake.documentoSalvo.conteudo()).contains("Complemento:");
    }

    @Test
    void naoDeveCriarDocumentoComModeloDeOutraEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        ModeloDocumentoProfissionalService service = service(
                modeloDaEmpresa(OUTRA_EMPRESA_ID),
                documento -> {
                },
                Optional.empty()
        );

        assertThatThrownBy(() -> service.criarDocumentoPorModelo(command(null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Modelo de documento profissional nao pertence a empresa informada.");
    }

    @Test
    void naoDeveCriarDocumentoComModeloInativo() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        ModeloDocumentoProfissionalService service = service(
                modeloInativo(),
                documento -> {
                },
                Optional.empty()
        );

        assertThatThrownBy(() -> service.criarDocumentoPorModelo(command(null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Modelo de documento profissional esta inativo.");
    }

    @Test
    void naoDeveListarModelosSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        ModeloDocumentoProfissionalService service = service(
                modeloGlobal(),
                documento -> {
                },
                Optional.empty()
        );

        assertThatThrownBy(() -> service.listarModelos(null, new Paginacao(0, 20), null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private ModeloDocumentoProfissionalService service(
            ModeloDocumentoProfissional modelo,
            SalvarDocumentoProfissionalPort salvarPort,
            Optional<ClientePaciente> cliente
    ) {
        return new ModeloDocumentoProfissionalService(
                (empresaId, paginacao, busca, tipo, ativo) ->
                        new ResultadoPaginado<>(List.of(modelo), 1, paginacao.pagina(), paginacao.tamanho()),
                id -> Optional.of(modelo),
                salvarPort,
                id -> cliente,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CriarDocumentoPorModeloCommand command(UUID empresaId, UUID clientePacienteId) {
        return new CriarDocumentoPorModeloCommand(
                MODELO_ID,
                empresaId,
                clientePacienteId,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao customizada",
                "Paciente compareceu ao atendimento.",
                StatusDocumentoProfissional.EMITIDO
        );
    }

    private ModeloDocumentoProfissional modeloGlobal() {
        return new ModeloDocumentoProfissional(
                MODELO_ID,
                null,
                "Declaracao profissional",
                "Modelo geral",
                TipoDocumentoProfissional.DECLARACAO,
                "Declaracao profissional",
                "Declaramos para os devidos fins.",
                true,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private ModeloDocumentoProfissional modeloDaEmpresa(UUID empresaId) {
        return new ModeloDocumentoProfissional(
                MODELO_ID,
                empresaId,
                "Declaracao interna",
                "Modelo interno",
                TipoDocumentoProfissional.DECLARACAO,
                "Declaracao interna",
                "Declaramos para os devidos fins.",
                true,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private ModeloDocumentoProfissional modeloInativo() {
        return new ModeloDocumentoProfissional(
                MODELO_ID,
                null,
                "Declaracao inativa",
                "Modelo inativo",
                TipoDocumentoProfissional.DECLARACAO,
                "Declaracao inativa",
                "Declaramos para os devidos fins.",
                false,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private ClientePaciente cliente(UUID empresaId) {
        return ClientePaciente.cadastrar(
                empresaId,
                "Ana Cliente",
                TipoCliente.CLIENTE_PACIENTE,
                AreaCliente.NUTRI,
                "12345678900",
                "ana@test.local",
                "11999999999",
                LocalDate.parse("1990-01-10"),
                null,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarDocumentoFake implements SalvarDocumentoProfissionalPort {

        private DocumentoProfissional documentoSalvo;

        @Override
        public void salvarDocumento(DocumentoProfissional documento) {
            this.documentoSalvo = documento;
        }
    }
}
