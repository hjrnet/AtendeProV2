package br.com.atendepro.modules.spaces.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.spaces.application.port.out.CarregarRecursoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.ListarRecursosSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.SalvarRecursoSpacesPort;
import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcRecursoSpacesAdapter implements
        SalvarRecursoSpacesPort,
        CarregarRecursoSpacesPorIdPort,
        ListarRecursosSpacesPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRecursoSpacesAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarRecurso(RecursoSpaces recurso) {
        jdbcTemplate.update(
                """
                insert into spaces_recursos (
                    id, empresa_id, nome, tipo, descricao, capacidade_pessoas, localizacao,
                    ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                recurso.id(),
                recurso.empresaId(),
                recurso.nome(),
                recurso.tipo().name(),
                recurso.descricao(),
                recurso.capacidadePessoas(),
                recurso.localizacao(),
                recurso.ativo(),
                Timestamp.from(recurso.criadoEm()),
                Timestamp.from(recurso.atualizadoEm())
        );
    }

    @Override
    public Optional<RecursoSpaces> carregarRecursoPorId(UUID recursoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, nome, tipo, descricao, capacidade_pessoas, localizacao,
                           ativo, criado_em, atualizado_em
                    from spaces_recursos
                    where id = ?
                    """,
                    (rs, rowNum) -> new RecursoSpaces(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getString("nome"),
                            TipoRecursoSpaces.deCodigo(rs.getString("tipo")),
                            rs.getString("descricao"),
                            rs.getInt("capacidade_pessoas"),
                            rs.getString("localizacao"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant(),
                            rs.getTimestamp("atualizado_em").toInstant()
                    ),
                    recursoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<RecursoSpaces> listarRecursos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoRecursoSpaces tipo,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, tipo, ativo, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from spaces_recursos " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var recursos = jdbcTemplate.query(
                """
                select id, empresa_id, nome, tipo, descricao, capacidade_pessoas, localizacao,
                       ativo, criado_em, atualizado_em
                from spaces_recursos
                %s
                order by nome asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> new RecursoSpaces(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("nome"),
                        TipoRecursoSpaces.deCodigo(rs.getString("tipo")),
                        rs.getString("descricao"),
                        rs.getInt("capacidade_pessoas"),
                        rs.getString("localizacao"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(recursos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            TipoRecursoSpaces tipo,
            Boolean ativo,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(nome) like ? or lower(descricao) like ? or lower(localizacao) like ?)");
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        if (tipo != null) {
            filtro.append(" and tipo = ?");
            parametros.add(tipo.name());
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }
}
