package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.adminsaas.application.result.AdminSaasStatusResult;

public record AdminSaasStatusResponse(
        String produto,
        String release,
        String statusOperacional,
        List<String> capacidades
) {

    public AdminSaasStatusResponse {
        capacidades = List.copyOf(capacidades);
    }

    static AdminSaasStatusResponse de(AdminSaasStatusResult result) {
        return new AdminSaasStatusResponse(
                result.produto(),
                result.release(),
                result.statusOperacional(),
                result.capacidades()
        );
    }
}
