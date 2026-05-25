package br.com.atendepro.modules.documento.application.service;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.documento.application.port.in.GerarPdfDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.out.CarregarCarimboProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.CarregarDocumentoProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.CarregarMarcaDaguaAcademicaPlanoPort;
import br.com.atendepro.modules.documento.application.port.out.GerarPdfDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalPdfResult;
import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class DocumentoProfissionalPdfService implements GerarPdfDocumentoProfissionalUseCase {

    private final CarregarDocumentoProfissionalPorIdPort carregarDocumentoProfissionalPorIdPort;
    private final CarregarCarimboProfissionalPorIdPort carregarCarimboProfissionalPorIdPort;
    private final CarregarMarcaDaguaAcademicaPlanoPort carregarMarcaDaguaAcademicaPlanoPort;
    private final GerarPdfDocumentoProfissionalPort gerarPdfDocumentoProfissionalPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;

    public DocumentoProfissionalPdfService(
            CarregarDocumentoProfissionalPorIdPort carregarDocumentoProfissionalPorIdPort,
            CarregarCarimboProfissionalPorIdPort carregarCarimboProfissionalPorIdPort,
            CarregarMarcaDaguaAcademicaPlanoPort carregarMarcaDaguaAcademicaPlanoPort,
            GerarPdfDocumentoProfissionalPort gerarPdfDocumentoProfissionalPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService
    ) {
        this.carregarDocumentoProfissionalPorIdPort = carregarDocumentoProfissionalPorIdPort;
        this.carregarCarimboProfissionalPorIdPort = carregarCarimboProfissionalPorIdPort;
        this.carregarMarcaDaguaAcademicaPlanoPort = carregarMarcaDaguaAcademicaPlanoPort;
        this.gerarPdfDocumentoProfissionalPort = gerarPdfDocumentoProfissionalPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
    }

    @Override
    public DocumentoProfissionalPdfResult gerarPdf(UUID documentoId, UUID carimboId) {
        validarPermissao();
        DocumentoProfissional documento = carregarDocumentoProfissionalPorIdPort.carregarDocumentoPorId(documentoId)
                .orElseThrow(() -> new BusinessException(
                        "DOCUMENTO_NAO_ENCONTRADO",
                        "Documento profissional nao encontrado."
                ));
        tenantAccessService.validarAcessoEmpresa(documento.empresaId());
        CarimboProfissional carimbo = carregarCarimbo(carimboId, documento.empresaId());
        String marcaDaguaAcademica = carregarMarcaDaguaAcademicaPlanoPort
                .carregarMarcaDaguaAcademica(documento.empresaId())
                .orElse(null);
        return gerarPdfDocumentoProfissionalPort.gerarPdf(documento, carimbo, marcaDaguaAcademica);
    }

    private CarimboProfissional carregarCarimbo(UUID carimboId, UUID empresaId) {
        if (carimboId == null) {
            return null;
        }
        CarimboProfissional carimbo = carregarCarimboProfissionalPorIdPort.carregarCarimboPorId(carimboId)
                .orElseThrow(() -> new BusinessException(
                        "CARIMBO_NAO_ENCONTRADO",
                        "Carimbo profissional nao encontrado."
                ));
        tenantAccessService.validarAcessoEmpresa(carimbo.empresaId());
        if (!carimbo.empresaId().equals(empresaId)) {
            throw new BusinessException(
                    "CARIMBO_EMPRESA_DIVERGENTE",
                    "Carimbo profissional nao pertence a empresa do documento."
            );
        }
        return carimbo;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_DOCUMENTOS);
    }
}
