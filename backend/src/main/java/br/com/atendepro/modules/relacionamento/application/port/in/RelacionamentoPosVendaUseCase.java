package br.com.atendepro.modules.relacionamento.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.relacionamento.application.command.CriarTarefaRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarContatoRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarPesquisaNpsRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.result.PainelPosVendaResult;

public interface RelacionamentoPosVendaUseCase {

    PainelPosVendaResult consultarPainel(UUID empresaId, AreaCliente area, String busca);

    PainelPosVendaResult.Contato registrarContato(RegistrarContatoRelacionamentoCommand command);

    PainelPosVendaResult.PesquisaNps registrarPesquisaNps(RegistrarPesquisaNpsRelacionamentoCommand command);

    PainelPosVendaResult.Tarefa criarTarefa(CriarTarefaRelacionamentoCommand command);

    Optional<PainelPosVendaResult.Tarefa> concluirTarefa(UUID empresaId, UUID tarefaId);
}
