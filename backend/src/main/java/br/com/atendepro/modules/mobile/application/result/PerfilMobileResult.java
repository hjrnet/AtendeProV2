package br.com.atendepro.modules.mobile.application.result;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;

public record PerfilMobileResult(
        UUID usuarioId,
        UUID empresaId,
        String nomeUsuario,
        String emailUsuario,
        Set<PerfilAcesso> perfis,
        Set<String> authorities,
        EmpresaMobileResult empresa,
        List<ClienteVinculadoMobileResult> clientesVinculados,
        boolean exigeVinculoCliente,
        String papelPrincipal
) {

    public PerfilMobileResult {
        perfis = Set.copyOf(perfis);
        authorities = Set.copyOf(authorities);
        clientesVinculados = List.copyOf(clientesVinculados);
    }

    public static PerfilMobileResult de(
            UsuarioAutenticacao usuario,
            EmpresaTenant empresa,
            List<ClienteVinculadoMobileResult> clientesVinculados
    ) {
        return new PerfilMobileResult(
                usuario.id(),
                usuario.empresaId(),
                usuario.nome(),
                usuario.email().valor(),
                usuario.perfis(),
                usuario.authorities(),
                EmpresaMobileResult.de(empresa),
                clientesVinculados,
                usuario.possuiPerfil(PerfilAcesso.CLIENTE),
                papelPrincipal(usuario)
        );
    }

    private static String papelPrincipal(UsuarioAutenticacao usuario) {
        if (usuario.possuiPerfil(PerfilAcesso.SUPER_ADMIN) || usuario.possuiPerfil(PerfilAcesso.SUPORTE)) {
            return "ADMIN_SAAS";
        }
        if (usuario.possuiPerfil(PerfilAcesso.EMPRESA_ADMIN)
                || usuario.possuiPerfil(PerfilAcesso.PROFISSIONAL)
                || usuario.possuiPerfil(PerfilAcesso.RECEPCIONISTA)) {
            return "PROFISSIONAL";
        }
        if (usuario.possuiPerfil(PerfilAcesso.CLIENTE)) {
            return "CLIENTE";
        }
        if (usuario.possuiPerfil(PerfilAcesso.ESTUDANTE)) {
            return "ESTUDANTE";
        }
        return "USUARIO";
    }
}
