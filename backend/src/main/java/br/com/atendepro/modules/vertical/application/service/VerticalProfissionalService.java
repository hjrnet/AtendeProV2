package br.com.atendepro.modules.vertical.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.vertical.application.port.in.DetalharVerticalProfissionalUseCase;
import br.com.atendepro.modules.vertical.application.port.in.ListarVerticaisProfissionaisUseCase;
import br.com.atendepro.modules.vertical.application.port.out.CarregarCatalogoVerticaisProfissionaisPort;
import br.com.atendepro.modules.vertical.application.result.VerticalProfissionalResult;
import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;

@Service
public class VerticalProfissionalService implements
        ListarVerticaisProfissionaisUseCase,
        DetalharVerticalProfissionalUseCase {

    private final CarregarCatalogoVerticaisProfissionaisPort carregarCatalogoVerticaisProfissionaisPort;
    private final PermissaoAcessoService permissaoAcessoService;

    public VerticalProfissionalService(
            CarregarCatalogoVerticaisProfissionaisPort carregarCatalogoVerticaisProfissionaisPort,
            PermissaoAcessoService permissaoAcessoService
    ) {
        this.carregarCatalogoVerticaisProfissionaisPort = carregarCatalogoVerticaisProfissionaisPort;
        this.permissaoAcessoService = permissaoAcessoService;
    }

    @Override
    public List<VerticalProfissionalResult> listarVerticais() {
        validarPermissao();
        return carregarCatalogoVerticaisProfissionaisPort.listarVerticais()
                .stream()
                .map(VerticalProfissionalResult::de)
                .toList();
    }

    @Override
    public Optional<VerticalProfissionalResult> detalharVertical(CodigoVerticalProfissional codigo) {
        validarPermissao();
        return carregarCatalogoVerticaisProfissionaisPort.carregarVertical(codigo)
                .map(VerticalProfissionalResult::de);
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_VERTICAIS);
    }
}
