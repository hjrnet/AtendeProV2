package br.com.atendepro.modules.equipamento.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.equipamento.domain.model.Equipamento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarEquipamentosPort {

    ResultadoPaginado<Equipamento> listarEquipamentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate manutencaoAte
    );
}
