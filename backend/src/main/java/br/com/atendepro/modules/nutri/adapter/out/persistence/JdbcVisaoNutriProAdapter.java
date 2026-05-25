package br.com.atendepro.modules.nutri.adapter.out.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.nutri.application.port.out.CarregarVisaoNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarProntuarioNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarPacientesNutriProPort;
import br.com.atendepro.modules.nutri.application.result.DadosProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.MetricasNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;
import br.com.atendepro.modules.nutri.application.result.ResumoProntuarioNutriProResult;

@Repository
@Profile("!test")
public class JdbcVisaoNutriProAdapter implements
        CarregarVisaoNutriProPort,
        ListarPacientesNutriProPort,
        CarregarProntuarioNutriProPort {

    private static final String AREA_NUTRI = "NUTRI";

    private final JdbcTemplate jdbcTemplate;

    public JdbcVisaoNutriProAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasNutriProResult carregarVisaoNutriPro(UUID empresaId, LocalDate hoje) {
        LocalDate daqui7Dias = hoje.plusDays(7);
        return new MetricasNutriProResult(
                carregarNomeEmpresa(empresaId),
                contar("select count(*) from clientes_pacientes where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_NUTRI),
                contarAgendaNutri(empresaId, hoje, hoje),
                contarAgendaNutri(empresaId, hoje, daqui7Dias),
                contar("select count(*) from servicos_procedimentos where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_NUTRI),
                contarDocumentosNutri(empresaId),
                contarSimulacoesNutri(empresaId, false),
                contarSimulacoesNutri(empresaId, true),
                0,
                listarPacientesRecentes(empresaId)
        );
    }

    @Override
    public List<PacienteNutriResumoResult> listarPacientesNutriPro(UUID empresaId, String busca) {
        String termo = busca == null || busca.isBlank() ? null : "%" + busca.trim().toLowerCase() + "%";
        if (termo == null) {
            return jdbcTemplate.query("""
                    select id, nome, telefone, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where empresa_id = ?
                      and area = ?
                    order by ativo desc, nome
                    limit 30
                    """, this::mapearPaciente, empresaId, AREA_NUTRI);
        }
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                  and (lower(nome) like ? or lower(coalesce(email, '')) like ? or lower(coalesce(telefone, '')) like ?)
                order by ativo desc, nome
                limit 30
                """, this::mapearPaciente, empresaId, AREA_NUTRI, termo, termo, termo);
    }

    @Override
    public Optional<DadosProntuarioNutriProResult> carregarProntuarioNutriPro(UUID empresaId, UUID pacienteId, LocalDate hoje) {
        return carregarPacienteProntuario(empresaId, pacienteId, hoje)
                .map(paciente -> new DadosProntuarioNutriProResult(
                        paciente,
                        new ResumoProntuarioNutriProResult(
                                contar("select count(*) from documentos_profissionais where empresa_id = ? and cliente_paciente_id = ? and ativo = true", empresaId, pacienteId),
                                contar("""
                                        select count(*)
                                        from agenda_compromissos
                                        where empresa_id = ?
                                          and cliente_paciente_id = ?
                                          and status <> 'CANCELADO'
                                          and inicio::date >= ?
                                        """, empresaId, pacienteId, hoje),
                                contarSimulacoesNutri(empresaId, false),
                                0,
                                "PREPARADO",
                                "PREPARADO",
                                "PROXIMA_TASK",
                                "PROXIMA_TASK",
                                "PREPARADO",
                                carregarUltimaConsulta(empresaId, pacienteId)
                        )
                ));
    }

    private String carregarNomeEmpresa(UUID empresaId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select nome_fantasia from empresas where id = ?",
                    String.class,
                    empresaId
            );
        } catch (EmptyResultDataAccessException exception) {
            return "Empresa selecionada";
        }
    }

    private long contarAgendaNutri(UUID empresaId, LocalDate inicio, LocalDate fim) {
        return contar("""
                select count(*)
                from agenda_compromissos agenda
                left join clientes_pacientes cliente on cliente.id = agenda.cliente_paciente_id
                where agenda.empresa_id = ?
                  and agenda.status <> 'CANCELADO'
                  and agenda.inicio::date between ? and ?
                  and (cliente.area = ? or agenda.profissional_nome ilike '%nutri%')
                """, empresaId, inicio, fim, AREA_NUTRI);
    }

    private long contarDocumentosNutri(UUID empresaId) {
        return contar("""
                select count(*)
                from documentos_profissionais documento
                left join clientes_pacientes cliente on cliente.id = documento.cliente_paciente_id
                where documento.empresa_id = ?
                  and documento.ativo = true
                  and (cliente.area = ? or documento.tipo ilike '%NUTRI%' or documento.titulo ilike '%nutri%')
                """, empresaId, AREA_NUTRI);
    }

    private long contarSimulacoesNutri(UUID empresaId, boolean somenteAlertas) {
        String filtroAlerta = somenteAlertas ? " and simulacao.status_margem <> 'SAUDAVEL'" : "";
        return contar("""
                select count(*)
                from precificacao_simulacoes simulacao
                left join servicos_procedimentos servico on servico.id = simulacao.servico_procedimento_id
                where simulacao.empresa_id = ?
                  and simulacao.ativo = true
                  and (servico.area = ? or simulacao.nome_procedimento ilike '%nutri%')
                """ + filtroAlerta, empresaId, AREA_NUTRI);
    }

    private List<PacienteNutriResumoResult> listarPacientesRecentes(UUID empresaId) {
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                order by atualizado_em desc
                limit 5
                """, this::mapearPaciente, empresaId, AREA_NUTRI);
    }

    private Optional<PacienteProntuarioNutriProResult> carregarPacienteProntuario(UUID empresaId, UUID pacienteId, LocalDate hoje) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select id, empresa_id, nome, email, telefone, data_nascimento, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where id = ?
                      and empresa_id = ?
                      and area = ?
                    """, (rs, rowNum) -> mapearPacienteProntuario(rs, hoje), pacienteId, empresaId, AREA_NUTRI));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private PacienteProntuarioNutriProResult mapearPacienteProntuario(ResultSet rs, LocalDate hoje) throws SQLException {
        LocalDate dataNascimento = rs.getObject("data_nascimento", LocalDate.class);
        return new PacienteProntuarioNutriProResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("telefone"),
                dataNascimento,
                idade(dataNascimento, hoje),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private java.time.Instant carregarUltimaConsulta(UUID empresaId, UUID pacienteId) {
        try {
            var timestamp = jdbcTemplate.queryForObject("""
                    select max(inicio)
                    from agenda_compromissos
                    where empresa_id = ?
                      and cliente_paciente_id = ?
                      and status <> 'CANCELADO'
                      and inicio < now()
                    """, java.sql.Timestamp.class, empresaId, pacienteId);
            return timestamp == null ? null : timestamp.toInstant();
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private PacienteNutriResumoResult mapearPaciente(ResultSet rs, int rowNum) throws SQLException {
        return new PacienteNutriResumoResult(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                rs.getString("telefone"),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private Integer idade(LocalDate dataNascimento, LocalDate hoje) {
        if (dataNascimento == null) {
            return null;
        }
        return Period.between(dataNascimento, hoje).getYears();
    }

    private long contar(String sql, Object... parametros) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class, parametros);
        return total == null ? 0 : total;
    }
}
