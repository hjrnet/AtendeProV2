package br.com.atendepro.modules.nutri.application.port.out;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.DadosProntuarioNutriProResult;

public interface CarregarProntuarioNutriProPort {

    Optional<DadosProntuarioNutriProResult> carregarProntuarioNutriPro(UUID empresaId, UUID pacienteId, LocalDate hoje);
}
