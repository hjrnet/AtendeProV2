package br.com.atendepro.modules.documento.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.HistoricoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarHistoricoDocumentoProfissionalPort {

    ResultadoPaginado<HistoricoDocumentoProfissional> listarHistorico(UUID documentoId, UUID empresaId, Paginacao paginacao);
}
