package br.com.atendepro.modules.beauty.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.EvidenciaEvolucaoBeautyPro;

public interface ListarEvidenciasEvolucaoBeautyProPort {
    List<EvidenciaEvolucaoBeautyPro> listarEvidenciasEvolucao(UUID empresaId, UUID clienteId);
}
