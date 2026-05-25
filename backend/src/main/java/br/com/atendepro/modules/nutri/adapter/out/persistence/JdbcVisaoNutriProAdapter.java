package br.com.atendepro.modules.nutri.adapter.out.persistence;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.nutri.application.port.out.CarregarAvaliacaoAntropometricaNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarVisaoNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarProntuarioNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarAvaliacoesAntropometricasNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarPacientesNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.SalvarAvaliacaoAntropometricaNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.VerificarPacienteNutriProPort;
import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;
import br.com.atendepro.modules.nutri.domain.model.ObjetivoNutricionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.SexoBiologicoNutriPro;
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
        CarregarProntuarioNutriProPort,
        VerificarPacienteNutriProPort,
        SalvarAvaliacaoAntropometricaNutriProPort,
        ListarAvaliacoesAntropometricasNutriProPort,
        CarregarAvaliacaoAntropometricaNutriProPort {

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
                                existeAvaliacaoAntropometrica(empresaId, pacienteId) ? "DISPONIVEL" : "PROXIMA_TASK",
                                existeAvaliacaoAntropometrica(empresaId, pacienteId) ? "DISPONIVEL" : "PROXIMA_TASK",
                                "PREPARADO",
                                carregarUltimaConsulta(empresaId, pacienteId)
                        )
                ));
    }

    @Override
    public boolean existePacienteNutriPro(UUID empresaId, UUID pacienteId) {
        return contar("""
                select count(*)
                from clientes_pacientes
                where empresa_id = ?
                  and id = ?
                  and area = ?
                """, empresaId, pacienteId, AREA_NUTRI) > 0;
    }

    @Override
    public void salvarAvaliacaoAntropometrica(AvaliacaoAntropometricaNutriPro avaliacao) {
        jdbcTemplate.update("""
                insert into nutri_avaliacoes_antropometricas (
                    id, empresa_id, paciente_id, peso_kg, altura_cm, idade, sexo, imc, objetivo,
                    fator_atividade, geb_kcal, tmb_kcal, get_kcal, meta_energetica_kcal,
                    formula, aviso, observacoes, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                avaliacao.id(),
                avaliacao.empresaId(),
                avaliacao.pacienteId(),
                avaliacao.pesoKg(),
                avaliacao.alturaCm(),
                avaliacao.idade(),
                avaliacao.sexo().name(),
                avaliacao.imc(),
                avaliacao.objetivo().name(),
                avaliacao.fatorAtividade(),
                avaliacao.gebKcal(),
                avaliacao.tmbKcal(),
                avaliacao.getKcal(),
                avaliacao.metaEnergeticaKcal(),
                avaliacao.formula(),
                avaliacao.aviso(),
                avaliacao.observacoes(),
                Timestamp.from(avaliacao.criadoEm()),
                Timestamp.from(avaliacao.atualizadoEm())
        );
    }

    @Override
    public List<AvaliacaoAntropometricaNutriPro> listarAvaliacoesAntropometricas(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_avaliacoes_antropometricas
                where empresa_id = ?
                  and paciente_id = ?
                order by criado_em desc
                limit 20
                """, this::mapearAvaliacaoAntropometrica, empresaId, pacienteId);
    }

    @Override
    public Optional<AvaliacaoAntropometricaNutriPro> carregarAvaliacaoAntropometrica(UUID empresaId, UUID pacienteId, UUID avaliacaoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_avaliacoes_antropometricas
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearAvaliacaoAntropometrica, avaliacaoId, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
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

    private boolean existeAvaliacaoAntropometrica(UUID empresaId, UUID pacienteId) {
        return contar("""
                select count(*)
                from nutri_avaliacoes_antropometricas
                where empresa_id = ?
                  and paciente_id = ?
                """, empresaId, pacienteId) > 0;
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

    private AvaliacaoAntropometricaNutriPro mapearAvaliacaoAntropometrica(ResultSet rs, int rowNum) throws SQLException {
        return new AvaliacaoAntropometricaNutriPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                bigDecimal(rs, "peso_kg"),
                bigDecimal(rs, "altura_cm"),
                rs.getInt("idade"),
                SexoBiologicoNutriPro.deCodigo(rs.getString("sexo")),
                bigDecimal(rs, "imc"),
                ObjetivoNutricionalNutriPro.deCodigo(rs.getString("objetivo")),
                bigDecimal(rs, "fator_atividade"),
                bigDecimal(rs, "geb_kcal"),
                bigDecimal(rs, "tmb_kcal"),
                bigDecimal(rs, "get_kcal"),
                bigDecimal(rs, "meta_energetica_kcal"),
                rs.getString("formula"),
                rs.getString("aviso"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private BigDecimal bigDecimal(ResultSet rs, String coluna) throws SQLException {
        return rs.getBigDecimal(coluna);
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
