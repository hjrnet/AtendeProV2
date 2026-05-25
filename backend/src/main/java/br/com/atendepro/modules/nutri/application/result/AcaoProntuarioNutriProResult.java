package br.com.atendepro.modules.nutri.application.result;

import br.com.atendepro.modules.nutri.domain.model.StatusAcaoNutriPro;

public record AcaoProntuarioNutriProResult(
        String codigo,
        String titulo,
        String descricao,
        StatusAcaoNutriPro status,
        boolean destaque
) {
}
