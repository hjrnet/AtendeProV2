package br.com.atendepro.modules.suporte.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.suporte.application.result.ChamadoSuporteResult;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarChamadosSuporteUseCase {

    ResultadoPaginado<ChamadoSuporteResult> listarChamados(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            StatusChamadoSuporte status,
            PrioridadeChamadoSuporte prioridade
    );
}
