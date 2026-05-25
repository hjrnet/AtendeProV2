package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.CriarTermoConsentimentoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.TermoConsentimentoBeautyProResult;

public interface CriarTermoConsentimentoBeautyProUseCase {
    TermoConsentimentoBeautyProResult criarTermoConsentimento(CriarTermoConsentimentoBeautyProCommand command);
}
