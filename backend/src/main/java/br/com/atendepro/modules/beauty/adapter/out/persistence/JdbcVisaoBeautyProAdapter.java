package br.com.atendepro.modules.beauty.adapter.out.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.beauty.application.port.out.CarregarVisaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;

@Repository
@Profile("!test")
public class JdbcVisaoBeautyProAdapter implements CarregarVisaoBeautyProPort {

    private static final String AREA_BEAUTY = "BEAUTY";

    private final JdbcTemplate jdbcTemplate;

    public JdbcVisaoBeautyProAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasBeautyProResult carregarVisaoBeautyPro(UUID empresaId, LocalDate hoje) {
        LocalDate daqui7Dias = hoje.plusDays(7);
        return new MetricasBeautyProResult(
                carregarNomeEmpresa(empresaId),
                contar("select count(*) from clientes_pacientes where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_BEAUTY),
                contarAgendaBeauty(empresaId, hoje, hoje),
                contarAgendaBeauty(empresaId, hoje, daqui7Dias),
                contar("select count(*) from servicos_procedimentos where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_BEAUTY),
                contar("select count(*) from estoque_produtos where empresa_id = ? and ativo = true", empresaId),
                contar("select count(*) from equipamentos where empresa_id = ? and ativo = true", empresaId),
                contarSimulacoesBeauty(empresaId, false),
                contarSimulacoesBeauty(empresaId, true),
                listarClientesRecentes(empresaId)
        );
    }

    private String carregarNomeEmpresa(UUID empresaId) {
        String nome = jdbcTemplate.queryForObject("select nome_fantasia from empresas where id = ?", String.class, empresaId);
        if (nome == null || nome.isBlank()) {
            return "Empresa selecionada";
        }
        return nome;
    }

    private long contarAgendaBeauty(UUID empresaId, LocalDate inicio, LocalDate fim) {
        return contar("""
                select count(*)
                from agenda_compromissos agenda
                left join clientes_pacientes cliente on cliente.id = agenda.cliente_paciente_id
                where agenda.empresa_id = ?
                  and agenda.status <> 'CANCELADO'
                  and agenda.inicio::date between ? and ?
                  and (cliente.area = ? or agenda.profissional_nome ilike '%estetic%')
                """, empresaId, inicio, fim, AREA_BEAUTY);
    }

    private long contarSimulacoesBeauty(UUID empresaId, boolean somenteAlertas) {
        String filtroAlerta = somenteAlertas ? " and simulacao.status_margem <> 'SAUDAVEL'" : "";
        return contar("""
                select count(*)
                from precificacao_simulacoes simulacao
                left join servicos_procedimentos servico on servico.id = simulacao.servico_procedimento_id
                where simulacao.empresa_id = ?
                  and simulacao.ativo = true
                  and (
                    servico.area = ?
                    or simulacao.nome_procedimento ilike '%pele%'
                    or simulacao.nome_procedimento ilike '%massagem%'
                    or simulacao.nome_procedimento ilike '%peeling%'
                    or simulacao.nome_procedimento ilike '%estet%'
                  )
                """ + filtroAlerta, empresaId, AREA_BEAUTY);
    }

    private List<ClienteBeautyResumoResult> listarClientesRecentes(UUID empresaId) {
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                order by atualizado_em desc
                limit 5
                """, this::mapearCliente, empresaId, AREA_BEAUTY);
    }

    private ClienteBeautyResumoResult mapearCliente(ResultSet rs, int rowNum) throws SQLException {
        return new ClienteBeautyResumoResult(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                rs.getString("telefone"),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private long contar(String sql, Object... parametros) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class, parametros);
        return total == null ? 0 : total;
    }
}
