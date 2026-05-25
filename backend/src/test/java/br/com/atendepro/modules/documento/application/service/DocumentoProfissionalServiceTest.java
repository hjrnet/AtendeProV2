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
import br.com.atendepro.modules.documento.application.command.CriarDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.port.out.SalvarDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class DocumentoProfissionalServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("f899c2f6-2bc1-4e08-b730-686c42e07850");
    private static final UUID CLIENTE_ID = UUID.fromString("075ec9be-6fa1-4fdc-a044-1cbcf4ce32d1");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCriarDocumentoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarDocumentoFake salvarDocumentoFake = new SalvarDocumentoFake();
        DocumentoProfissionalService service = service(salvarDocumentoFake, Optional.empty());

        var result = service.criarDocumento(command(null, null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.status()).isEqualTo(StatusDocumentoProfissional.RASCUNHO);
        assertThat(salvarDocumentoFake.documentoSalvo.titulo()).isEqualTo("Declaracao de acompanhamento");
    }

    @Test
    void deveValidarClientePacienteDaEmpresaDoDocumento() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        SalvarDocumentoFake salvarDocumentoFake = new SalvarDocumentoFake();
        DocumentoProfissionalService service = service(salvarDocumentoFake, Optional.of(cliente(EMPRESA_ID)));

        var result = service.criarDocumento(command(null, CLIENTE_ID));

        assertThat(result.clientePacienteId()).isEqualTo(CLIENTE_ID);
        assertThat(salvarDocumentoFake.documentoSalvo.clientePacienteId()).isEqualTo(CLIENTE_ID);
    }

    @Test
    void deveListarDocumentosDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        DocumentoProfissional documento = documento(EMPRESA_ID);
        DocumentoProfissionalService service = new DocumentoProfissionalService(
                documentoSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, tipo, status, clientePacienteId, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("declaracao");
                    assertThat(tipo).isEqualTo(TipoDocumentoProfissional.DECLARACAO);
                    assertThat(status).isEqualTo(StatusDocumentoProfissional.RASCUNHO);
                    assertThat(ativo).isTrue();
                    return new ResultadoPaginado<>(List.of(documento), 1, paginacao.pagina(), paginacao.tamanho());
                },
                id -> Optional.empty(),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarDocumentos(
                null,
                new Paginacao(0, 20),
                "declaracao",
                TipoDocumentoProfissional.DECLARACAO,
                StatusDocumentoProfissional.RASCUNHO,
                null,
                true
        );

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("titulo").containsExactly("Declaracao de acompanhamento");
    }

    @Test
    void naoDevePermitirClientePacienteDeOutraEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        DocumentoProfissionalService service = service(documento -> {
        }, Optional.of(cliente(OUTRA_EMPRESA_ID)));

        assertThatThrownBy(() -> service.criarDocumento(command(null, CLIENTE_ID)))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        DocumentoProfissionalService service = service(documento -> {
        }, Optional.empty());

        assertThatThrownBy(() -> service.criarDocumento(command(null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar documentos.");
    }

    @Test
    void naoDeveOperarDocumentosSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        DocumentoProfissionalService service = service(documento -> {
        }, Optional.empty());

        assertThatThrownBy(() -> service.listarDocumentos(null, new Paginacao(0, 20), null, null, null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private DocumentoProfissionalService service(
            SalvarDocumentoProfissionalPort salvarPort,
            Optional<ClientePaciente> cliente
    ) {
        return new DocumentoProfissionalService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, tipo, status, clientePacienteId, ativo) ->
                        new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                id -> cliente,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CriarDocumentoProfissionalCommand command(UUID empresaId, UUID clientePacienteId) {
        return new CriarDocumentoProfissionalCommand(
                empresaId,
                clientePacienteId,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao de acompanhamento",
                TipoDocumentoProfissional.DECLARACAO,
                "Paciente em acompanhamento profissional.",
                null
        );
    }

    private DocumentoProfissional documento(UUID empresaId) {
        return DocumentoProfissional.criar(
                empresaId,
                null,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao de acompanhamento",
                TipoDocumentoProfissional.DECLARACAO,
                "Paciente em acompanhamento profissional.",
                StatusDocumentoProfissional.RASCUNHO,
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
