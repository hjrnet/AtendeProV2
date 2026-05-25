package br.com.atendepro.modules.busca.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.busca.application.result.ResultadoBuscaGlobalItemResult;

public interface BuscarGlobalPort {

    List<ResultadoBuscaGlobalItemResult> buscarGlobal(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limitePorTipo
    );
}
