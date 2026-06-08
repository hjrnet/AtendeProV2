package br.com.atendepro.modules.relacionamento.application.port.out;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.relacionamento.domain.model.CanalContatoRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.StatusTarefaRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.TipoTarefaRelacionamento;

public record DadosPosVenda(
        List<Cliente> clientes,
        List<Tarefa> tarefas,
        List<Contato> contatosRecentes,
        List<PesquisaNps> npsRecentes
) {
    public record Cliente(
            UUID id,
            UUID empresaId,
            String nome,
            AreaCliente area,
            String email,
            String telefone,
            LocalDate dataNascimento,
            Instant ultimaConsultaEm,
            Instant proximaConsultaEm,
            Instant ultimoContatoEm,
            int faltasRecentes,
            Integer ultimaNotaNps,
            int protocolosAtivos,
            int planosAtivos,
            boolean ativo,
            Instant atualizadoEm
    ) {
    }

    public record Tarefa(
            UUID id,
            UUID empresaId,
            UUID clienteId,
            String clienteNome,
            AreaCliente area,
            TipoTarefaRelacionamento tipo,
            String titulo,
            String descricao,
            LocalDate dataRecomendada,
            StatusTarefaRelacionamento status,
            String origem,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
    }

    public record Contato(
            UUID id,
            UUID empresaId,
            UUID clienteId,
            String clienteNome,
            AreaCliente area,
            CanalContatoRelacionamento canal,
            String templateCodigo,
            String mensagem,
            String observacoes,
            Instant criadoEm
    ) {
    }

    public record PesquisaNps(
            UUID id,
            UUID empresaId,
            UUID clienteId,
            String clienteNome,
            AreaCliente area,
            int nota,
            String comentario,
            String origem,
            Instant criadoEm
    ) {
    }
}
