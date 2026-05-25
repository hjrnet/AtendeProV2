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
                BigDecimal.ZERO,
                contarEmpresasPorStatus(true),
                contarEmpresasPorStatus(false),
                0,
                0
        );
    }

    private long contarEmpresasPorStatus(boolean ativo) {
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from empresas where ativo = ?",
                Long.class,
                ativo
        );
        return total == null ? 0 : total;
    }
}
