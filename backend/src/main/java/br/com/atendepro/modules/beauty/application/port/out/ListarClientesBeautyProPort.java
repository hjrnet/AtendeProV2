package br.com.atendepro.modules.beauty.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;

public interface ListarClientesBeautyProPort {
    List<ClienteBeautyResumoResult> listarClientesBeautyPro(UUID empresaId, String busca);
}
