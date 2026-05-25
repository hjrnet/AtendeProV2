package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.ConsultarIntegracoesOperacionaisBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.IntegracoesOperacionaisBeautyProResult;

public interface ConsultarIntegracoesOperacionaisBeautyProUseCase {
    IntegracoesOperacionaisBeautyProResult consultarIntegracoesOperacionais(ConsultarIntegracoesOperacionaisBeautyProCommand command);
}
