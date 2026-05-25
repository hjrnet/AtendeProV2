package br.com.atendepro.modules.documento.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.cliente.application.port.out.CarregarClientePacientePorIdPort;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.modules.documento.application.command.CriarDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.port.in.CriarDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.DetalharDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarDocumentosProfissionaisUseCase;
import br.com.atendepro.modules.documento.application.port.out.CarregarDocumentoProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.ListarDocumentosProfissionaisPort;
import br.com.atendepro.modules.documento.application.port.out.SalvarDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class DocumentoProfissionalService implements
        CriarDocumentoProfissionalUseCase,
        DetalharDocumentoProfissionalUseCase,
        ListarDocumentosProfissionaisUseCase {

    private final SalvarDocumentoProfissionalPort salvarDocumentoProfissionalPort;
    private final CarregarDocumentoProfissionalPorIdPort carregarDocumentoProfissionalPorIdPort;
    private final ListarDocumentosProfissionaisPort listarDocumentosProfissionaisPort;
    private final CarregarClientePacientePorIdPort carregarClientePacientePorIdPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public DocumentoProfissionalService(
            SalvarDocumentoProfissionalPort salvarDocumentoProfissionalPort,
            CarregarDocumentoProfissionalPorIdPort carregarDocumentoProfissionalPorIdPort,
            ListarDocumentosProfissionaisPort listarDocumentosProfissionaisPort,
            CarregarClientePacientePorIdPort carregarClientePacientePorIdPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarDocumentoProfissionalPort = salvarDocumentoProfissionalPort;
        this.carregarDocumentoProfissionalPorIdPort = carregarDocumentoProfissionalPorIdPort;
        this.listarDocumentosProfissionaisPort = listarDocumentosProfissionaisPort;
        this.carregarClientePacientePorIdPort = carregarClientePacientePorIdPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public DocumentoProfissionalResult criarDocumento(CriarDocumentoProfissionalCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClientePaciente(command.clientePacienteId(), empresaId);

        DocumentoProfissional documento = DocumentoProfissional.criar(
                empresaId,
                command.clientePacienteId(),
                command.profissionalId(),
                command.profissionalNome(),
                command.titulo(),
                command.tipo(),
                command.conteudo(),
                command.status(),
                Instant.now(clock)
        );
        salvarDocumentoProfissionalPort.salvarDocumento(documento);
        return DocumentoProfissionalResult.de(documento);
    }

    @Override
    public Optional<DocumentoProfissionalResult> detalharDocumento(UUID documentoId) {
        validarPermissao();
        return carregarDocumentoProfissionalPorIdPort.carregarDocumentoPorId(documentoId)
                .filter(documento -> {
                    tenantAccessService.validarAcessoEmpresa(documento.empresaId());
                    return true;
                })
                .map(DocumentoProfissionalResult::de);
    }

    @Override
    public ResultadoPaginado<DocumentoProfissionalResult> listarDocumentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            StatusDocumentoProfissional status,
            UUID clientePacienteId,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        validarClientePaciente(clientePacienteId, empresaResolvida);

        ResultadoPaginado<DocumentoProfissional> documentos = listarDocumentosProfissionaisPort.listarDocumentos(
                empresaResolvida,
                paginacao,
                busca,
                tipo,
                status,
                clientePacienteId,
                ativo
        );
        return new ResultadoPaginado<>(
                documentos.itens().stream().map(DocumentoProfissionalResult::de).toList(),
                documentos.totalItens(),
                documentos.pagina(),
                documentos.tamanho()
        );
    }

    private void validarClientePaciente(UUID clientePacienteId, UUID empresaId) {
        if (clientePacienteId == null) {
            return;
        }
        ClientePaciente cliente = carregarClientePacientePorIdPort.carregarClientePacientePorId(clientePacienteId)
                .orElseThrow(() -> new BusinessException(
                        "DOCUMENTO_CLIENTE_NAO_ENCONTRADO",
                        "Cliente ou paciente do documento nao encontrado."
                ));
        tenantAccessService.validarAcessoEmpresa(cliente.empresaId());
        if (!cliente.empresaId().equals(empresaId)) {
            throw new BusinessException(
                    "DOCUMENTO_CLIENTE_EMPRESA_DIVERGENTE",
                    "Cliente ou paciente nao pertence a empresa do documento."
            );
        }
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
            throw new BusinessException("DOCUMENTO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar documentos.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_DOCUMENTOS);
    }
}
