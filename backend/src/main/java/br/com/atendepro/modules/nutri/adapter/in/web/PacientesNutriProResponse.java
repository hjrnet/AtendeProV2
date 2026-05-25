package br.com.atendepro.modules.nutri.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;

public record PacientesNutriProResponse(List<VisaoNutriProResponse.PacienteNutriResumoResponse> itens) {

    public static PacientesNutriProResponse de(List<PacienteNutriResumoResult> pacientes) {
        return new PacientesNutriProResponse(
                pacientes.stream().map(VisaoNutriProResponse.PacienteNutriResumoResponse::de).toList()
        );
    }
}
