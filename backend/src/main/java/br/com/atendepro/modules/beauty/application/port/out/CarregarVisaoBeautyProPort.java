package br.com.atendepro.modules.beauty.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;

public interface CarregarVisaoBeautyProPort {

    MetricasBeautyProResult carregarVisaoBeautyPro(UUID empresaId, LocalDate hoje);
}
