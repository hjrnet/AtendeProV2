package br.com.atendepro.modules.precificacao.adapter.out.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.precificacao.application.port.out.CarregarMetricasDashboardPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.result.DistribuicaoStatusPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.MetricasDashboardPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.SimulacaoDashboardPrecificacaoResult;

@Repository
@Profile("!test")
public class JdbcDashboardPrecificacaoAdapter implements CarregarMetricasDashboardPrecificacaoPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDashboardPrecificacaoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasDashboardPrecificacaoResult carregarMetricasDashboardPrecificacao(UUID empresaId) {
        var resumo = jdbcTemplate.queryForObject(
                """
                select count(*) as total_simulacoes,
                       coalesce(avg(custo_total), 0) as custo_medio,
                       coalesce(avg(preco_recomendado), 0) as preco_medio_recomendado,
                       coalesce(avg(preco_venda), 0) as preco_medio_venda,
                       coalesce(avg(lucro_estimado), 0) as lucro_medio,
                       coalesce(avg(margem_real_percentual), 0) as margem_media_percentual,
                       coalesce(sum(case when status_margem = 'SAUDAVEL' then 1 else 0 end), 0) as simulacoes_saudaveis,
                       coalesce(sum(case when status_margem <> 'SAUDAVEL' then 1 else 0 end), 0) as simulacoes_com_alerta
                from precificacao_simulacoes
                where empresa_id = ?
                  and ativo = true
                """,
                this::mapearResumo,
                empresaId
        );
        var distribuicao = jdbcTemplate.query(
                """
                select status_margem, count(*) as total
                from precificacao_simulacoes
                where empresa_id = ?
                  and ativo = true
                group by status_margem
                order by total desc, status_margem
                """,
                (rs, rowNum) -> new DistribuicaoStatusPrecificacaoResult(rs.getString("status_margem"), rs.getLong("total")),
                empresaId
        );
        var recentes = jdbcTemplate.query(
                """
                select nome_procedimento, custo_total, preco_recomendado, preco_venda,
                       margem_real_percentual, atualizado_em
                from precificacao_simulacoes
                where empresa_id = ?
                  and ativo = true
                order by atualizado_em desc
                limit 8
                """,
                this::mapearSimulacaoRecente,
                empresaId
        );
        return new MetricasDashboardPrecificacaoResult(
                resumo.totalSimulacoes(),
                resumo.custoMedio(),
                resumo.precoMedioRecomendado(),
                resumo.precoMedioVenda(),
                resumo.lucroMedio(),
                resumo.margemMediaPercentual(),
                resumo.simulacoesSaudaveis(),
                resumo.simulacoesComAlerta(),
                distribuicao,
                recentes
        );
    }

    private ResumoDashboardPrecificacao mapearResumo(ResultSet rs, int rowNum) throws SQLException {
        return new ResumoDashboardPrecificacao(
                rs.getLong("total_simulacoes"),
                decimal(rs, "custo_medio"),
                decimal(rs, "preco_medio_recomendado"),
                decimal(rs, "preco_medio_venda"),
                decimal(rs, "lucro_medio"),
                decimal(rs, "margem_media_percentual"),
                rs.getLong("simulacoes_saudaveis"),
                rs.getLong("simulacoes_com_alerta")
        );
    }

    private SimulacaoDashboardPrecificacaoResult mapearSimulacaoRecente(ResultSet rs, int rowNum) throws SQLException {
        return new SimulacaoDashboardPrecificacaoResult(
                rs.getString("nome_procedimento"),
                decimal(rs, "custo_total"),
                decimal(rs, "preco_recomendado"),
                decimal(rs, "preco_venda"),
                decimal(rs, "margem_real_percentual"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private BigDecimal decimal(ResultSet rs, String coluna) throws SQLException {
        BigDecimal valor = rs.getBigDecimal(coluna);
        return valor == null ? BigDecimal.ZERO.setScale(2) : valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private record ResumoDashboardPrecificacao(
            long totalSimulacoes,
            BigDecimal custoMedio,
            BigDecimal precoMedioRecomendado,
            BigDecimal precoMedioVenda,
            BigDecimal lucroMedio,
            BigDecimal margemMediaPercentual,
            long simulacoesSaudaveis,
            long simulacoesComAlerta
    ) {
    }
}
