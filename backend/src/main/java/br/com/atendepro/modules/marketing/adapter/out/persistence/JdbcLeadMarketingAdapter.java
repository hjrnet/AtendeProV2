package br.com.atendepro.modules.marketing.adapter.out.persistence;

import java.sql.Timestamp;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.marketing.application.port.out.SalvarLeadMarketingPort;
import br.com.atendepro.modules.marketing.domain.model.LeadMarketing;

@Repository
@Profile("!test")
public class JdbcLeadMarketingAdapter implements SalvarLeadMarketingPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLeadMarketingAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarLead(LeadMarketing lead) {
        jdbcTemplate.update(
                """
                insert into marketing_leads (
                    id, nome, email, telefone, area_interesse, tamanho_operacao,
                    origem, mensagem, status, criado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                lead.id(),
                lead.nome(),
                lead.email(),
                lead.telefone(),
                lead.areaInteresse().name(),
                lead.tamanhoOperacao().name(),
                lead.origem(),
                lead.mensagem(),
                lead.status().name(),
                Timestamp.from(lead.criadoEm())
        );
    }
}
