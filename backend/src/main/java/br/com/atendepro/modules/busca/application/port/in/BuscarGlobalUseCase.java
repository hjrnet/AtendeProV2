package br.com.atendepro.modules.busca.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.busca.application.result.BuscaGlobalResult;

public interface BuscarGlobalUseCase {

    BuscaGlobalResult buscarGlobal(UUID empresaId, String busca, String categoria, String status, int limitePorTipo);
}
