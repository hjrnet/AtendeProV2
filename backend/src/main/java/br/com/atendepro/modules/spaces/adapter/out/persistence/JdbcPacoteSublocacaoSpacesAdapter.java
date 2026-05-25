package br.com.atendepro.modules.spaces.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.spaces.application.port.out.CarregarPacoteSublocacaoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.ListarPacotesSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.SalvarPacoteSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcPacoteSublocacaoSpacesAdapter implements
        SalvarPacoteSublocacaoSpacesPort,
        CarregarPacoteSublocacaoSpacesPorIdPort,
        ListarPacotesSublocacaoSpacesPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPacoteSublocacaoSpacesAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarPacote(PacoteSublocacaoSpaces pacote) {
        jdbcTemplate.update(
                """
                insert into spaces_pacotes_sublocacao (
                    id, empresa_id, recurso_id, nome, tipo, descricao, duracao_horas,
                    valor_fixo, percentual_receita, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                pacote.id(),
                pacote.empresaId(),
                pacote.recursoId(),
                pacote.nome(),
                pacote.tipo().name(),
                pacote.descricao(),
                pacote.duracaoHoras(),
                pacote.valorFixo(),
                pacote.percentualReceita(),
                pacote.ativo(),
                Timestamp.from(pacote.criadoEm()),
                Timestamp.from(pacote.atualizadoEm())
        );
    }

    @Override
    public Optional<PacoteSublocacaoSpaces> carregarPacotePorId(UUID pacoteId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, recurso_id, nome, tipo, descricao, duracao_horas,
                           valor_fixo, percentual_receita, ativo, criado_em, atualizado_em
                    from spaces_pacotes_sublocacao
                    where id = ?
                    """,
                    (rs, rowNum) -> new PacoteSublocacaoSpaces(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("recurso_id", UUID.class),
                            rs.getString("nome"),
                            TipoPacoteSublocacaoSpaces.deCodigo(rs.getString("tipo")),
                            rs.getString("descricao"),
                            rs.getBigDecimal("duracao_horas"),
                            rs.getBigDecimal("valor_fixo"),
                            rs.getBigDecimal("percentual_receita"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant(),
                            rs.getTimestamp("atualizado_em").toInstant()
                    ),
                    pacoteId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<PacoteSublocacaoSpaces> listarPacotes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            UUID recursoId,
            TipoPacoteSublocacaoSpaces tipo,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, recursoId, tipo, ativo, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from spaces_pacotes_sublocacao " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var pacotes = jdbcTemplate.query(
                """
                select id, empresa_id, recurso_id, nome, tipo, descricao, duracao_horas,
                       valor_fixo, percentual_receita, ativo, criado_em, atualizado_em
                from spaces_pacotes_sublocacao
                %s
                order by nome asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> new PacoteSublocacaoSpaces(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("recurso_id", UUID.class),
                        rs.getString("nome"),
                        TipoPacoteSublocacaoSpaces.deCodigo(rs.getString("tipo")),
                        rs.getString("descricao"),
                        rs.getBigDecimal("duracao_horas"),
                        rs.getBigDecimal("valor_fixo"),
                        rs.getBigDecimal("percentual_receita"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(pacotes, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            UUID recursoId,
            TipoPacoteSublocacaoSpaces tipo,
            Boolean ativo,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(nome) like ? or lower(descricao) like ?)");
            parametros.add(termo);
            parametros.add(termo);
        }
        if (recursoId != null) {
            filtro.append(" and recurso_id = ?");
            parametros.add(recursoId);
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
