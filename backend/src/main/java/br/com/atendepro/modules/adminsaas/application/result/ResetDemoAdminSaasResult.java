package br.com.atendepro.modules.adminsaas.application.result;

import java.time.Instant;
import java.util.List;

import br.com.atendepro.modules.adminsaas.domain.model.PerfilDemoAdminSaas;

public record ResetDemoAdminSaasResult(
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

    public ResetDemoAdminSaasResult {
        etapas = List.copyOf(etapas);
        credenciais = List.copyOf(credenciais);
        avisos = List.copyOf(avisos);
    }
}
