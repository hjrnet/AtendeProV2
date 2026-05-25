package br.com.atendepro.modules.auth.adapter.out.persistence;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.auth.application.port.out.AtualizarSenhaUsuarioPort;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorIdPort;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorEmailPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarUsuarioAutenticacaoPort;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Repository
@Profile("!test")
public class JdbcUsuarioAutenticacaoAdapter implements
        CarregarUsuarioPorEmailPort,
        CarregarUsuarioPorIdPort,
        SalvarUsuarioAutenticacaoPort,
        AtualizarSenhaUsuarioPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUsuarioAutenticacaoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<UsuarioAutenticacao> carregarUsuarioPorEmail(EmailUsuario email) {
        try {
            UsuarioAutenticacao usuario = jdbcTemplate.queryForObject(
                    """
                    select id, nome, email, senha_hash, perfis, ativo, criado_em
                    from auth_usuarios
                    where email = ?
                    """,
                    (rs, rowNum) -> new UsuarioAutenticacao(
                            rs.getObject("id", UUID.class),
                            EmailUsuario.de(rs.getString("email")),
                            rs.getString("nome"),
                            rs.getString("senha_hash"),
                            mapearPerfis(rs.getArray("perfis")),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant()
                    ),
                    email.valor()
            );
            return Optional.of(usuario);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UsuarioAutenticacao> carregarUsuarioPorId(UUID usuarioId) {
        try {
            UsuarioAutenticacao usuario = jdbcTemplate.queryForObject(
                    """
                    select id, nome, email, senha_hash, perfis, ativo, criado_em
                    from auth_usuarios
                    where id = ?
                    """,
                    (rs, rowNum) -> new UsuarioAutenticacao(
                            rs.getObject("id", UUID.class),
                            EmailUsuario.de(rs.getString("email")),
                            rs.getString("nome"),
                            rs.getString("senha_hash"),
                            mapearPerfis(rs.getArray("perfis")),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant()
                    ),
                    usuarioId
            );
            return Optional.of(usuario);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void salvarUsuarioAutenticacao(UsuarioAutenticacao usuario) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                    insert into auth_usuarios (id, nome, email, senha_hash, perfis, ativo, criado_em, atualizado_em)
                    values (?, ?, ?, ?, ?, ?, ?, ?)
                    """
            );
            Timestamp criadoEm = Timestamp.from(usuario.criadoEm());
            statement.setObject(1, usuario.id());
            statement.setString(2, usuario.nome());
            statement.setString(3, usuario.email().valor());
            statement.setString(4, usuario.senhaHash());
            statement.setArray(5, connection.createArrayOf("text", mapearPerfis(usuario.perfis())));
            statement.setBoolean(6, usuario.ativo());
            statement.setTimestamp(7, criadoEm);
            statement.setTimestamp(8, criadoEm);
            return statement;
        });
    }

    @Override
    public void atualizarSenhaUsuario(UUID usuarioId, String senhaHash) {
        jdbcTemplate.update(
                """
                update auth_usuarios
                   set senha_hash = ?,
                       atualizado_em = now()
                 where id = ?
                """,
                senhaHash,
                usuarioId
        );
    }

    private String[] mapearPerfis(Set<PerfilAcesso> perfis) {
        return perfis.stream()
                .map(Enum::name)
                .toArray(String[]::new);
    }

    private Set<PerfilAcesso> mapearPerfis(Array perfisSql) throws SQLException {
        String[] perfis = (String[]) perfisSql.getArray();
        return Arrays.stream(perfis)
                .map(PerfilAcesso::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }
}
