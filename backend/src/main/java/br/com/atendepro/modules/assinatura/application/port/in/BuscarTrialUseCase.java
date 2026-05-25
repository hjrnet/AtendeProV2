package br.com.atendepro.modules.assinatura.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.result.TrialResult;

public interface BuscarTrialUseCase {

    Optional<TrialResult> buscarTrialPorId(UUID trialId);
}
