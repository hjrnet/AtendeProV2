package br.com.atendepro.modules.adminsaas.adapter.out.persistence;

import java.math.BigDecimal;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.adminsaas.application.port.out.CarregarMetricasVendasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.MetricasVendasAdminSaasResult;
import br.com.atendepro.modules.adminsaas.application.result.PlanoVendidoAdminSaasResult;

@Repository
@Profile("!test")
public class JdbcAdminSaasVendasAdapter implements CarregarMetricasVendasAdminSaasPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAdminSaasVendasAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasVendasAdminSaasResult carregarMetricasVendas() {
        return new MetricasVendasAdminSaasResult(
                consultarMrrAtivo(),
                contar("select count(*) from assinatura_trials"),
                contar("select count(*) from assinatura_trials where status = 'CONVERTIDO'"),
                contar("select count(*) from assinaturas where status = 'ATIVA'"),
                contar("select count(*) from assinaturas where status = 'CANCELADA'"),
                carregarPlanosVendidos()
        );
    }

    private BigDecimal consultarMrrAtivo() {
        return consultarDecimal(
                """
                select coalesce(sum(p.valor_mensal), 0)
                from assinaturas a
                join planos p on p.id = a.plano_id
                where a.status = 'ATIVA'
                """
        );
    }

    private java.util.List<PlanoVendidoAdminSaasResult> carregarPlanosVendidos() {
        return jdbcTemplate.query(
                """
                select p.id,
                       p.codigo,
                       p.nome,
                       count(a.id) as total_assinaturas,
                       coalesce(sum(case when a.status = 'ATIVA' then 1 else 0 end), 0) as assinaturas_ativas,
                       coalesce(sum(case when a.status = 'ATIVA' then p.valor_mensal else 0 end), 0) as mrr
                from assinaturas a
                join planos p on p.id = a.plano_id
                group by p.id, p.codigo, p.nome
                order by total_assinaturas desc, mrr desc, p.nome asc
                """,
                (rs, rowNum) -> new PlanoVendidoAdminSaasResult(
                        rs.getObject("id", java.util.UUID.class),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getLong("total_assinaturas"),
                        rs.getLong("assinaturas_ativas"),
                        rs.getBigDecimal("mrr")
                )
        );
    }

    private long contar(String sql) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class);
        return total == null ? 0 : total;
    }

    private BigDecimal consultarDecimal(String sql) {
        BigDecimal valor = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
