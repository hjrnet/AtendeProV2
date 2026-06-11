package br.com.atendepro.modules.mobile.adapter.out.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.mobile.application.port.out.ListarClientesVinculadosMobilePort;
import br.com.atendepro.modules.mobile.application.result.ClienteVinculadoMobileResult;

@Repository
@Profile("!test")
public class JdbcMobilePerfilAdapter implements ListarClientesVinculadosMobilePort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMobilePerfilAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ClienteVinculadoMobileResult> listarClientesVinculadosPorEmail(UUID empresaId, String email) {
        if (empresaId == null || email == null || email.isBlank()) {
            return List.of();
        }

        return jdbcTemplate.query(
                """
                select id, empresa_id, nome, tipo, area, documento, email, telefone, data_nascimento,
                       observacoes, ativo, criado_em, atualizado_em
                  from clientes_pacientes
                 where empresa_id = ?
                   and ativo = true
                   and lower(email) = lower(?)
                 order by case area
                            when 'NUTRI' then 1
                            when 'BEAUTY' then 2
                            else 3
                          end,
                          atualizado_em desc
                """,
                (rs, rowNum) -> new ClienteVinculadoMobileResult(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("nome"),
                        rs.getString("tipo"),
                        rs.getString("area"),
                        rs.getString("documento"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        data(rs.getDate("data_nascimento")),
                        rs.getString("observacoes"),
                        rs.getBoolean("ativo"),
                        instante(rs.getTimestamp("criado_em")),
                        instante(rs.getTimestamp("atualizado_em"))
                ),
                empresaId,
                email.trim()
        );
    }

    private java.time.LocalDate data(Date data) {
        return data == null ? null : data.toLocalDate();
    }

    private java.time.Instant instante(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
