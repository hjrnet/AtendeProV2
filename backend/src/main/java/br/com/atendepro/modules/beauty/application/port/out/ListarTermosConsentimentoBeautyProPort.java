package br.com.atendepro.modules.beauty.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.TermoConsentimentoBeautyPro;

public interface ListarTermosConsentimentoBeautyProPort {
    List<TermoConsentimentoBeautyPro> listarTermosConsentimento(UUID empresaId, UUID clienteId);
}
