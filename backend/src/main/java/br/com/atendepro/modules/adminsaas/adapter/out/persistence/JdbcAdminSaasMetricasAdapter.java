package br.com.atendepro.modules.adminsaas.adapter.out.persistence;

import java.math.BigDecimal;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.adminsaas.application.port.out.CarregarMetricasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.MetricasAdminSaasResult;

@Repository
@Profile("!test")
public class JdbcAdminSaasMetricasAdapter implements CarregarMetricasAdminSaasPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAdminSaasMetricasAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasAdminSaasResult carregarMetricas() {
        return new MetricasAdminSaasResult(
                consultarMrrAtivo(),
                contarEmpresasPorStatus(true),
                contarEmpresasPorStatus(false),
                contarTrialsAtivos(),
                0
        );
    }

    private BigDecimal consultarMrrAtivo() {
        BigDecimal mrr = jdbcTemplate.queryForObject(
                """
                select coalesce(sum(p.valor_mensal), 0)
                from assinaturas a
                join planos p on p.id = a.plano_id
                where a.status = 'ATIVA'
                """,
                BigDecimal.class
        );
        return mrr == null ? BigDecimal.ZERO : mrr;
    }

    private long contarEmpresasPorStatus(boolean ativo) {
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from empresas where ativo = ?",
                Long.class,
                ativo
        );
        return total == null ? 0 : total;
    }

    private long contarTrialsAtivos() {
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from assinatura_trials where status = 'ATIVO'",
                Long.class
        );
        return total == null ? 0 : total;
    }
}
