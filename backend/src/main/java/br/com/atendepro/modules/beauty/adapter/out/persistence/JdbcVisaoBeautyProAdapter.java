package br.com.atendepro.modules.beauty.adapter.out.persistence;

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

import br.com.atendepro.modules.beauty.application.port.out.AtualizarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarClienteBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarVisaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarClientesBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarFichasEsteticasBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyProntuarioResult;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;
import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ObjetivoEsteticoBeautyPro;

@Repository
@Profile("!test")
public class JdbcVisaoBeautyProAdapter implements
        CarregarVisaoBeautyProPort,
        ListarClientesBeautyProPort,
        CarregarClienteBeautyProPort,
        CarregarFichaEsteticaBeautyProPort,
        SalvarFichaEsteticaBeautyProPort,
        AtualizarFichaEsteticaBeautyProPort,
        ListarFichasEsteticasBeautyProPort {

    private static final String AREA_BEAUTY = "BEAUTY";

    private final JdbcTemplate jdbcTemplate;

    public JdbcVisaoBeautyProAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasBeautyProResult carregarVisaoBeautyPro(UUID empresaId, LocalDate hoje) {
        LocalDate daqui7Dias = hoje.plusDays(7);
        return new MetricasBeautyProResult(
                carregarNomeEmpresa(empresaId),
                contar("select count(*) from clientes_pacientes where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_BEAUTY),
                contarAgendaBeauty(empresaId, hoje, hoje),
                contarAgendaBeauty(empresaId, hoje, daqui7Dias),
                contar("select count(*) from servicos_procedimentos where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_BEAUTY),
                contar("select count(*) from estoque_produtos where empresa_id = ? and ativo = true", empresaId),
                contar("select count(*) from equipamentos where empresa_id = ? and ativo = true", empresaId),
                contarSimulacoesBeauty(empresaId, false),
                contarSimulacoesBeauty(empresaId, true),
                listarClientesRecentes(empresaId)
        );
    }

    @Override
    public List<ClienteBeautyResumoResult> listarClientesBeautyPro(UUID empresaId, String busca) {
        String termo = busca == null || busca.isBlank() ? null : "%" + busca.trim().toLowerCase() + "%";
        if (termo == null) {
            return jdbcTemplate.query("""
                    select id, nome, telefone, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where empresa_id = ?
                      and area = ?
                    order by ativo desc, nome
                    limit 30
                    """, this::mapearCliente, empresaId, AREA_BEAUTY);
        }
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                  and (lower(nome) like ? or lower(coalesce(email, '')) like ? or lower(coalesce(telefone, '')) like ?)
                order by ativo desc, nome
                limit 30
                """, this::mapearCliente, empresaId, AREA_BEAUTY, termo, termo, termo);
    }

    @Override
    public Optional<ClienteBeautyProntuarioResult> carregarClienteBeautyPro(UUID empresaId, UUID clienteId, LocalDate hoje) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select id, empresa_id, nome, email, telefone, data_nascimento, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where id = ?
                      and empresa_id = ?
                      and area = ?
                    """, (rs, rowNum) -> mapearClienteProntuario(rs, hoje), clienteId, empresaId, AREA_BEAUTY));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<FichaEsteticaBeautyPro> carregarFichaAtual(UUID empresaId, UUID clienteId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from beauty_fichas_esteticas
                    where empresa_id = ?
                      and cliente_id = ?
                    order by atualizado_em desc
                    limit 1
                    """, this::mapearFichaEstetica, empresaId, clienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<FichaEsteticaBeautyPro> carregarFichaEstetica(UUID empresaId, UUID clienteId, UUID fichaId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from beauty_fichas_esteticas
                    where id = ?
                      and empresa_id = ?
                      and cliente_id = ?
                    """, this::mapearFichaEstetica, fichaId, empresaId, clienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void salvarFichaEstetica(FichaEsteticaBeautyPro ficha) {
        jdbcTemplate.update("""
                insert into beauty_fichas_esteticas (
                    id, empresa_id, cliente_id, objetivo, queixa_principal, historico_estetico,
                    alergias, medicamentos, gestante, lactante, sensibilidade_pele, usa_acidos,
                    exposicao_solar_intensa, procedimentos_recentes, contraindicacoes, observacoes,
                    criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ficha.id(),
                ficha.empresaId(),
                ficha.clienteId(),
                ficha.objetivo().name(),
                ficha.queixaPrincipal(),
                ficha.historicoEstetico(),
                ficha.alergias(),
                ficha.medicamentos(),
                ficha.gestante(),
                ficha.lactante(),
                ficha.sensibilidadePele(),
                ficha.usaAcidos(),
                ficha.exposicaoSolarIntensa(),
                ficha.procedimentosRecentes(),
                ficha.contraindicacoes(),
                ficha.observacoes(),
                Timestamp.from(ficha.criadoEm()),
                Timestamp.from(ficha.atualizadoEm())
        );
    }

    @Override
    public void atualizarFichaEstetica(FichaEsteticaBeautyPro ficha) {
        jdbcTemplate.update("""
                update beauty_fichas_esteticas
                set objetivo = ?,
                    queixa_principal = ?,
                    historico_estetico = ?,
                    alergias = ?,
                    medicamentos = ?,
                    gestante = ?,
                    lactante = ?,
                    sensibilidade_pele = ?,
                    usa_acidos = ?,
                    exposicao_solar_intensa = ?,
                    procedimentos_recentes = ?,
                    contraindicacoes = ?,
                    observacoes = ?,
                    atualizado_em = ?
                where id = ?
                  and empresa_id = ?
                  and cliente_id = ?
                """,
                ficha.objetivo().name(),
                ficha.queixaPrincipal(),
                ficha.historicoEstetico(),
                ficha.alergias(),
                ficha.medicamentos(),
                ficha.gestante(),
                ficha.lactante(),
                ficha.sensibilidadePele(),
                ficha.usaAcidos(),
                ficha.exposicaoSolarIntensa(),
                ficha.procedimentosRecentes(),
                ficha.contraindicacoes(),
                ficha.observacoes(),
                Timestamp.from(ficha.atualizadoEm()),
                ficha.id(),
                ficha.empresaId(),
                ficha.clienteId()
        );
    }

    @Override
    public List<FichaEsteticaBeautyPro> listarFichasEsteticas(UUID empresaId, UUID clienteId) {
        return jdbcTemplate.query("""
                select *
                from beauty_fichas_esteticas
                where empresa_id = ?
                  and cliente_id = ?
                order by atualizado_em desc
                limit 20
                """, this::mapearFichaEstetica, empresaId, clienteId);
    }

    private String carregarNomeEmpresa(UUID empresaId) {
        String nome = jdbcTemplate.queryForObject("select nome_fantasia from empresas where id = ?", String.class, empresaId);
        if (nome == null || nome.isBlank()) {
            return "Empresa selecionada";
        }
        return nome;
    }

    private long contarAgendaBeauty(UUID empresaId, LocalDate inicio, LocalDate fim) {
        return contar("""
                select count(*)
                from agenda_compromissos agenda
                left join clientes_pacientes cliente on cliente.id = agenda.cliente_paciente_id
                where agenda.empresa_id = ?
                  and agenda.status <> 'CANCELADO'
                  and agenda.inicio::date between ? and ?
                  and (cliente.area = ? or agenda.profissional_nome ilike '%estetic%')
                """, empresaId, inicio, fim, AREA_BEAUTY);
    }

    private long contarSimulacoesBeauty(UUID empresaId, boolean somenteAlertas) {
        String filtroAlerta = somenteAlertas ? " and simulacao.status_margem <> 'SAUDAVEL'" : "";
        return contar("""
                select count(*)
                from precificacao_simulacoes simulacao
                left join servicos_procedimentos servico on servico.id = simulacao.servico_procedimento_id
                where simulacao.empresa_id = ?
                  and simulacao.ativo = true
                  and (
                    servico.area = ?
                    or simulacao.nome_procedimento ilike '%pele%'
                    or simulacao.nome_procedimento ilike '%massagem%'
                    or simulacao.nome_procedimento ilike '%peeling%'
                    or simulacao.nome_procedimento ilike '%estet%'
                  )
                """ + filtroAlerta, empresaId, AREA_BEAUTY);
    }

    private List<ClienteBeautyResumoResult> listarClientesRecentes(UUID empresaId) {
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                order by atualizado_em desc
                limit 5
                """, this::mapearCliente, empresaId, AREA_BEAUTY);
    }

    private ClienteBeautyResumoResult mapearCliente(ResultSet rs, int rowNum) throws SQLException {
        return new ClienteBeautyResumoResult(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                rs.getString("telefone"),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private ClienteBeautyProntuarioResult mapearClienteProntuario(ResultSet rs, LocalDate hoje) throws SQLException {
        LocalDate dataNascimento = rs.getObject("data_nascimento", LocalDate.class);
        return new ClienteBeautyProntuarioResult(
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

    private FichaEsteticaBeautyPro mapearFichaEstetica(ResultSet rs, int rowNum) throws SQLException {
        return new FichaEsteticaBeautyPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                ObjetivoEsteticoBeautyPro.deCodigo(rs.getString("objetivo")),
                rs.getString("queixa_principal"),
                rs.getString("historico_estetico"),
                rs.getString("alergias"),
                rs.getString("medicamentos"),
                rs.getBoolean("gestante"),
                rs.getBoolean("lactante"),
                rs.getBoolean("sensibilidade_pele"),
                rs.getBoolean("usa_acidos"),
                rs.getBoolean("exposicao_solar_intensa"),
                rs.getString("procedimentos_recentes"),
                rs.getString("contraindicacoes"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
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
