package br.com.atendepro.modules.assinatura.application.port.out;

import br.com.atendepro.modules.assinatura.domain.model.TrialAssinatura;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarTrialsPort {

    ResultadoPaginado<TrialAssinatura> listarTrials(Paginacao paginacao, TrialStatus status);
}
