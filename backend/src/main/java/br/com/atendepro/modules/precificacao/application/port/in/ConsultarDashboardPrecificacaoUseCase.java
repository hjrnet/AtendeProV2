package br.com.atendepro.modules.precificacao.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.DashboardPrecificacaoResult;

public interface ConsultarDashboardPrecificacaoUseCase {

    DashboardPrecificacaoResult consultarDashboardPrecificacao(UUID empresaId);
}
