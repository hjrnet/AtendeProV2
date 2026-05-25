package br.com.atendepro.modules.nutri.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.MetricasNutriProResult;

public interface CarregarVisaoNutriProPort {

    MetricasNutriProResult carregarVisaoNutriPro(UUID empresaId, LocalDate hoje);
}
