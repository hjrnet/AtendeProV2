package br.com.atendepro.modules.estoque.adapter.out.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.estoque.application.port.out.CarregarProdutoEstoquePorIdPort;
import br.com.atendepro.modules.estoque.application.port.out.ListarProdutosEstoquePort;
import br.com.atendepro.modules.estoque.application.port.out.SalvarProdutoEstoquePort;
import br.com.atendepro.modules.estoque.domain.model.ProdutoEstoque;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcProdutoEstoqueAdapter implements
        SalvarProdutoEstoquePort,
        CarregarProdutoEstoquePorIdPort,
        ListarProdutosEstoquePort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcProdutoEstoqueAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarProdutoEstoque(ProdutoEstoque produto) {
        jdbcTemplate.update(
                """
                insert into estoque_produtos (
                    id, empresa_id, nome, categoria, lote, validade, unidade,
                    quantidade_atual, custo_unitario, estoque_minimo, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                produto.id(),
                produto.empresaId(),
                produto.nome(),
                produto.categoria(),
                produto.lote(),
                produto.validade(),
                produto.unidade(),
                produto.quantidadeAtual(),
                produto.custoUnitario(),
                produto.estoqueMinimo(),
                produto.ativo(),
                Timestamp.from(produto.criadoEm()),
                Timestamp.from(produto.atualizadoEm())
        );
    }

    @Override
    public Optional<ProdutoEstoque> carregarProdutoEstoquePorId(UUID produtoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, nome, categoria, lote, validade, unidade,
                           quantidade_atual, custo_unitario, estoque_minimo, ativo, criado_em, atualizado_em
                    from estoque_produtos
                    where id = ?
                    """,
                    this::mapearProdutoEstoque,
                    produtoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<ProdutoEstoque> listarProdutosEstoque(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate vencendoAte
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, categoria, ativo, vencendoAte, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from estoque_produtos " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var produtos = jdbcTemplate.query(
                """
                select id, empresa_id, nome, categoria, lote, validade, unidade,
                       quantidade_atual, custo_unitario, estoque_minimo, ativo, criado_em, atualizado_em
                from estoque_produtos
                %s
                order by nome asc
                limit ? offset ?
                """.formatted(filtro),
                this::mapearProdutoEstoque,
                parametros.toArray()
        );
        return new ResultadoPaginado<>(produtos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate vencendoAte,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(nome) like ? or lower(coalesce(lote, '')) like ?)");
            parametros.add(termo);
            parametros.add(termo);
        }
        if (categoria != null && !categoria.isBlank()) {
            filtro.append(" and lower(coalesce(categoria, '')) = ?");
            parametros.add(categoria.trim().toLowerCase());
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        if (vencendoAte != null) {
            filtro.append(" and validade is not null and validade <= ?");
            parametros.add(vencendoAte);
        }
        return filtro.toString();
    }

    private ProdutoEstoque mapearProdutoEstoque(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Date validade = rs.getDate("validade");
        return new ProdutoEstoque(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getString("lote"),
                validade == null ? null : validade.toLocalDate(),
                rs.getString("unidade"),
                rs.getBigDecimal("quantidade_atual"),
                rs.getBigDecimal("custo_unitario"),
                rs.getBigDecimal("estoque_minimo"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }
}
