package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.time.Instant;
import java.util.List;

import br.com.atendepro.modules.adminsaas.application.result.ResetDemoAdminSaasResult;
import br.com.atendepro.modules.adminsaas.domain.model.PerfilDemoAdminSaas;

public record ResetDemoAdminSaasResponse(
        PerfilDemoAdminSaas perfil,
        String perfilRotulo,
        String status,
        boolean executado,
        String ambiente,
        List<String> etapas,
        List<String> credenciais,
        List<String> avisos,
        Instant atualizadoEm
) {

    public static ResetDemoAdminSaasResponse de(ResetDemoAdminSaasResult result) {
        return new ResetDemoAdminSaasResponse(
                result.perfil(),
                result.perfilRotulo(),
                result.status(),
                result.executado(),
                result.ambiente(),
                result.etapas(),
                result.credenciais(),
                result.avisos(),
                result.atualizadoEm()
        );
    }
}
