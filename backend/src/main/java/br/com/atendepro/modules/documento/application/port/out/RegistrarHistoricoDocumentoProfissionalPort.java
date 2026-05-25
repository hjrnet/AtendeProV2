package br.com.atendepro.modules.documento.application.port.out;

import br.com.atendepro.modules.documento.domain.model.HistoricoDocumentoProfissional;

public interface RegistrarHistoricoDocumentoProfissionalPort {

    void registrarHistorico(HistoricoDocumentoProfissional historico);
}
