package br.com.atendepro.modules.documento.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.CarimboProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarCarimbosProfissionaisUseCase {

    ResultadoPaginado<CarimboProfissionalResult> listarCarimbos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            ConselhoProfissional conselho,
            String uf,
            UUID profissionalId,
            Boolean ativo
    );
}
