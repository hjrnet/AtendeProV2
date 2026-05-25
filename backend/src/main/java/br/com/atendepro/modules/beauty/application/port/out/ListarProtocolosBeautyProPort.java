package br.com.atendepro.modules.beauty.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.ProtocoloBeautyPro;

public interface ListarProtocolosBeautyProPort {
    List<ProtocoloBeautyPro> listarProtocolos(UUID empresaId, UUID clienteId);
}
