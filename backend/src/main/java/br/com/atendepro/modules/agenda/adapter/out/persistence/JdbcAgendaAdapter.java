package br.com.atendepro.modules.agenda.adapter.out.persistence;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.agenda.application.port.out.CarregarCompromissoAgendaPorIdPort;
import br.com.atendepro.modules.agenda.application.port.out.ListarAgendaPort;
import br.com.atendepro.modules.agenda.application.port.out.SalvarCompromissoAgendaPort;
import br.com.atendepro.modules.agenda.application.port.out.VerificarConflitoAgendaPort;
import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.modules.agenda.domain.model.CompromissoAgenda;
import br.com.atendepro.modules.agenda.domain.model.TipoCompromisso;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcAgendaAdapter implements
        SalvarCompromissoAgendaPort,
        CarregarCompromissoAgendaPorIdPort,
        ListarAgendaPort,
        VerificarConflitoAgendaPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAgendaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarCompromisso(CompromissoAgenda compromisso) {
        jdbcTemplate.update(
                """
                insert into agenda_compromissos (
                    id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome, sala,
                    tipo, status, inicio, fim, observacoes, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                compromisso.id(),
                compromisso.empresaId(),
                compromisso.clientePacienteId(),
                compromisso.profissionalId(),
                compromisso.profissionalNome(),
                compromisso.sala(),
                compromisso.tipo().name(),
                compromisso.status().name(),
                Timestamp.from(compromisso.inicio()),
                Timestamp.from(compromisso.fim()),
                compromisso.observacoes(),
                Timestamp.from(compromisso.criadoEm()),
                Timestamp.from(compromisso.atualizadoEm())
        );
    }

    @Override
    public Optional<CompromissoAgenda> carregarCompromissoPorId(UUID compromissoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome, sala,
                           tipo, status, inicio, fim, observacoes, criado_em, atualizado_em
                    from agenda_compromissos
                    where id = ?
                    """,
                    (rs, rowNum) -> mapearCompromisso(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("cliente_paciente_id", UUID.class),
                            rs.getObject("profissional_id", UUID.class),
                            rs.getString("profissional_nome"),
                            rs.getString("sala"),
                            TipoCompromisso.valueOf(rs.getString("tipo")),
                            AgendaStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("inicio"),
                            rs.getTimestamp("fim"),
                            rs.getString("observacoes"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")
                    ),
                    compromissoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<CompromissoAgenda> listarAgenda(
            UUID empresaId,
            Paginacao paginacao,
            Instant inicio,
            Instant fim,
            UUID profissionalId,
            String sala,
            AgendaStatus status
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, inicio, fim, profissionalId, sala, status, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from agenda_compromissos " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var compromissos = jdbcTemplate.query(
                """
                select id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome, sala,
                       tipo, status, inicio, fim, observacoes, criado_em, atualizado_em
                from agenda_compromissos
                %s
                order by inicio asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearCompromisso(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("cliente_paciente_id", UUID.class),
                        rs.getObject("profissional_id", UUID.class),
                        rs.getString("profissional_nome"),
                        rs.getString("sala"),
                        TipoCompromisso.valueOf(rs.getString("tipo")),
                        AgendaStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("inicio"),
                        rs.getTimestamp("fim"),
                        rs.getString("observacoes"),
                        rs.getTimestamp("criado_em"),
                        rs.getTimestamp("atualizado_em")
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(compromissos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    @Override
    public boolean existeConflitoAgenda(UUID empresaId, UUID profissionalId, String sala, Instant inicio, Instant fim) {
        var parametros = new ArrayList<>();
        var filtro = new StringBuilder(
                """
                select count(*)
                from agenda_compromissos
                where empresa_id = ?
                  and status not in ('CANCELADO')
                  and inicio < ?
                  and fim > ?
                """
        );
        parametros.add(empresaId);
        parametros.add(Timestamp.from(fim));
        parametros.add(Timestamp.from(inicio));
        if (profissionalId != null && sala != null && !sala.isBlank()) {
            filtro.append(" and (profissional_id = ? or lower(sala) = ?)");
            parametros.add(profissionalId);
            parametros.add(sala.trim().toLowerCase());
        } else if (profissionalId != null) {
            filtro.append(" and profissional_id = ?");
            parametros.add(profissionalId);
        } else if (sala != null && !sala.isBlank()) {
            filtro.append(" and lower(sala) = ?");
            parametros.add(sala.trim().toLowerCase());
        } else {
            return false;
        }

        Long total = jdbcTemplate.queryForObject(filtro.toString(), Long.class, parametros.toArray());
        return total != null && total > 0;
    }

    private String montarFiltro(
            UUID empresaId,
            Instant inicio,
            Instant fim,
            UUID profissionalId,
            String sala,
            AgendaStatus status,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (inicio != null) {
            filtro.append(" and fim > ?");
            parametros.add(Timestamp.from(inicio));
        }
        if (fim != null) {
            filtro.append(" and inicio < ?");
            parametros.add(Timestamp.from(fim));
        }
        if (profissionalId != null) {
            filtro.append(" and profissional_id = ?");
            parametros.add(profissionalId);
        }
        if (sala != null && !sala.isBlank()) {
            filtro.append(" and lower(sala) = ?");
            parametros.add(sala.trim().toLowerCase());
        }
        if (status != null) {
            filtro.append(" and status = ?");
            parametros.add(status.name());
        }
        return filtro.toString();
    }

    private CompromissoAgenda mapearCompromisso(
            UUID id,
            UUID empresaId,
            UUID clientePacienteId,
            UUID profissionalId,
            String profissionalNome,
            String sala,
            TipoCompromisso tipo,
            AgendaStatus status,
            Timestamp inicio,
            Timestamp fim,
            String observacoes,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new CompromissoAgenda(
                id,
                empresaId,
                clientePacienteId,
                profissionalId,
                profissionalNome,
                sala,
                tipo,
                status,
                inicio.toInstant(),
                fim.toInstant(),
                observacoes,
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }
}
