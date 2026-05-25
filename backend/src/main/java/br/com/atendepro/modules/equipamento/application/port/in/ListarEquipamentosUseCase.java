package br.com.atendepro.modules.equipamento.application.port.in;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.equipamento.application.result.EquipamentoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarEquipamentosUseCase {

    ResultadoPaginado<EquipamentoResult> listarEquipamentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate manutencaoAte
    );
}
