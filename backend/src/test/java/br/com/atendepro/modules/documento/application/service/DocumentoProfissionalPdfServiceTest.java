package br.com.atendepro.modules.documento.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalPdfResult;
import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.domain.exception.BusinessException;

class DocumentoProfissionalPdfServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("f899c2f6-2bc1-4e08-b730-686c42e07850");
    private static final UUID DOCUMENTO_ID = UUID.fromString("31464259-fb23-4d6e-9b42-c2ad38d8b5fd");
    private static final UUID CARIMBO_ID = UUID.fromString("236b0c4b-c0bb-4a5c-b9be-4fcb94ffaf43");

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveGerarPdfDoDocumentoComCarimboDaMesmaEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        DocumentoProfissional documento = documento(EMPRESA_ID);
        CarimboProfissional carimbo = carimbo(EMPRESA_ID);
        DocumentoProfissionalPdfService service = new DocumentoProfissionalPdfService(
                id -> Optional.of(documento),
                id -> Optional.of(carimbo),
                empresaId -> Optional.of("Uso academico - Plano Estudante AtendePro"),
                (documentoCarregado, carimboCarregado, marcaDaguaAcademica) -> {
                    assertThat(documentoCarregado.id()).isEqualTo(DOCUMENTO_ID);
                    assertThat(carimboCarregado.id()).isEqualTo(CARIMBO_ID);
                    assertThat(marcaDaguaAcademica).isEqualTo("Uso academico - Plano Estudante AtendePro");
                    return new DocumentoProfissionalPdfResult("documento.pdf", "application/pdf", "%PDF".getBytes());
                },
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        var result = service.gerarPdf(DOCUMENTO_ID, CARIMBO_ID);

        assertThat(result.contentType()).isEqualTo("application/pdf");
    }

    @Test
    void naoDeveGerarPdfComCarimboDeOutraEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        DocumentoProfissionalPdfService service = new DocumentoProfissionalPdfService(
                id -> Optional.of(documento(EMPRESA_ID)),
                id -> Optional.of(carimbo(OUTRA_EMPRESA_ID)),
                empresaId -> Optional.empty(),
                (documento, carimbo, marcaDaguaAcademica) -> new DocumentoProfissionalPdfResult("documento.pdf", "application/pdf", "%PDF".getBytes()),
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        assertThatThrownBy(() -> service.gerarPdf(DOCUMENTO_ID, CARIMBO_ID))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void naoDeveGerarPdfSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        DocumentoProfissionalPdfService service = new DocumentoProfissionalPdfService(
                id -> Optional.of(documento(EMPRESA_ID)),
                id -> Optional.empty(),
                empresaId -> Optional.empty(),
                (documento, carimbo, marcaDaguaAcademica) -> new DocumentoProfissionalPdfResult("documento.pdf", "application/pdf", "%PDF".getBytes()),
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        assertThatThrownBy(() -> service.gerarPdf(DOCUMENTO_ID, null))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    @Test
    void deveFalharQuandoDocumentoNaoExistir() {
        DocumentoProfissionalPdfService service = new DocumentoProfissionalPdfService(
                id -> Optional.empty(),
                id -> Optional.empty(),
                empresaId -> Optional.empty(),
                (documento, carimbo, marcaDaguaAcademica) -> new DocumentoProfissionalPdfResult("documento.pdf", "application/pdf", "%PDF".getBytes()),
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        assertThatThrownBy(() -> service.gerarPdf(DOCUMENTO_ID, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Documento profissional nao encontrado.");
    }

    private DocumentoProfissional documento(UUID empresaId) {
        return new DocumentoProfissional(
                DOCUMENTO_ID,
                empresaId,
                null,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao TASK-0603",
                TipoDocumentoProfissional.DECLARACAO,
                "Paciente em acompanhamento profissional.",
                StatusDocumentoProfissional.EMITIDO,
                1,
                "codigo-task-0604",
                true,
                true,
                Instant.parse("2026-05-25T12:00:00Z"),
                Instant.parse("2026-05-25T12:00:00Z")
        );
    }

    private CarimboProfissional carimbo(UUID empresaId) {
        return new CarimboProfissional(
                CARIMBO_ID,
                empresaId,
                UUID.randomUUID(),
                "Dra. Marina",
                ConselhoProfissional.CRN,
                "SP",
                "CRN-0603",
                "Dra. Marina - CRN-0603",
                "AtendePro Clinica Demo",
                true,
                Instant.parse("2026-05-25T12:00:00Z"),
                Instant.parse("2026-05-25T12:00:00Z")
        );
    }
}
