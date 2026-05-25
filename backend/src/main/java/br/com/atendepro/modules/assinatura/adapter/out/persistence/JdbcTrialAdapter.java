package br.com.atendepro.modules.assinatura.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.assinatura.application.port.out.AtualizarTrialPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarTrialAtivoPorEmpresaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarTrialPorIdPort;
import br.com.atendepro.modules.assinatura.application.port.out.ListarTrialsPort;
import br.com.atendepro.modules.assinatura.application.port.out.SalvarTrialPort;
import br.com.atendepro.modules.assinatura.domain.model.TrialAssinatura;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcTrialAdapter implements
        SalvarTrialPort,
        AtualizarTrialPort,
        CarregarTrialAtivoPorEmpresaPort,
        CarregarTrialPorIdPort,
        ListarTrialsPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTrialAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarTrial(TrialAssinatura trial) {
        jdbcTemplate.update(
                """
                insert into assinatura_trials (
                    id, empresa_id, plano_id, status, iniciado_em, expira_em, convertido_em, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                trial.id(),
                trial.empresaId(),
                trial.planoId(),
                trial.status().name(),
                Timestamp.from(trial.iniciadoEm()),
                Timestamp.from(trial.expiraEm()),
                trial.convertidoEm() == null ? null : Timestamp.from(trial.convertidoEm()),
                Timestamp.from(trial.criadoEm()),
                Timestamp.from(trial.atualizadoEm())
        );
    }

    @Override
    public void atualizarTrial(TrialAssinatura trial) {
        jdbcTemplate.update(
                """
                update assinatura_trials
                set status = ?,
                    convertido_em = ?,
                    atualizado_em = ?
                where id = ?
                """,
                trial.status().name(),
                trial.convertidoEm() == null ? null : Timestamp.from(trial.convertidoEm()),
                Timestamp.from(trial.atualizadoEm()),
                trial.id()
        );
    }

    @Override
    public Optional<TrialAssinatura> carregarTrialAtivoPorEmpresa(UUID empresaId) {
        return carregarTrial(
                """
                select id, empresa_id, plano_id, status, iniciado_em, expira_em, convertido_em, criado_em, atualizado_em
                from assinatura_trials
                where empresa_id = ? and status = 'ATIVO'
                order by expira_em desc
                limit 1
                """,
                empresaId
        );
    }

    @Override
    public Optional<TrialAssinatura> carregarTrialPorId(UUID trialId) {
        return carregarTrial(
                """
                select id, empresa_id, plano_id, status, iniciado_em, expira_em, convertido_em, criado_em, atualizado_em
                from assinatura_trials
                where id = ?
                """,
                trialId
        );
    }

    @Override
    public ResultadoPaginado<TrialAssinatura> listarTrials(Paginacao paginacao, TrialStatus status) {
        var parametros = new ArrayList<>();
        String filtro = "";
        if (status != null) {
            filtro = "where status = ?";
            parametros.add(status.name());
        }
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from assinatura_trials " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var trials = jdbcTemplate.query(
                """
                select id, empresa_id, plano_id, status, iniciado_em, expira_em, convertido_em, criado_em, atualizado_em
                from assinatura_trials
                %s
                order by criado_em desc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearTrial(rs.getObject("id", UUID.class)),
                parametros.toArray()
        );

        return new ResultadoPaginado<>(trials, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private Optional<TrialAssinatura> carregarTrial(String sql, Object parametro) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> mapearTrial(rs.getObject("id", UUID.class)),
                    parametro
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private TrialAssinatura mapearTrial(UUID trialId) {
        return jdbcTemplate.queryForObject(
                """
                select id, empresa_id, plano_id, status, iniciado_em, expira_em, convertido_em, criado_em, atualizado_em
                from assinatura_trials
                where id = ?
                """,
                (rs, rowNum) -> new TrialAssinatura(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("plano_id", UUID.class),
                        TrialStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("iniciado_em").toInstant(),
                        rs.getTimestamp("expira_em").toInstant(),
                        rs.getTimestamp("convertido_em") == null ? null : rs.getTimestamp("convertido_em").toInstant(),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                trialId
        );
    }
}
