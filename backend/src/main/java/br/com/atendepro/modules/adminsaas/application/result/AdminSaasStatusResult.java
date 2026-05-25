package br.com.atendepro.modules.adminsaas.application.result;

import java.util.List;

public record AdminSaasStatusResult(
        String produto,
        String release,
        String statusOperacional,
        List<String> capacidades
) {

    public AdminSaasStatusResult {
        capacidades = List.copyOf(capacidades);
    }
}
