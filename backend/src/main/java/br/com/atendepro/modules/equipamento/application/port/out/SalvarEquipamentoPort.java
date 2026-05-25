package br.com.atendepro.modules.equipamento.application.port.out;

import br.com.atendepro.modules.equipamento.domain.model.Equipamento;

public interface SalvarEquipamentoPort {

    void salvarEquipamento(Equipamento equipamento);
}
