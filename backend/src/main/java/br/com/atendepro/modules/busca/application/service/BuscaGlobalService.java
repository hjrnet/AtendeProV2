package br.com.atendepro.modules.busca.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.busca.application.port.in.BuscarGlobalUseCase;
import br.com.atendepro.modules.busca.application.port.out.BuscarGlobalPort;
import br.com.atendepro.modules.busca.application.result.BuscaGlobalResult;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class BuscaGlobalService implements BuscarGlobalUseCase {

    private static final int LIMITE_MINIMO = 1;
    private static final int LIMITE_MAXIMO = 20;

    private final BuscarGlobalPort buscarGlobalPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;

    public BuscaGlobalService(
            BuscarGlobalPort buscarGlobalPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService
    ) {
        this.buscarGlobalPort = buscarGlobalPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
    }

    @Override
    public BuscaGlobalResult buscarGlobal(UUID empresaId, String busca, String categoria, String status, int limitePorTipo) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.VISUALIZAR_EMPRESA);
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        int limiteNormalizado = normalizarLimite(limitePorTipo);
        var itens = buscarGlobalPort.buscarGlobal(
                empresaResolvida,
                textoOpcional(busca),
                textoOpcional(categoria),
                textoOpcional(status),
                limiteNormalizado
        );
        return new BuscaGlobalResult(
                empresaResolvida,
                textoOpcional(busca),
                textoOpcional(categoria),
                textoOpcional(status),
                limiteNormalizado,
                itens.size(),
                itens
        );
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
            throw new BusinessException("BUSCA_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para realizar busca.");
        }
        return empresaIdSolicitada;
    }

    private int normalizarLimite(int limitePorTipo) {
        if (limitePorTipo < LIMITE_MINIMO) {
            return LIMITE_MINIMO;
        }
        return Math.min(limitePorTipo, LIMITE_MAXIMO);
    }

    private String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
