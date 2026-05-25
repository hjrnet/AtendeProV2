package br.com.atendepro.modules.custo.adapter.out.persistence;

import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.custo.application.port.out.CarregarCustoGeralPorIdPort;
import br.com.atendepro.modules.custo.application.port.out.ListarCustosGeraisPort;
import br.com.atendepro.modules.custo.application.port.out.SalvarCustoGeralPort;
import br.com.atendepro.modules.custo.domain.model.CustoGeral;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcCustoGeralAdapter implements SalvarCustoGeralPort, CarregarCustoGeralPorIdPort, ListarCustosGeraisPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCustoGeralAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarCustoGeral(CustoGeral custo) {
        jdbcTemplate.update(
                """
                insert into custos_gerais (
                    id, empresa_id, descricao, tipo, categoria, valor, competencia, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                custo.id(),
                custo.empresaId(),
                custo.descricao(),
                custo.tipo().name(),
                custo.categoria(),
                custo.valor(),
                custo.competencia() == null ? null : custo.competencia().toString(),
                custo.ativo(),
                Timestamp.from(custo.criadoEm()),
                Timestamp.from(custo.atualizadoEm())
        );
    }

    @Override
    public Optional<CustoGeral> carregarCustoGeralPorId(UUID custoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, descricao, tipo, categoria, valor, competencia, ativo, criado_em, atualizado_em
                    from custos_gerais
                    where id = ?
                    """,
                    (rs, rowNum) -> new CustoGeral(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getString("descricao"),
                            TipoCustoGeral.valueOf(rs.getString("tipo")),
                            rs.getString("categoria"),
                            rs.getBigDecimal("valor"),
                            rs.getString("competencia") == null ? null : YearMonth.parse(rs.getString("competencia")),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant(),
                            rs.getTimestamp("atualizado_em").toInstant()
                    ),
                    custoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<CustoGeral> listarCustosGerais(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoCustoGeral tipo,
            String categoria,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, tipo, categoria, ativo, parametros);
        Long total = jdbcTemplate.queryForObject("select count(*) from custos_gerais " + filtro, Long.class, parametros.toArray());

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var custos = jdbcTemplate.query(
                """
                select id, empresa_id, descricao, tipo, categoria, valor, competencia, ativo, criado_em, atualizado_em
                from custos_gerais
                %s
                order by criado_em desc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> new CustoGeral(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("descricao"),
                        TipoCustoGeral.valueOf(rs.getString("tipo")),
                        rs.getString("categoria"),
                        rs.getBigDecimal("valor"),
                        rs.getString("competencia") == null ? null : YearMonth.parse(rs.getString("competencia")),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(custos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(UUID empresaId, String busca, TipoCustoGeral tipo, String categoria, Boolean ativo, ArrayList<Object> parametros) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and lower(descricao) like ?");
            parametros.add(termo);
        }
        if (tipo != null) {
            filtro.append(" and tipo = ?");
            parametros.add(tipo.name());
        }
        if (categoria != null && !categoria.isBlank()) {
            filtro.append(" and lower(categoria) = ?");
            parametros.add(categoria.trim().toLowerCase());
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }
}
