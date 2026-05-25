package br.com.atendepro.modules.dashboard.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.dashboard.application.port.out.CarregarMetricasDashboardEmpresaPort;
import br.com.atendepro.modules.dashboard.application.result.MetricasDashboardEmpresaResult;

@Repository
@Profile("!test")
public class JdbcDashboardEmpresaAdapter implements CarregarMetricasDashboardEmpresaPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDashboardEmpresaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasDashboardEmpresaResult carregarMetricasDashboardEmpresa(UUID empresaId, LocalDate hoje) {
        LocalDate daqui7Dias = hoje.plusDays(7);
        LocalDate daqui30Dias = hoje.plusDays(30);
        return new MetricasDashboardEmpresaResult(
                contar("select count(*) from clientes_pacientes where empresa_id = ? and ativo = true", empresaId),
                contar("""
                        select count(*) from agenda_compromissos
                        where empresa_id = ? and status <> 'CANCELADO' and inicio::date = ?
                        """, empresaId, hoje),
                contar("""
                        select count(*) from agenda_compromissos
                        where empresa_id = ? and status <> 'CANCELADO' and inicio::date between ? and ?
                        """, empresaId, hoje, daqui7Dias),
                contar("select count(*) from servicos_procedimentos where empresa_id = ? and ativo = true", empresaId),
                contar("""
                        select count(*) from estoque_produtos
                        where empresa_id = ? and ativo = true and quantidade_atual <= estoque_minimo
                        """, empresaId),
                contar("""
                        select count(*) from estoque_produtos
                        where empresa_id = ? and ativo = true and validade is not null and validade between ? and ?
                        """, empresaId, hoje, daqui30Dias),
                contar("""
                        select count(*) from equipamentos
                        where empresa_id = ? and ativo = true
                          and proxima_manutencao_em is not null and proxima_manutencao_em between ? and ?
                        """, empresaId, hoje, daqui30Dias),
                somar("select coalesce(sum(valor), 0) from custos_gerais where empresa_id = ? and ativo = true", empresaId),
                somar("""
                        select coalesce(sum(valor), 0)
                        from custos_alimentacao_transporte
                        where empresa_id = ? and ativo = true
                        """, empresaId)
        );
    }

    private long contar(String sql, Object... parametros) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class, parametros);
        return total == null ? 0 : total;
    }

    private BigDecimal somar(String sql, Object... parametros) {
        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class, parametros);
        return total == null ? BigDecimal.ZERO : total;
    }
}
