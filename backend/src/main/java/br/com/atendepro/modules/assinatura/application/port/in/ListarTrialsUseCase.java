package br.com.atendepro.modules.assinatura.application.port.in;

import br.com.atendepro.modules.assinatura.application.result.TrialResult;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarTrialsUseCase {

    ResultadoPaginado<TrialResult> listarTrials(Paginacao paginacao, TrialStatus status);
}
