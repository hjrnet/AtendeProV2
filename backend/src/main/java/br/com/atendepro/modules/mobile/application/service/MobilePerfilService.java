package br.com.atendepro.modules.mobile.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorIdPort;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;
import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorIdPort;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.modules.mobile.application.port.in.ConsultarPerfilMobileUseCase;
import br.com.atendepro.modules.mobile.application.port.out.ListarClientesVinculadosMobilePort;
import br.com.atendepro.modules.mobile.application.result.PerfilMobileResult;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
public class MobilePerfilService implements ConsultarPerfilMobileUseCase {

    private final CarregarUsuarioPorIdPort carregarUsuarioPorIdPort;
    private final CarregarEmpresaPorIdPort carregarEmpresaPorIdPort;
    private final ListarClientesVinculadosMobilePort listarClientesVinculadosMobilePort;

    public MobilePerfilService(
            CarregarUsuarioPorIdPort carregarUsuarioPorIdPort,
            CarregarEmpresaPorIdPort carregarEmpresaPorIdPort,
            ListarClientesVinculadosMobilePort listarClientesVinculadosMobilePort
    ) {
        this.carregarUsuarioPorIdPort = carregarUsuarioPorIdPort;
        this.carregarEmpresaPorIdPort = carregarEmpresaPorIdPort;
        this.listarClientesVinculadosMobilePort = listarClientesVinculadosMobilePort;
    }

    @Override
    public PerfilMobileResult consultarPerfilMobile(UUID usuarioId) {
        if (usuarioId == null) {
            throw new AutenticacaoException("MOBILE_SESSAO_INVALIDA", "Sessao mobile invalida.");
        }

        UsuarioAutenticacao usuario = carregarUsuarioPorIdPort.carregarUsuarioPorId(usuarioId)
                .orElseThrow(() -> new AutenticacaoException(
                        "MOBILE_USUARIO_NAO_ENCONTRADO",
                        "Usuario autenticado nao foi encontrado."
                ));

        if (!usuario.ativo()) {
            throw new AutenticacaoException("MOBILE_USUARIO_INATIVO", "Usuario autenticado esta inativo.");
        }

        if (usuario.empresaId() == null) {
            throw new BusinessException(
                    "MOBILE_EMPRESA_OBRIGATORIA",
                    "App mobile requer usuario vinculado a uma empresa."
            );
        }

        EmpresaTenant empresa = carregarEmpresaPorIdPort.carregarEmpresaPorId(usuario.empresaId())
                .orElseThrow(() -> new BusinessException(
                        "MOBILE_EMPRESA_NAO_ENCONTRADA",
                        "Empresa do usuario mobile nao foi encontrada."
                ));

        if (!empresa.ativo()) {
            throw new BusinessException("MOBILE_EMPRESA_BLOQUEADA", "Empresa do usuario mobile esta bloqueada.");
        }

        return PerfilMobileResult.de(
                usuario,
                empresa,
                listarClientesVinculadosMobilePort.listarClientesVinculadosPorEmail(
                        usuario.empresaId(),
                        usuario.email().valor()
                )
        );
    }
}
