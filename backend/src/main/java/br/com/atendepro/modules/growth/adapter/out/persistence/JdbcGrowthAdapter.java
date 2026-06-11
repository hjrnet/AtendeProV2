package br.com.atendepro.modules.growth.adapter.out.persistence;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.RegistrarLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.port.out.GrowthPort;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ApresentacaoDemoGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ClientePosVendaGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.IndicadorVerticalGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.LeadGrowthResult;
import br.com.atendepro.modules.growth.domain.model.EtapaLeadGrowth;
import br.com.atendepro.modules.growth.domain.model.PerfilDemoGrowth;

@Repository
@Profile("!test")
public class JdbcGrowthAdapter implements GrowthPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcGrowthAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LeadGrowthResult> listarLeads(UUID empresaId, AreaCliente vertical, EtapaLeadGrowth etapa, String busca) {
        StringBuilder sql = new StringBuilder("""
                select id, empresa_id, nome, email, telefone, vertical, origem, etapa,
                       potencial_mensal, cliente_paciente_id, compromisso_agenda_id,
                       observacoes, criado_em, atualizado_em
                from growth_leads
                where empresa_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(empresaId);
        if (vertical != null) {
            sql.append(" and vertical = ?");
            params.add(vertical.name());
        }
        if (etapa != null) {
            sql.append(" and etapa = ?");
            params.add(etapa.name());
        }
        if (busca != null && !busca.isBlank()) {
            sql.append(" and (lower(nome) like ? or lower(email) like ? or coalesce(telefone, '') like ?)");
            String filtro = "%" + busca.trim().toLowerCase() + "%";
            params.add(filtro);
            params.add(filtro);
            params.add("%" + busca.trim() + "%");
        }
        sql.append(" order by atualizado_em desc, nome asc limit 100");
        return jdbcTemplate.query(sql.toString(), this::mapearLead, params.toArray());
    }

    @Override
    public LeadGrowthResult salvarLead(UUID id, RegistrarLeadGrowthCommand command, Instant agora) {
        jdbcTemplate.update(
                """
                insert into growth_leads (
                    id, empresa_id, nome, email, telefone, vertical, origem, etapa,
                    potencial_mensal, cliente_paciente_id, compromisso_agenda_id,
                    observacoes, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                command.empresaId(),
                command.nome(),
                command.email(),
                command.telefone(),
                command.vertical().name(),
                command.origem(),
                command.etapa().name(),
                command.potencialMensal(),
                command.clientePacienteId(),
                command.compromissoAgendaId(),
                command.observacoes(),
                Timestamp.from(agora),
                Timestamp.from(agora)
        );
        return listarLeads(command.empresaId(), null, null, command.email()).stream()
                .filter(lead -> lead.id().equals(id))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public Optional<LeadGrowthResult> atualizarEtapaLead(UUID empresaId, UUID leadId, EtapaLeadGrowth etapa, Instant atualizadoEm) {
        int linhas = jdbcTemplate.update(
                """
                update growth_leads
                   set etapa = ?, atualizado_em = ?
                 where empresa_id = ? and id = ?
                """,
                etapa.name(),
                Timestamp.from(atualizadoEm),
                empresaId,
                leadId
        );
        if (linhas == 0) {
            return Optional.empty();
        }
        return carregarLead(empresaId, leadId);
    }

    @Override
    public Optional<LeadGrowthResult> atualizarVinculosLead(UUID empresaId, UUID leadId, UUID clientePacienteId, UUID compromissoAgendaId, Instant atualizadoEm) {
        int linhas = jdbcTemplate.update(
                """
                update growth_leads
                   set cliente_paciente_id = ?, compromisso_agenda_id = ?, atualizado_em = ?
                 where empresa_id = ? and id = ?
                """,
                clientePacienteId,
                compromissoAgendaId,
                Timestamp.from(atualizadoEm),
                empresaId,
                leadId
        );
        if (linhas == 0) {
            return Optional.empty();
        }
        return carregarLead(empresaId, leadId);
    }

    @Override
    public List<ClientePosVendaGrowthResult> carregarClientesPosVenda(UUID empresaId, AreaCliente vertical) {
        StringBuilder sql = new StringBuilder("""
                select c.id, c.nome, c.area, c.email, c.telefone,
                       (select max(a.inicio) from agenda_compromissos a
                         where a.empresa_id = c.empresa_id and a.cliente_paciente_id = c.id and a.status = 'REALIZADO') as ultima_consulta_em,
                       (select min(a.inicio) from agenda_compromissos a
                         where a.empresa_id = c.empresa_id and a.cliente_paciente_id = c.id
                           and a.status in ('AGENDADO', 'CONFIRMADO') and a.inicio >= now()) as proxima_consulta_em,
                       (select max(rc.criado_em) from relacionamento_contatos rc
                         where rc.empresa_id = c.empresa_id and rc.cliente_id = c.id) as ultimo_contato_em,
                       (select count(*)::int from agenda_compromissos a
                         where a.empresa_id = c.empresa_id and a.cliente_paciente_id = c.id
                           and a.status = 'CANCELADO' and a.inicio >= now() - interval '90 days') as faltas_recentes,
                       (select rn.nota from relacionamento_nps rn
                         where rn.empresa_id = c.empresa_id and rn.cliente_id = c.id
                         order by rn.criado_em desc limit 1) as ultima_nota_nps,
                       c.atualizado_em
                  from clientes_pacientes c
                 where c.empresa_id = ? and c.ativo = true and c.area in ('NUTRI', 'BEAUTY')
                """);
        List<Object> params = new ArrayList<>();
        params.add(empresaId);
        if (vertical != null) {
            sql.append(" and c.area = ?");
            params.add(vertical.name());
        }
        sql.append(" order by c.atualizado_em desc, c.nome asc limit 100");
        return jdbcTemplate.query(sql.toString(), this::mapearClientePosVenda, params.toArray());
    }

    @Override
    public List<IndicadorVerticalGrowthResult> carregarIndicadoresVerticais(UUID empresaId) {
        return jdbcTemplate.query(
                """
                with p as (select ?::uuid empresa_id), verticais(area) as (values ('NUTRI'), ('BEAUTY'))
                select v.area,
                       (select count(*) from clientes_pacientes c where c.empresa_id = p.empresa_id and c.area = v.area and c.ativo = true) as clientes_ativos,
                       (select count(*) from agenda_compromissos a join clientes_pacientes c on c.id = a.cliente_paciente_id and c.empresa_id = a.empresa_id
                         where a.empresa_id = p.empresa_id and c.area = v.area and a.status in ('AGENDADO', 'CONFIRMADO') and a.inicio >= now() and a.inicio < now() + interval '30 days') as agenda_proximos_30_dias,
                       coalesce((select count(*)::numeric from agenda_compromissos a join clientes_pacientes c on c.id = a.cliente_paciente_id and c.empresa_id = a.empresa_id
                         where a.empresa_id = p.empresa_id and c.area = v.area and a.status in ('AGENDADO', 'CONFIRMADO') and a.inicio >= now() and a.inicio < now() + interval '30 days')
                         * coalesce((select avg(sp.preco_base) from servicos_procedimentos sp where sp.empresa_id = p.empresa_id and sp.area = v.area and sp.ativo = true), 0), 0) as faturamento_previsto,
                       coalesce((select avg(sp.preco_base) from servicos_procedimentos sp where sp.empresa_id = p.empresa_id and sp.area = v.area and sp.ativo = true), 0) as ticket_medio,
                       coalesce((select avg(ps.margem_real_percentual) from precificacao_simulacoes ps join servicos_procedimentos sp on sp.id = ps.servico_procedimento_id and sp.empresa_id = ps.empresa_id where ps.empresa_id = p.empresa_id and sp.area = v.area and ps.ativo = true), 0) as margem_media_percentual,
                       coalesce((select count(*)::numeric from clientes_pacientes c where c.empresa_id = p.empresa_id and c.area = v.area and c.ativo = true and (select count(*) from agenda_compromissos a where a.empresa_id = c.empresa_id and a.cliente_paciente_id = c.id and a.status = 'REALIZADO') > 1) * 100 / nullif((select count(*) from clientes_pacientes c where c.empresa_id = p.empresa_id and c.area = v.area and c.ativo = true), 0), 0) as recorrencia_percentual,
                       (select count(*) from clientes_pacientes c where c.empresa_id = p.empresa_id and c.area = v.area and c.ativo = true and (select count(*) from agenda_compromissos a where a.empresa_id = c.empresa_id and a.cliente_paciente_id = c.id and a.status = 'REALIZADO') > 1) as clientes_com_recompra
                  from verticais v cross join p
                 order by v.area
                """,
                this::mapearIndicador,
                empresaId
        );
    }

    @Override
    public List<ApresentacaoDemoGrowthResult> listarApresentacoesDemo(UUID empresaId, PerfilDemoGrowth perfil) {
        StringBuilder sql = new StringBuilder("""
                select id, perfil, titulo, roteiro, metricas_chave, chamada_para_acao, atualizado_em
                  from growth_demo_apresentacoes
                 where ativo = true and (empresa_id is null or empresa_id = ?)
                """);
        List<Object> params = new ArrayList<>();
        params.add(empresaId);
        if (perfil != null) {
            sql.append(" and perfil = ?");
            params.add(perfil.name());
        }
        sql.append(" order by case when empresa_id is null then 0 else 1 end, perfil, titulo");
        return jdbcTemplate.query(sql.toString(), this::mapearApresentacao, params.toArray());
    }

    private Optional<LeadGrowthResult> carregarLead(UUID empresaId, UUID leadId) {
        List<LeadGrowthResult> leads = jdbcTemplate.query(
                """
                select id, empresa_id, nome, email, telefone, vertical, origem, etapa,
                       potencial_mensal, cliente_paciente_id, compromisso_agenda_id,
                       observacoes, criado_em, atualizado_em
                  from growth_leads
                 where empresa_id = ? and id = ?
                """,
                this::mapearLead,
                empresaId,
                leadId
        );
        return leads.stream().findFirst();
    }

    private LeadGrowthResult mapearLead(ResultSet rs, int rowNum) throws SQLException {
        return new LeadGrowthResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("telefone"),
                AreaCliente.valueOf(rs.getString("vertical")),
                rs.getString("origem"),
                EtapaLeadGrowth.valueOf(rs.getString("etapa")),
                rs.getBigDecimal("potencial_mensal"),
                rs.getObject("cliente_paciente_id", UUID.class),
                rs.getObject("compromisso_agenda_id", UUID.class),
                rs.getString("observacoes"),
                instant(rs, "criado_em"),
                instant(rs, "atualizado_em")
        );
    }

    private ClientePosVendaGrowthResult mapearClientePosVenda(ResultSet rs, int rowNum) throws SQLException {
        return new ClientePosVendaGrowthResult(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                AreaCliente.valueOf(rs.getString("area")),
                rs.getString("email"),
                rs.getString("telefone"),
                instant(rs, "ultima_consulta_em"),
                instant(rs, "proxima_consulta_em"),
                instant(rs, "ultimo_contato_em"),
                rs.getInt("faltas_recentes"),
                (Integer) rs.getObject("ultima_nota_nps"),
                instant(rs, "atualizado_em")
        );
    }

    private IndicadorVerticalGrowthResult mapearIndicador(ResultSet rs, int rowNum) throws SQLException {
        AreaCliente vertical = AreaCliente.valueOf(rs.getString("area"));
        BigDecimal faturamento = valor(rs, "faturamento_previsto");
        BigDecimal ticket = valor(rs, "ticket_medio");
        BigDecimal margem = valor(rs, "margem_media_percentual");
        BigDecimal recorrencia = valor(rs, "recorrencia_percentual");
        long agenda = rs.getLong("agenda_proximos_30_dias");
        String leitura = agenda > 0
                ? "Demanda futura mapeada; revisar capacidade, recorrencia e margem antes de vender novos pacotes."
                : "Baixa demanda futura; priorizar campanhas de reativacao e conversao do funil.";
        return new IndicadorVerticalGrowthResult(
                vertical,
                rs.getLong("clientes_ativos"),
                agenda,
                faturamento,
                ticket,
                margem,
                recorrencia,
                rs.getLong("clientes_com_recompra"),
                leitura
        );
    }

    private ApresentacaoDemoGrowthResult mapearApresentacao(ResultSet rs, int rowNum) throws SQLException {
        return new ApresentacaoDemoGrowthResult(
                rs.getObject("id", UUID.class),
                PerfilDemoGrowth.valueOf(rs.getString("perfil")),
                rs.getString("titulo"),
                rs.getString("roteiro"),
                rs.getString("metricas_chave"),
                rs.getString("chamada_para_acao"),
                instant(rs, "atualizado_em")
        );
    }

    private Instant instant(ResultSet rs, String coluna) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(coluna);
        return timestamp == null ? null : timestamp.toInstant();
    }

    private BigDecimal valor(ResultSet rs, String coluna) throws SQLException {
        BigDecimal valor = rs.getBigDecimal(coluna);
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
