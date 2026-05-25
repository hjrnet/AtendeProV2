package br.com.atendepro.modules.servico.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.servico.application.port.out.CarregarServicoProcedimentoPorIdPort;
import br.com.atendepro.modules.servico.application.port.out.ListarServicosProcedimentosPort;
import br.com.atendepro.modules.servico.application.port.out.SalvarServicoProcedimentoPort;
import br.com.atendepro.modules.servico.domain.model.ServicoProcedimento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcServicoProcedimentoAdapter implements
        SalvarServicoProcedimentoPort,
        CarregarServicoProcedimentoPorIdPort,
        ListarServicosProcedimentosPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcServicoProcedimentoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarServicoProcedimento(ServicoProcedimento servico) {
        jdbcTemplate.update(
                """
                insert into servicos_procedimentos (
                    id, empresa_id, nome, descricao, area, duracao_minutos, preco_base,
                    ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                servico.id(),
                servico.empresaId(),
                servico.nome(),
                servico.descricao(),
                servico.area().name(),
                servico.duracaoMinutos(),
                servico.precoBase(),
                servico.ativo(),
                Timestamp.from(servico.criadoEm()),
                Timestamp.from(servico.atualizadoEm())
        );
    }

    @Override
    public Optional<ServicoProcedimento> carregarServicoProcedimentoPorId(UUID servicoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, nome, descricao, area, duracao_minutos, preco_base,
                           ativo, criado_em, atualizado_em
                    from servicos_procedimentos
                    where id = ?
                    """,
                    (rs, rowNum) -> new ServicoProcedimento(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            AreaCliente.deCodigo(rs.getString("area")),
                            rs.getInt("duracao_minutos"),
                            rs.getBigDecimal("preco_base"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant(),
                            rs.getTimestamp("atualizado_em").toInstant()
                    ),
                    servicoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<ServicoProcedimento> listarServicosProcedimentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, area, ativo, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from servicos_procedimentos " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var servicos = jdbcTemplate.query(
                """
                select id, empresa_id, nome, descricao, area, duracao_minutos, preco_base,
                       ativo, criado_em, atualizado_em
                from servicos_procedimentos
                %s
                order by nome asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> new ServicoProcedimento(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        AreaCliente.deCodigo(rs.getString("area")),
                        rs.getInt("duracao_minutos"),
                        rs.getBigDecimal("preco_base"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(servicos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(UUID empresaId, String busca, AreaCliente area, Boolean ativo, ArrayList<Object> parametros) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(nome) like ? or lower(descricao) like ?)");
            parametros.add(termo);
            parametros.add(termo);
        }
        if (area != null) {
            filtro.append(" and area = ?");
            parametros.add(area.name());
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }
}
