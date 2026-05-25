package br.com.atendepro.modules.beauty.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.IntegracoesOperacionaisBeautyProResult;

public interface CarregarIntegracoesOperacionaisBeautyProPort {
    IntegracoesOperacionaisBeautyProResult carregarIntegracoesOperacionais(UUID empresaId, LocalDate hoje);
}
