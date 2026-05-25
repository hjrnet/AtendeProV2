package br.com.atendepro.modules.precificacao.adapter.out.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.precificacao.application.port.out.AtualizarSimulacaoPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.CarregarSimulacaoPrecificacaoPorIdPort;
import br.com.atendepro.modules.precificacao.application.port.out.ListarSimulacoesPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.SalvarSimulacaoPrecificacaoPort;
import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcSimulacaoPrecificacaoAdapter implements
        SalvarSimulacaoPrecificacaoPort,
        AtualizarSimulacaoPrecificacaoPort,
        CarregarSimulacaoPrecificacaoPorIdPort,
        ListarSimulacoesPrecificacaoPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSimulacaoPrecificacaoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarSimulacao(SimulacaoPrecificacao simulacao) {
        jdbcTemplate.update(
                """
                insert into precificacao_simulacoes (
                    id, empresa_id, servico_procedimento_id, nome_procedimento, duracao_minutos,
                    custo_insumos, custo_sala_por_hora, valor_hora_profissional, custo_deslocamento,
                    custo_alimentacao, taxas, margem_desejada_percentual, preco_venda, custo_total,
                    preco_minimo, preco_recomendado, lucro_estimado, margem_real_percentual,
                    status_margem, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                parametros(simulacao)
        );
    }

    @Override
    public void atualizarSimulacao(SimulacaoPrecificacao simulacao) {
        jdbcTemplate.update(
                """
                update precificacao_simulacoes
                set nome_procedimento = ?,
                    duracao_minutos = ?,
                    custo_insumos = ?,
                    custo_sala_por_hora = ?,
                    valor_hora_profissional = ?,
                    custo_deslocamento = ?,
                    custo_alimentacao = ?,
                    taxas = ?,
                    margem_desejada_percentual = ?,
                    preco_venda = ?,
                    custo_total = ?,
                    preco_minimo = ?,
                    preco_recomendado = ?,
                    lucro_estimado = ?,
                    margem_real_percentual = ?,
                    status_margem = ?,
                    ativo = ?,
                    atualizado_em = ?
                where id = ?
                """,
                simulacao.nomeProcedimento(),
                simulacao.duracaoMinutos(),
                simulacao.custoInsumos(),
                simulacao.custoSalaPorHora(),
                simulacao.valorHoraProfissional(),
                simulacao.custoDeslocamento(),
                simulacao.custoAlimentacao(),
                simulacao.taxas(),
                simulacao.margemDesejadaPercentual(),
                simulacao.precoVenda(),
                simulacao.custoTotal(),
                simulacao.precoMinimo(),
                simulacao.precoRecomendado(),
                simulacao.lucroEstimado(),
                simulacao.margemRealPercentual(),
                simulacao.statusMargem().name(),
                simulacao.ativo(),
                Timestamp.from(simulacao.atualizadoEm()),
                simulacao.id()
        );
    }

    @Override
    public Optional<SimulacaoPrecificacao> carregarSimulacaoPorId(UUID simulacaoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select *
                    from precificacao_simulacoes
                    where id = ?
                    """,
                    this::mapearSimulacao,
                    simulacaoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<SimulacaoPrecificacao> listarSimulacoes(UUID empresaId, Paginacao paginacao, String busca) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from precificacao_simulacoes " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var simulacoes = jdbcTemplate.query(
                """
                select *
                from precificacao_simulacoes
                %s
                order by atualizado_em desc
                limit ? offset ?
                """.formatted(filtro),
                this::mapearSimulacao,
                parametros.toArray()
        );
        return new ResultadoPaginado<>(simulacoes, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(UUID empresaId, String busca, ArrayList<Object> parametros) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            filtro.append(" and lower(nome_procedimento) like ?");
            parametros.add("%" + busca.trim().toLowerCase() + "%");
        }
        return filtro.toString();
    }

    private Object[] parametros(SimulacaoPrecificacao simulacao) {
        return new Object[] {
                simulacao.id(),
                simulacao.empresaId(),
                simulacao.servicoProcedimentoId(),
                simulacao.nomeProcedimento(),
                simulacao.duracaoMinutos(),
                simulacao.custoInsumos(),
                simulacao.custoSalaPorHora(),
                simulacao.valorHoraProfissional(),
                simulacao.custoDeslocamento(),
                simulacao.custoAlimentacao(),
                simulacao.taxas(),
                simulacao.margemDesejadaPercentual(),
                simulacao.precoVenda(),
                simulacao.custoTotal(),
                simulacao.precoMinimo(),
                simulacao.precoRecomendado(),
                simulacao.lucroEstimado(),
                simulacao.margemRealPercentual(),
                simulacao.statusMargem().name(),
                simulacao.ativo(),
                Timestamp.from(simulacao.criadoEm()),
                Timestamp.from(simulacao.atualizadoEm())
        };
    }

    private SimulacaoPrecificacao mapearSimulacao(ResultSet rs, int rowNum) throws SQLException {
        return new SimulacaoPrecificacao(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("servico_procedimento_id", UUID.class),
                rs.getString("nome_procedimento"),
                rs.getInt("duracao_minutos"),
                rs.getBigDecimal("custo_insumos"),
                rs.getBigDecimal("custo_sala_por_hora"),
                rs.getBigDecimal("valor_hora_profissional"),
                rs.getBigDecimal("custo_deslocamento"),
                rs.getBigDecimal("custo_alimentacao"),
                rs.getBigDecimal("taxas"),
                rs.getBigDecimal("margem_desejada_percentual"),
                rs.getBigDecimal("preco_venda"),
                rs.getBigDecimal("custo_total"),
                rs.getBigDecimal("preco_minimo"),
                rs.getBigDecimal("preco_recomendado"),
                rs.getBigDecimal("lucro_estimado"),
                rs.getBigDecimal("margem_real_percentual"),
                StatusMargemPrecificacao.valueOf(rs.getString("status_margem")),
                rs.getBoolean("ativo"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }
}
