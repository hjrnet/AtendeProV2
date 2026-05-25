package br.com.atendepro.modules.documento.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.documento.application.command.CancelarDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.command.SubstituirDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.port.in.CancelarDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarHistoricoDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.SubstituirDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.out.AtualizarDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.port.out.CarregarDocumentoProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.ListarHistoricoDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.port.out.RegistrarHistoricoDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;
import br.com.atendepro.modules.documento.application.result.HistoricoDocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.HistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class DocumentoProfissionalHistoricoService implements
        SubstituirDocumentoProfissionalUseCase,
        CancelarDocumentoProfissionalUseCase,
        ListarHistoricoDocumentoProfissionalUseCase {

    private final CarregarDocumentoProfissionalPorIdPort carregarDocumentoProfissionalPorIdPort;
    private final AtualizarDocumentoProfissionalPort atualizarDocumentoProfissionalPort;
    private final RegistrarHistoricoDocumentoProfissionalPort registrarHistoricoDocumentoProfissionalPort;
    private final ListarHistoricoDocumentoProfissionalPort listarHistoricoDocumentoProfissionalPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public DocumentoProfissionalHistoricoService(
            CarregarDocumentoProfissionalPorIdPort carregarDocumentoProfissionalPorIdPort,
            AtualizarDocumentoProfissionalPort atualizarDocumentoProfissionalPort,
            RegistrarHistoricoDocumentoProfissionalPort registrarHistoricoDocumentoProfissionalPort,
            ListarHistoricoDocumentoProfissionalPort listarHistoricoDocumentoProfissionalPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarDocumentoProfissionalPorIdPort = carregarDocumentoProfissionalPorIdPort;
        this.atualizarDocumentoProfissionalPort = atualizarDocumentoProfissionalPort;
        this.registrarHistoricoDocumentoProfissionalPort = registrarHistoricoDocumentoProfissionalPort;
        this.listarHistoricoDocumentoProfissionalPort = listarHistoricoDocumentoProfissionalPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    @Transactional
    public DocumentoProfissionalResult substituirDocumento(SubstituirDocumentoProfissionalCommand command) {
        validarPermissao();
        DocumentoProfissional anterior = carregarDocumento(command.documentoId());
        validarDocumentoAlteravel(anterior);
        if (command.status() == StatusDocumentoProfissional.CANCELADO) {
            throw new BusinessException(
                    "DOCUMENTO_STATUS_SUBSTITUICAO_INVALIDO",
                    "Use o cancelamento para cancelar documento profissional."
            );
        }

        Instant agora = Instant.now(clock);
        DocumentoProfissional novo = anterior.substituir(command.titulo(), command.conteudo(), command.status(), agora);
        HistoricoDocumentoProfissional historico = HistoricoDocumentoProfissional.registrarSubstituicao(
                anterior,
                novo,
                command.motivo(),
                usuarioAtualId(),
                agora
        );
        atualizarDocumentoProfissionalPort.atualizarDocumento(novo);
        registrarHistoricoDocumentoProfissionalPort.registrarHistorico(historico);
        return DocumentoProfissionalResult.de(novo);
    }

    @Override
    @Transactional
    public DocumentoProfissionalResult cancelarDocumento(CancelarDocumentoProfissionalCommand command) {
        validarPermissao();
        DocumentoProfissional anterior = carregarDocumento(command.documentoId());
        validarDocumentoAlteravel(anterior);

        Instant agora = Instant.now(clock);
        DocumentoProfissional novo = anterior.cancelar(agora);
        HistoricoDocumentoProfissional historico = HistoricoDocumentoProfissional.registrarCancelamento(
                anterior,
                novo,
                command.motivo(),
                usuarioAtualId(),
                agora
        );
        atualizarDocumentoProfissionalPort.atualizarDocumento(novo);
        registrarHistoricoDocumentoProfissionalPort.registrarHistorico(historico);
        return DocumentoProfissionalResult.de(novo);
    }

    @Override
    public ResultadoPaginado<HistoricoDocumentoProfissionalResult> listarHistorico(
            UUID documentoId,
            Paginacao paginacao
    ) {
        validarPermissao();
        DocumentoProfissional documento = carregarDocumento(documentoId);
        ResultadoPaginado<HistoricoDocumentoProfissional> historico =
                listarHistoricoDocumentoProfissionalPort.listarHistorico(documento.id(), documento.empresaId(), paginacao);
        return new ResultadoPaginado<>(
                historico.itens().stream().map(HistoricoDocumentoProfissionalResult::de).toList(),
                historico.totalItens(),
                historico.pagina(),
                historico.tamanho()
        );
    }

    private DocumentoProfissional carregarDocumento(UUID documentoId) {
        DocumentoProfissional documento = carregarDocumentoProfissionalPorIdPort.carregarDocumentoPorId(documentoId)
                .orElseThrow(() -> new BusinessException(
                        "DOCUMENTO_NAO_ENCONTRADO",
                        "Documento profissional nao encontrado."
                ));
        tenantAccessService.validarAcessoEmpresa(documento.empresaId());
        return documento;
    }

    private void validarDocumentoAlteravel(DocumentoProfissional documento) {
        if (documento.status() == StatusDocumentoProfissional.CANCELADO || !documento.ativo()) {
            throw new BusinessException(
                    "DOCUMENTO_NAO_ALTERAVEL",
                    "Documento profissional cancelado nao pode ser alterado."
            );
        }
    }

    private UUID usuarioAtualId() {
        return TenantContextHolder.contextoAtual()
                .map(TenantContext::usuarioId)
                .orElse(null);
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_DOCUMENTOS);
    }
}
