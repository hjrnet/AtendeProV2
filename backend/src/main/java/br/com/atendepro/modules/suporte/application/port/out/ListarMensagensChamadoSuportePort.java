package br.com.atendepro.modules.suporte.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;

public interface ListarMensagensChamadoSuportePort {

    List<MensagemChamadoSuporte> listarMensagens(UUID chamadoId);
}
