package br.com.atendepro.modules.auth.adapter.out.persistence;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.auth.application.port.out.CarregarTokenRecuperacaoSenhaAtivoPort;
import br.com.atendepro.modules.auth.application.port.out.MarcarTokenRecuperacaoSenhaUtilizadoPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarTokenRecuperacaoSenhaPort;
import br.com.atendepro.modules.auth.domain.model.TokenRecuperacaoSenha;

@Repository
@Profile("!test")
public class JdbcTokenRecuperacaoSenhaAdapter implements
        CarregarTokenRecuperacaoSenhaAtivoPort,
        SalvarTokenRecuperacaoSenhaPort,
        MarcarTokenRecuperacaoSenhaUtilizadoPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTokenRecuperacaoSenhaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<TokenRecuperacaoSenha> carregarTokenRecuperacaoSenhaAtivo(String tokenHash, Instant agora) {
        try {
            TokenRecuperacaoSenha token = jdbcTemplate.queryForObject(
                    """
                    select id, usuario_id, token_hash, expira_em, utilizado, criado_em
                    from auth_password_reset_tokens
                    where token_hash = ?
                      and utilizado = false
                      and expira_em > ?
                    """,
                    (rs, rowNum) -> new TokenRecuperacaoSenha(
                            rs.getObject("id", UUID.class),
                            rs.getObject("usuario_id", UUID.class),
                            rs.getString("token_hash"),
                            rs.getTimestamp("expira_em").toInstant(),
                            rs.getBoolean("utilizado"),
                            rs.getTimestamp("criado_em").toInstant()
                    ),
                    tokenHash,
                    Timestamp.from(agora)
            );
            return Optional.of(token);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void salvarTokenRecuperacaoSenha(TokenRecuperacaoSenha token) {
        jdbcTemplate.update(
                """
                insert into auth_password_reset_tokens (id, usuario_id, token_hash, expira_em, utilizado, criado_em)
                values (?, ?, ?, ?, ?, ?)
                """,
                token.id(),
                token.usuarioId(),
                token.tokenHash(),
                Timestamp.from(token.expiraEm()),
                token.utilizado(),
                Timestamp.from(token.criadoEm())
        );
    }

    @Override
    public void marcarTokenRecuperacaoSenhaUtilizado(UUID tokenId, Instant utilizadoEm) {
        jdbcTemplate.update(
                """
                update auth_password_reset_tokens
                   set utilizado = true,
                       utilizado_em = ?
                 where id = ?
                """,
                Timestamp.from(utilizadoEm),
                tokenId
        );
    }
}
