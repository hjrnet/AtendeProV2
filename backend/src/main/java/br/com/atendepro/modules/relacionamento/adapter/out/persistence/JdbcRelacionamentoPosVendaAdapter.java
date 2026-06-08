package br.com.atendepro.modules.relacionamento.adapter.out.persistence;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.relacionamento.application.command.CriarTarefaRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarContatoRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarPesquisaNpsRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.port.out.DadosPosVenda;
import br.com.atendepro.modules.relacionamento.application.port.out.RelacionamentoPosVendaPort;
import br.com.atendepro.modules.relacionamento.domain.model.CanalContatoRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.StatusTarefaRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.TipoTarefaRelacionamento;

@Repository
@Profile("!test")
public class JdbcRelacionamentoPosVendaAdapter implements RelacionamentoPosVendaPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRelacionamentoPosVendaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DadosPosVenda carregarDadosPosVenda(UUID empresaId, AreaCliente area, String busca, LocalDate hoje) {
        return new DadosPosVenda(
                carregarClientes(empresaId, area, busca),
                carregarTarefas(empresaId, area),
                carregarContatosRecentes(empresaId, area),
                carregarNpsRecentes(empresaId, area)
        );
    }

    @Override
    public DadosPosVenda.Contato salvarContato(RegistrarContatoRelacionamentoCommand command, Instant criadoEm) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                """
                insert into relacionamento_contatos (
                    id, empresa_id, cliente_id, area, canal, template_codigo, mensagem, observacoes, criado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                command.empresaId(),
                command.clienteId(),
                command.area().name(),
                command.canal().name(),
                command.templateCodigo(),
                command.mensagem(),
                command.observacoes(),
                Timestamp.from(criadoEm)
        );
        return new DadosPosVenda.Contato(
                id,
                command.empresaId(),
                command.clienteId(),
                carregarNomeCliente(command.empresaId(), command.clienteId()),
                command.area(),
                command.canal(),
                command.templateCodigo(),
                command.mensagem(),
                command.observacoes(),
                criadoEm
        );
    }

    @Override
    public DadosPosVenda.PesquisaNps salvarPesquisaNps(RegistrarPesquisaNpsRelacionamentoCommand command, Instant criadoEm) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                """
                insert into relacionamento_nps (
                    id, empresa_id, cliente_id, area, nota, comentario, origem, criado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                command.empresaId(),
                command.clienteId(),
                command.area().name(),
                command.nota(),
                command.comentario(),
                command.origem(),
                Timestamp.from(criadoEm)
        );
        return new DadosPosVenda.PesquisaNps(
                id,
                command.empresaId(),
                command.clienteId(),
                carregarNomeCliente(command.empresaId(), command.clienteId()),
                command.area(),
                command.nota(),
                command.comentario(),
                command.origem(),
                criadoEm
        );
    }

    @Override
    public DadosPosVenda.Tarefa salvarTarefa(CriarTarefaRelacionamentoCommand command, Instant criadoEm) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                """
                insert into relacionamento_tarefas (
                    id, empresa_id, cliente_id, area, tipo, titulo, descricao, data_recomendada,
                    status, origem, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                command.empresaId(),
                command.clienteId(),
                command.area().name(),
                command.tipo().name(),
                command.titulo(),
                command.descricao(),
                command.dataRecomendada() == null ? null : Date.valueOf(command.dataRecomendada()),
                StatusTarefaRelacionamento.PENDENTE.name(),
                command.origem(),
                Timestamp.from(criadoEm),
                Timestamp.from(criadoEm)
        );
        return new DadosPosVenda.Tarefa(
                id,
                command.empresaId(),
                command.clienteId(),
                carregarNomeCliente(command.empresaId(), command.clienteId()),
                command.area(),
                command.tipo(),
                command.titulo(),
                command.descricao(),
                command.dataRecomendada(),
                StatusTarefaRelacionamento.PENDENTE,
                command.origem(),
                criadoEm,
                criadoEm
        );
    }

    @Override
    public Optional<DadosPosVenda.Tarefa> concluirTarefa(UUID empresaId, UUID tarefaId, Instant atualizadoEm) {
        int alterados = jdbcTemplate.update(
                """
                update relacionamento_tarefas
                set status = 'CONCLUIDA', atualizado_em = ?
                where empresa_id = ? and id = ?
                """,
                Timestamp.from(atualizadoEm),
                empresaId,
                tarefaId
        );
        if (alterados == 0) {
            return Optional.empty();
        }
        return carregarTarefaPorId(empresaId, tarefaId);
    }

    private ArrayList<DadosPosVenda.Cliente> carregarClientes(UUID empresaId, AreaCliente area, String busca) {
        var parametros = new ArrayList<>();
        parametros.add(empresaId);
        StringBuilder filtro = new StringBuilder("where c.empresa_id = ? and c.ativo = true");
        aplicarFiltroArea(filtro, parametros, area, "c.area");
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(c.nome) like ? or lower(coalesce(c.email, '')) like ? or lower(coalesce(c.telefone, '')) like ?)");
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        parametros.add(80);
        return new ArrayList<>(jdbcTemplate.query(
                """
                select
                    c.id,
                    c.empresa_id,
                    c.nome,
                    c.area,
                    c.email,
                    c.telefone,
                    c.data_nascimento,
                    c.ativo,
                    c.atualizado_em,
                    max(a.inicio) filter (where a.status = 'REALIZADO') as ultima_consulta_em,
                    min(a.inicio) filter (where a.status in ('AGENDADO', 'CONFIRMADO') and a.inicio >= now()) as proxima_consulta_em,
                    count(distinct a.id) filter (where a.status = 'CANCELADO' and a.inicio >= now() - interval '90 days') as faltas_recentes,
                    max(rc.criado_em) as ultimo_contato_em,
                    (array_agg(nps.nota order by nps.criado_em desc) filter (where nps.id is not null))[1] as ultima_nota_nps,
                    count(distinct bp.id) filter (where bp.status = 'ATIVO') as protocolos_ativos,
                    count(distinct np.id) filter (where np.status = 'ATIVO') as planos_ativos
                from clientes_pacientes c
                left join agenda_compromissos a on a.empresa_id = c.empresa_id and a.cliente_paciente_id = c.id
                left join relacionamento_contatos rc on rc.empresa_id = c.empresa_id and rc.cliente_id = c.id
                left join relacionamento_nps nps on nps.empresa_id = c.empresa_id and nps.cliente_id = c.id
                left join beauty_protocolos bp on bp.empresa_id = c.empresa_id and bp.cliente_id = c.id
                left join nutri_planos_alimentares np on np.empresa_id = c.empresa_id and np.paciente_id = c.id
                %s
                group by c.id, c.empresa_id, c.nome, c.area, c.email, c.telefone, c.data_nascimento, c.ativo, c.atualizado_em
                order by coalesce(max(a.inicio), c.atualizado_em) desc
                limit ?
                """.formatted(filtro),
                (rs, rowNum) -> new DadosPosVenda.Cliente(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("nome"),
                        AreaCliente.deCodigo(rs.getString("area")),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        dataLocal(rs.getDate("data_nascimento")),
                        instant(rs.getTimestamp("ultima_consulta_em")),
                        instant(rs.getTimestamp("proxima_consulta_em")),
                        instant(rs.getTimestamp("ultimo_contato_em")),
                        numero(rs.getObject("faltas_recentes")),
                        numeroOpcional(rs.getObject("ultima_nota_nps")),
                        numero(rs.getObject("protocolos_ativos")),
                        numero(rs.getObject("planos_ativos")),
                        rs.getBoolean("ativo"),
                        instant(rs.getTimestamp("atualizado_em"))
                ),
                parametros.toArray()
        ));
    }

    private ArrayList<DadosPosVenda.Tarefa> carregarTarefas(UUID empresaId, AreaCliente area) {
        var parametros = new ArrayList<>();
        parametros.add(empresaId);
        StringBuilder filtro = new StringBuilder("where rt.empresa_id = ?");
        aplicarFiltroArea(filtro, parametros, area, "rt.area");
        parametros.add(30);
        return new ArrayList<>(jdbcTemplate.query(
                """
                select rt.id, rt.empresa_id, rt.cliente_id, c.nome as cliente_nome, rt.area, rt.tipo, rt.titulo,
                       rt.descricao, rt.data_recomendada, rt.status, rt.origem, rt.criado_em, rt.atualizado_em
                from relacionamento_tarefas rt
                join clientes_pacientes c on c.id = rt.cliente_id and c.empresa_id = rt.empresa_id
                %s
                order by case when rt.status = 'PENDENTE' then 0 else 1 end, rt.data_recomendada asc nulls last, rt.atualizado_em desc
                limit ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearTarefa(rs),
                parametros.toArray()
        ));
    }

    private ArrayList<DadosPosVenda.Contato> carregarContatosRecentes(UUID empresaId, AreaCliente area) {
        var parametros = new ArrayList<>();
        parametros.add(empresaId);
        StringBuilder filtro = new StringBuilder("where rc.empresa_id = ?");
        aplicarFiltroArea(filtro, parametros, area, "rc.area");
        parametros.add(20);
        return new ArrayList<>(jdbcTemplate.query(
                """
                select rc.id, rc.empresa_id, rc.cliente_id, c.nome as cliente_nome, rc.area, rc.canal,
                       rc.template_codigo, rc.mensagem, rc.observacoes, rc.criado_em
                from relacionamento_contatos rc
                join clientes_pacientes c on c.id = rc.cliente_id and c.empresa_id = rc.empresa_id
                %s
                order by rc.criado_em desc
                limit ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearContato(rs),
                parametros.toArray()
        ));
    }

    private ArrayList<DadosPosVenda.PesquisaNps> carregarNpsRecentes(UUID empresaId, AreaCliente area) {
        var parametros = new ArrayList<>();
        parametros.add(empresaId);
        StringBuilder filtro = new StringBuilder("where rn.empresa_id = ?");
        aplicarFiltroArea(filtro, parametros, area, "rn.area");
        parametros.add(20);
        return new ArrayList<>(jdbcTemplate.query(
                """
                select rn.id, rn.empresa_id, rn.cliente_id, c.nome as cliente_nome, rn.area,
                       rn.nota, rn.comentario, rn.origem, rn.criado_em
                from relacionamento_nps rn
                join clientes_pacientes c on c.id = rn.cliente_id and c.empresa_id = rn.empresa_id
                %s
                order by rn.criado_em desc
                limit ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearNps(rs),
                parametros.toArray()
        ));
    }

    private Optional<DadosPosVenda.Tarefa> carregarTarefaPorId(UUID empresaId, UUID tarefaId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select rt.id, rt.empresa_id, rt.cliente_id, c.nome as cliente_nome, rt.area, rt.tipo, rt.titulo,
                           rt.descricao, rt.data_recomendada, rt.status, rt.origem, rt.criado_em, rt.atualizado_em
                    from relacionamento_tarefas rt
                    join clientes_pacientes c on c.id = rt.cliente_id and c.empresa_id = rt.empresa_id
                    where rt.empresa_id = ? and rt.id = ?
                    """,
                    (rs, rowNum) -> mapearTarefa(rs),
                    empresaId,
                    tarefaId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private String carregarNomeCliente(UUID empresaId, UUID clienteId) {
        return jdbcTemplate.queryForObject(
                "select nome from clientes_pacientes where empresa_id = ? and id = ?",
                String.class,
                empresaId,
                clienteId
        );
    }

    private void aplicarFiltroArea(StringBuilder filtro, ArrayList<Object> parametros, AreaCliente area, String coluna) {
        if (area != null) {
            filtro.append(" and ").append(coluna).append(" = ?");
            parametros.add(area.name());
            return;
        }
        filtro.append(" and ").append(coluna).append(" in ('NUTRI', 'BEAUTY')");
    }

    private DadosPosVenda.Tarefa mapearTarefa(ResultSet rs) throws SQLException {
        return new DadosPosVenda.Tarefa(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getString("cliente_nome"),
                AreaCliente.deCodigo(rs.getString("area")),
                TipoTarefaRelacionamento.valueOf(rs.getString("tipo")),
                rs.getString("titulo"),
                rs.getString("descricao"),
                dataLocal(rs.getDate("data_recomendada")),
                StatusTarefaRelacionamento.valueOf(rs.getString("status")),
                rs.getString("origem"),
                instant(rs.getTimestamp("criado_em")),
                instant(rs.getTimestamp("atualizado_em"))
        );
    }

    private DadosPosVenda.Contato mapearContato(ResultSet rs) throws SQLException {
        return new DadosPosVenda.Contato(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getString("cliente_nome"),
                AreaCliente.deCodigo(rs.getString("area")),
                CanalContatoRelacionamento.valueOf(rs.getString("canal")),
                rs.getString("template_codigo"),
                rs.getString("mensagem"),
                rs.getString("observacoes"),
                instant(rs.getTimestamp("criado_em"))
        );
    }

    private DadosPosVenda.PesquisaNps mapearNps(ResultSet rs) throws SQLException {
        return new DadosPosVenda.PesquisaNps(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getString("cliente_nome"),
                AreaCliente.deCodigo(rs.getString("area")),
                rs.getInt("nota"),
                rs.getString("comentario"),
                rs.getString("origem"),
                instant(rs.getTimestamp("criado_em"))
        );
    }

    private Instant instant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private LocalDate dataLocal(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private int numero(Object valor) {
        return valor == null ? 0 : ((Number) valor).intValue();
    }

    private Integer numeroOpcional(Object valor) {
        return valor == null ? null : ((Number) valor).intValue();
    }
}
