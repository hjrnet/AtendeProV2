package br.com.atendepro.modules.spaces.adapter.out.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.spaces.application.port.out.CarregarIndicadoresSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;

@Repository
@Profile("!test")
public class JdbcIndicadoresSublocacaoSpacesAdapter implements CarregarIndicadoresSublocacaoSpacesPort {

    private static final BigDecimal HORAS_BASE_MES_POR_RECURSO = new BigDecimal("176.00");
    private static final BigDecimal CEM = new BigDecimal("100.00");

    private final JdbcTemplate jdbcTemplate;

    public JdbcIndicadoresSublocacaoSpacesAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IndicadoresSublocacaoSpacesResult carregarIndicadores(UUID empresaId, Instant periodoInicio, Instant periodoFim) {
        ResumoRecursos recursos = jdbcTemplate.queryForObject(
                """
                select count(*) as total_recursos,
                       coalesce(sum(case when ativo = true then 1 else 0 end), 0) as recursos_ativos
                from spaces_recursos
                where empresa_id = ?
                """,
                this::mapearRecursos,
                empresaId
        );
        Long pacotesAtivos = jdbcTemplate.queryForObject(
                """
                select count(*)
                from spaces_pacotes_sublocacao
                where empresa_id = ?
                  and ativo = true
                """,
                Long.class,
                empresaId
        );
        ResumoOcupacoes ocupacoes = jdbcTemplate.queryForObject(
                """
                select coalesce(sum(case when o.status = 'RESERVADA' then 1 else 0 end), 0) as reservadas,
                       coalesce(sum(case when o.status = 'CONFIRMADA' then 1 else 0 end), 0) as confirmadas,
                       coalesce(sum(case when o.status = 'CANCELADA' then 1 else 0 end), 0) as canceladas,
                       coalesce(sum(case
                           when o.status in ('RESERVADA', 'CONFIRMADA')
                           then extract(epoch from (least(o.fim_em, ?) - greatest(o.inicio_em, ?))) / 3600
                           else 0
                       end), 0) as horas_ocupadas,
                       coalesce(sum(case
                           when o.status in ('RESERVADA', 'CONFIRMADA') and o.pacote_id is not null
                           then p.valor_fixo
                           else 0
                       end), 0) as receita_fixa_prevista
                from spaces_ocupacoes o
                left join spaces_pacotes_sublocacao p on p.id = o.pacote_id
                where o.empresa_id = ?
                  and o.inicio_em < ?
                  and o.fim_em > ?
                """,
                this::mapearOcupacoes,
                Timestamp.from(periodoFim),
                Timestamp.from(periodoInicio),
                empresaId,
                Timestamp.from(periodoFim),
                Timestamp.from(periodoInicio)
        );

        BigDecimal taxaOcupacao = calcularTaxaOcupacao(recursos.recursosAtivos(), ocupacoes.horasOcupadas());
        return new IndicadoresSublocacaoSpacesResult(
                empresaId,
                periodoInicio,
                periodoFim,
                recursos.totalRecursos(),
                recursos.recursosAtivos(),
                pacotesAtivos == null ? 0 : pacotesAtivos,
                ocupacoes.reservadas(),
                ocupacoes.confirmadas(),
                ocupacoes.canceladas(),
                ocupacoes.horasOcupadas(),
                ocupacoes.receitaFixaPrevista(),
                taxaOcupacao
        );
    }

    private ResumoRecursos mapearRecursos(ResultSet rs, int rowNum) throws SQLException {
        return new ResumoRecursos(rs.getLong("total_recursos"), rs.getLong("recursos_ativos"));
    }

    private ResumoOcupacoes mapearOcupacoes(ResultSet rs, int rowNum) throws SQLException {
        return new ResumoOcupacoes(
                rs.getLong("reservadas"),
                rs.getLong("confirmadas"),
                rs.getLong("canceladas"),
                decimal(rs, "horas_ocupadas"),
                decimal(rs, "receita_fixa_prevista")
        );
    }

    private BigDecimal calcularTaxaOcupacao(long recursosAtivos, BigDecimal horasOcupadas) {
        BigDecimal capacidadeMes = BigDecimal.valueOf(recursosAtivos).multiply(HORAS_BASE_MES_POR_RECURSO);
        if (capacidadeMes.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }
        return horasOcupadas.multiply(CEM).divide(capacidadeMes, 2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal decimal(ResultSet rs, String coluna) throws SQLException {
        BigDecimal valor = rs.getBigDecimal(coluna);
        return valor == null ? BigDecimal.ZERO.setScale(2) : valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private record ResumoRecursos(long totalRecursos, long recursosAtivos) {
    }

    private record ResumoOcupacoes(
            long reservadas,
            long confirmadas,
            long canceladas,
            BigDecimal horasOcupadas,
            BigDecimal receitaFixaPrevista
    ) {
    }
}
