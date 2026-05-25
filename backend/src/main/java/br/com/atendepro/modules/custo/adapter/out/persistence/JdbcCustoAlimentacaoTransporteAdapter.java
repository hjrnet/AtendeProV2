package br.com.atendepro.modules.custo.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.custo.application.port.out.ListarCustosAlimentacaoTransportePort;
import br.com.atendepro.modules.custo.application.port.out.SalvarCustoAlimentacaoTransportePort;
import br.com.atendepro.modules.custo.domain.model.CustoAlimentacaoTransporte;
import br.com.atendepro.modules.custo.domain.model.PeriodicidadeCustoPessoal;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcCustoAlimentacaoTransporteAdapter implements
        SalvarCustoAlimentacaoTransportePort,
        ListarCustosAlimentacaoTransportePort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCustoAlimentacaoTransporteAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarCustoAlimentacaoTransporte(CustoAlimentacaoTransporte custo) {
        jdbcTemplate.update(
                """
                insert into custos_alimentacao_transporte (
                    id, empresa_id, profissional_id, descricao, tipo, periodicidade, valor, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                custo.id(),
                custo.empresaId(),
                custo.profissionalId(),
                custo.descricao(),
                custo.tipo().name(),
                custo.periodicidade().name(),
                custo.valor(),
                custo.ativo(),
                Timestamp.from(custo.criadoEm()),
                Timestamp.from(custo.atualizadoEm())
        );
    }

    @Override
    public ResultadoPaginado<CustoAlimentacaoTransporte> listarCustosAlimentacaoTransporte(
            UUID empresaId,
            Paginacao paginacao,
            TipoCustoPessoal tipo,
            UUID profissionalId,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, tipo, profissionalId, ativo, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from custos_alimentacao_transporte " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var custos = jdbcTemplate.query(
                """
                select id, empresa_id, profissional_id, descricao, tipo, periodicidade, valor, ativo, criado_em, atualizado_em
                from custos_alimentacao_transporte
                %s
                order by criado_em desc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> new CustoAlimentacaoTransporte(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("profissional_id", UUID.class),
                        rs.getString("descricao"),
                        TipoCustoPessoal.valueOf(rs.getString("tipo")),
                        PeriodicidadeCustoPessoal.valueOf(rs.getString("periodicidade")),
                        rs.getBigDecimal("valor"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(custos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(UUID empresaId, TipoCustoPessoal tipo, UUID profissionalId, Boolean ativo, ArrayList<Object> parametros) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (tipo != null) {
            filtro.append(" and tipo = ?");
            parametros.add(tipo.name());
        }
        if (profissionalId != null) {
            filtro.append(" and profissional_id = ?");
            parametros.add(profissionalId);
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }
}
