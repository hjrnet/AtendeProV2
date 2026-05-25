package br.com.atendepro.modules.spaces.application.service;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.spaces.application.port.in.ConsultarSpacesUseCase;
import br.com.atendepro.modules.spaces.application.result.SpacesStatusResult;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

@Service
@Profile("!test")
public class SpacesService implements ConsultarSpacesUseCase {

    private final PermissaoAcessoService permissaoAcessoService;

    public SpacesService(PermissaoAcessoService permissaoAcessoService) {
        this.permissaoAcessoService = permissaoAcessoService;
    }

    @Override
    public SpacesStatusResult consultarStatus() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_SPACES);
        return new SpacesStatusResult(
                "AtendePro Spaces",
                "R5",
                "SPACES_OPERACIONAL",
                List.of(TipoRecursoSpaces.SALA, TipoRecursoSpaces.CADEIRA, TipoRecursoSpaces.CABINE, TipoRecursoSpaces.EQUIPAMENTO),
                List.of("salas", "cadeiras", "cabines", "equipamentos", "sublocacao")
        );
    }
}
