package br.com.atendepro.modules.relacionamento.application.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.relacionamento.domain.model.CanalContatoRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.StatusTarefaRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.TipoTarefaRelacionamento;

public record PainelPosVendaResult(
        UUID empresaId,
        AreaCliente area,
        Metricas metricas,
        List<Cliente> clientes,
        List<Tarefa> tarefas,
        List<TemplateMensagem> templates,
        List<Contato> contatosRecentes,
        List<PesquisaNps> npsRecentes,
        List<SegmentoCampanha> segmentos,
        Instant atualizadoEm
) {
    public record Metricas(
            int clientesMonitorados,
            int retornosPendentes,
            int clientesInativos,
            int faltasRecentes,
            int clientesSemContato,
            int oportunidadesRecorrencia,
            double npsMedio,
            int detratores
    ) {
    }

    public record Cliente(
            UUID id,
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
            String statusAcompanhamento,
            String statusRotulo,
            LocalDate retornoRecomendadoEm,
            String motivoRetorno,
            String riscoAbandono,
            boolean aniversarioProximo,
            boolean oportunidadeRecorrencia
    ) {
    }

    public record Tarefa(
            UUID id,
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

    public record TemplateMensagem(
            String codigo,
            AreaCliente area,
            String titulo,
            String objetivo,
            String mensagem,
            List<String> variaveis
    ) {
    }

    public record Contato(
            UUID id,
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
            UUID clienteId,
            String clienteNome,
            AreaCliente area,
            int nota,
            String comentario,
            String origem,
            Instant criadoEm
    ) {
    }

    public record SegmentoCampanha(
            String codigo,
            String titulo,
            String descricao,
            int quantidadeClientes,
            String acaoRecomendada
    ) {
    }
}
