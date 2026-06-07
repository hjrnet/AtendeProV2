package br.com.atendepro.modules.suporte.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.suporte.application.result.RespostaAssistidaChamadoSuporteResult;

public interface GerarRespostaAssistidaChamadoSuporteUseCase {

    RespostaAssistidaChamadoSuporteResult gerarRespostaAssistida(UUID chamadoId);
}
