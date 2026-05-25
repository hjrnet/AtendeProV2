package br.com.atendepro.modules.auth.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface MarcarTokenRecuperacaoSenhaUtilizadoPort {

    void marcarTokenRecuperacaoSenhaUtilizado(UUID tokenId, Instant utilizadoEm);
}
