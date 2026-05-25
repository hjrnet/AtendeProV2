package br.com.atendepro.modules.assinatura.application.port.out;

import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarAssinaturasPort {

    ResultadoPaginado<AssinaturaSaas> listarAssinaturas(Paginacao paginacao, AssinaturaStatus status);
}
