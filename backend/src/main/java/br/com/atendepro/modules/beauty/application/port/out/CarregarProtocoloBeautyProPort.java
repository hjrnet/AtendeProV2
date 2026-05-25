package br.com.atendepro.modules.beauty.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.ProtocoloBeautyPro;

public interface CarregarProtocoloBeautyProPort {
    Optional<ProtocoloBeautyPro> carregarProtocolo(UUID empresaId, UUID clienteId, UUID protocoloId);
}
