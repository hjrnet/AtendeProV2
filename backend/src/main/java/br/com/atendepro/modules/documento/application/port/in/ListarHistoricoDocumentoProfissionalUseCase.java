package br.com.atendepro.modules.documento.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.HistoricoDocumentoProfissionalResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarHistoricoDocumentoProfissionalUseCase {

    ResultadoPaginado<HistoricoDocumentoProfissionalResult> listarHistorico(UUID documentoId, Paginacao paginacao);
}
