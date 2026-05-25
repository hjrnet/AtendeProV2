package br.com.atendepro.modules.beauty.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.SessaoProtocoloBeautyPro;

public interface ListarSessoesProtocoloBeautyProPort {
    List<SessaoProtocoloBeautyPro> listarSessoes(UUID empresaId, UUID protocoloId);
}
