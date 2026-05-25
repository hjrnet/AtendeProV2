package br.com.atendepro.modules.assinatura.application.port.in;

import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarAssinaturasUseCase {

    ResultadoPaginado<AssinaturaResult> listarAssinaturas(Paginacao paginacao, AssinaturaStatus status);
}
