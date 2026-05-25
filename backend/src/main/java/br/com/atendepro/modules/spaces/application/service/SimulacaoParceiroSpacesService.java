package br.com.atendepro.modules.spaces.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.spaces.application.command.SimularParceiroSpacesCommand;
import br.com.atendepro.modules.spaces.application.port.in.SimularParceiroSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.out.CarregarPacoteSublocacaoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.result.SimulacaoParceiroSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.SimulacaoParceiroSpaces;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class SimulacaoParceiroSpacesService implements SimularParceiroSpacesUseCase {

    private final CarregarPacoteSublocacaoSpacesPorIdPort carregarPacoteSublocacaoSpacesPorIdPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;

    public SimulacaoParceiroSpacesService(
            CarregarPacoteSublocacaoSpacesPorIdPort carregarPacoteSublocacaoSpacesPorIdPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService
    ) {
        this.carregarPacoteSublocacaoSpacesPorIdPort = carregarPacoteSublocacaoSpacesPorIdPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
    }

    @Override
    public SimulacaoParceiroSpacesResult simularParceiro(SimularParceiroSpacesCommand command) {
        validarPermissao();
        PacoteSublocacaoSpaces pacote = carregarPacoteSublocacaoSpacesPorIdPort.carregarPacotePorId(command.pacoteId())
                .orElseThrow(() -> new BusinessException("SPACES_PACOTE_NAO_ENCONTRADO", "Pacote de sublocacao nao encontrado."));
        tenantAccessService.validarAcessoEmpresa(pacote.empresaId());
        UUID empresaId = resolverEmpresaId(command.empresaId(), pacote.empresaId());
        SimulacaoParceiroSpaces simulacao = SimulacaoParceiroSpaces.calcular(
                pacote,
                command.quantidadePacotesMes(),
                command.atendimentosMes(),
                command.ticketMedio(),
                command.custosOperacionaisParceiro()
        );
        return SimulacaoParceiroSpacesResult.de(empresaId, simulacao);
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada, UUID empresaPacote) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada != null && !empresaIdSolicitada.equals(empresaPacote)) {
            throw new BusinessException("SPACES_PACOTE_EMPRESA_DIVERGENTE", "Pacote de sublocacao nao pertence a empresa informada.");
        }
        return empresaPacote;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_SPACES);
    }
}
