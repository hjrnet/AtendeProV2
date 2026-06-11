package br.com.atendepro.modules.adminsaas.adapter.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.atendepro.modules.adminsaas.application.command.RegistrarEventoAuditoriaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.port.out.ConsultarAuditoriaOperacionalAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.RegistrarEventoAuditoriaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.AuditoriaOperacionalAdminSaasResult;
import br.com.atendepro.modules.adminsaas.application.result.ChecklistAuditoriaAdminSaasResult;
import br.com.atendepro.modules.adminsaas.application.result.EventoAuditoriaAdminSaasResult;

@Repository
@Profile("!test")
public class JdbcAdminSaasAuditoriaAdapter implements
        RegistrarEventoAuditoriaAdminSaasPort,
        ConsultarAuditoriaOperacionalAdminSaasPort {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JdbcAdminSaasAuditoriaAdapter(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registrarEvento(RegistrarEventoAuditoriaAdminSaasCommand command) {
        jdbcTemplate.update(
                """
                insert into admin_saas_auditoria_eventos
                    (tipo, severidade, descricao, empresa_id, usuario_id, referencia_tipo, referencia_id, metadados)
                values (?, ?, ?, ?, ?, ?, ?, ?::jsonb)
                """,
                command.tipo(),
                command.severidade(),
                command.descricao(),
                command.empresaId(),
                command.usuarioId(),
                command.referenciaTipo(),
                command.referenciaId(),
                serializarMetadados(command.metadados())
        );
    }

    @Override
    public AuditoriaOperacionalAdminSaasResult carregarAuditoriaOperacional() {
        long eventosUltimos7Dias = contar(
                "select count(*) from admin_saas_auditoria_eventos where criado_em >= now() - interval '7 days'"
        );
        long eventosCriticosUltimos7Dias = contar(
                """
                select count(*)
                from admin_saas_auditoria_eventos
                where criado_em >= now() - interval '7 days'
                  and severidade in ('ALTA', 'CRITICA')
                """
        );
        long empresasBloqueadas = contar("select count(*) from empresas where ativo = false");
        long trialsExpirando7Dias = contar(
                """
                select count(*)
                from assinatura_trials
                where status = 'ATIVO'
                  and expira_em between now() and now() + interval '7 days'
                """
        );
        long chamadosCriticosAbertos = contar(
                """
                select count(*)
                from chamados_suporte
                where status not in ('RESOLVIDO', 'FECHADO')
                  and prioridade in ('ALTA', 'CRITICA', 'URGENTE')
                """
        );

        return new AuditoriaOperacionalAdminSaasResult(
                eventosUltimos7Dias,
                eventosCriticosUltimos7Dias,
                empresasBloqueadas,
                trialsExpirando7Dias,
                chamadosCriticosAbertos,
                montarChecklist(eventosCriticosUltimos7Dias, empresasBloqueadas, trialsExpirando7Dias, chamadosCriticosAbertos),
                listarEventosRecentes(),
                Instant.now()
        );
    }

    private List<ChecklistAuditoriaAdminSaasResult> montarChecklist(
            long eventosCriticosUltimos7Dias,
            long empresasBloqueadas,
            long trialsExpirando7Dias,
            long chamadosCriticosAbertos
    ) {
        return List.of(
                itemChecklist("EVENTOS_CRITICOS", "Eventos criticos recentes", eventosCriticosUltimos7Dias == 0,
                        "Nenhum evento critico nos ultimos 7 dias.", eventosCriticosUltimos7Dias + " evento(s) critico(s) exigem revisao.", "ALTA"),
                itemChecklist("EMPRESAS_BLOQUEADAS", "Empresas bloqueadas", empresasBloqueadas == 0,
                        "Nao ha empresa bloqueada no momento.", empresasBloqueadas + " empresa(s) bloqueada(s) devem ser acompanhadas.", "MEDIA"),
                itemChecklist("TRIALS_EXPIRANDO", "Trials expirando em 7 dias", trialsExpirando7Dias == 0,
                        "Nao ha trial expirando nos proximos 7 dias.", trialsExpirando7Dias + " trial(s) precisam de acao comercial.", "MEDIA"),
                itemChecklist("CHAMADOS_CRITICOS", "Chamados criticos abertos", chamadosCriticosAbertos == 0,
                        "Nao ha chamado critico aberto.", chamadosCriticosAbertos + " chamado(s) critico(s) exigem resposta.", "ALTA")
        );
    }

    private ChecklistAuditoriaAdminSaasResult itemChecklist(
            String codigo,
            String titulo,
            boolean ok,
            String detalheOk,
            String detalheAlerta,
            String severidade
    ) {
        return new ChecklistAuditoriaAdminSaasResult(
                codigo,
                titulo,
                ok ? "OK" : "ACAO_REQUERIDA",
                ok ? detalheOk : detalheAlerta,
                ok ? "BAIXA" : severidade
        );
    }

    private List<EventoAuditoriaAdminSaasResult> listarEventosRecentes() {
        return jdbcTemplate.query(
                """
                select a.id, a.tipo, a.severidade, a.descricao, a.empresa_id, e.nome_fantasia as empresa_nome,
                       a.usuario_id, a.referencia_tipo, a.referencia_id, a.metadados::text as metadados, a.criado_em
                from admin_saas_auditoria_eventos a
                left join empresas e on e.id = a.empresa_id
                order by a.criado_em desc
                limit 12
                """,
                (rs, rowNum) -> new EventoAuditoriaAdminSaasResult(
                        rs.getObject("id", UUID.class),
                        rs.getString("tipo"),
                        rs.getString("severidade"),
                        rs.getString("descricao"),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("empresa_nome"),
                        rs.getObject("usuario_id", UUID.class),
                        rs.getString("referencia_tipo"),
                        rs.getObject("referencia_id", UUID.class),
                        rs.getString("metadados"),
                        rs.getTimestamp("criado_em").toInstant()
                )
        );
    }

    private long contar(String sql) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class);
        return total == null ? 0 : total;
    }

    private String serializarMetadados(Map<String, String> metadados) {
        try {
            return objectMapper.writeValueAsString(metadados == null ? Map.of() : metadados);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Nao foi possivel serializar metadados de auditoria Admin SaaS.", e);
        }
    }
}
