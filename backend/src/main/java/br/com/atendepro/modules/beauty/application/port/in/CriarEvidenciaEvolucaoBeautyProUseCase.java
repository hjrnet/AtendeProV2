package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.CriarEvidenciaEvolucaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.EvidenciaEvolucaoBeautyProResult;

public interface CriarEvidenciaEvolucaoBeautyProUseCase {
    EvidenciaEvolucaoBeautyProResult criarEvidenciaEvolucao(CriarEvidenciaEvolucaoBeautyProCommand command);
}
