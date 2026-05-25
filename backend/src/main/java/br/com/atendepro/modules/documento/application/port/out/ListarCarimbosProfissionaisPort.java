package br.com.atendepro.modules.documento.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarCarimbosProfissionaisPort {

    ResultadoPaginado<CarimboProfissional> listarCarimbos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            ConselhoProfissional conselho,
            String uf,
            UUID profissionalId,
            Boolean ativo
    );
}
