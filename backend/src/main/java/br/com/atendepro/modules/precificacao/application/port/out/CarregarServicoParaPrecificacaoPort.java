package br.com.atendepro.modules.precificacao.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.ServicoPrecificacaoResult;

public interface CarregarServicoParaPrecificacaoPort {

    Optional<ServicoPrecificacaoResult> carregarServicoParaPrecificacao(UUID empresaId, UUID servicoProcedimentoId);
}
