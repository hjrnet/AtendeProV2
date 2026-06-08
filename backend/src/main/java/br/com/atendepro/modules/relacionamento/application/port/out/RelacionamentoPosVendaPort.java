package br.com.atendepro.modules.relacionamento.application.port.out;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.relacionamento.application.command.CriarTarefaRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarContatoRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarPesquisaNpsRelacionamentoCommand;

public interface RelacionamentoPosVendaPort {

    DadosPosVenda carregarDadosPosVenda(UUID empresaId, AreaCliente area, String busca, LocalDate hoje);

    DadosPosVenda.Contato salvarContato(RegistrarContatoRelacionamentoCommand command, Instant criadoEm);

    DadosPosVenda.PesquisaNps salvarPesquisaNps(RegistrarPesquisaNpsRelacionamentoCommand command, Instant criadoEm);

    DadosPosVenda.Tarefa salvarTarefa(CriarTarefaRelacionamentoCommand command, Instant criadoEm);

    Optional<DadosPosVenda.Tarefa> concluirTarefa(UUID empresaId, UUID tarefaId, Instant atualizadoEm);
}
