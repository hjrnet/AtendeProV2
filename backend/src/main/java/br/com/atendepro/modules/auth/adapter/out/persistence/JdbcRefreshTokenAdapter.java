package br.com.atendepro.modules.auth.adapter.out.persistence;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.auth.application.port.out.CarregarRefreshTokenAtivoPort;
import br.com.atendepro.modules.auth.application.port.out.RevogarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarRefreshTokenPort;
import br.com.atendepro.modules.auth.domain.model.RefreshTokenAutenticacao;

@Repository
@Profile("!test")
public class JdbcRefreshTokenAdapter implements CarregarRefreshTokenAtivoPort, SalvarRefreshTokenPort, RevogarRefreshTokenPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRefreshTokenAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<RefreshTokenAutenticacao> carregarRefreshTokenAtivo(String tokenHash, Instant agora) {
        try {
            RefreshTokenAutenticacao refreshToken = jdbcTemplate.queryForObject(
                    """
                    select id, usuario_id, token_hash, expira_em, revogado, criado_em
                    from auth_refresh_tokens
                    where token_hash = ?
                      and revogado = false
                      and expira_em > ?
                    """,
                    (rs, rowNum) -> new RefreshTokenAutenticacao(
                            rs.getObject("id", UUID.class),
                            rs.getObject("usuario_id", UUID.class),
                            rs.getString("token_hash"),
                            rs.getTimestamp("expira_em").toInstant(),
                            rs.getBoolean("revogado"),
                            rs.getTimestamp("criado_em").toInstant()
                    ),
                    tokenHash,
                    Timestamp.from(agora)
            );
            return Optional.of(refreshToken);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void salvarRefreshToken(RefreshTokenAutenticacao refreshToken) {
        jdbcTemplate.update(
                """
                insert into auth_refresh_tokens (id, usuario_id, token_hash, expira_em, revogado, criado_em)
                values (?, ?, ?, ?, ?, ?)
                """,
                refreshToken.id(),
                refreshToken.usuarioId(),
                refreshToken.tokenHash(),
                Timestamp.from(refreshToken.expiraEm()),
                refreshToken.revogado(),
                Timestamp.from(refreshToken.criadoEm())
        );
    }

    @Override
    public void revogarRefreshToken(UUID refreshTokenId, Instant revogadoEm) {
        jdbcTemplate.update(
                """
                update auth_refresh_tokens
                   set revogado = true,
                       revogado_em = ?
                 where id = ?
                """,
                Timestamp.from(revogadoEm),
                refreshTokenId
        );
    }
}
