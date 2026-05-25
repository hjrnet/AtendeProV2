package br.com.atendepro.modules.plano.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.atendepro.modules.plano.application.port.out.AtualizarPlanoPort;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorCodigoPort;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.modules.plano.application.port.out.ListarPlanosPort;
import br.com.atendepro.modules.plano.application.port.out.SalvarPlanoPort;
import br.com.atendepro.modules.plano.domain.model.ModuloPlano;
import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcPlanoAdapter implements
        SalvarPlanoPort,
        AtualizarPlanoPort,
        CarregarPlanoPorIdPort,
        CarregarPlanoPorCodigoPort,
        ListarPlanosPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPlanoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void salvarPlano(PlanoAssinatura plano) {
        jdbcTemplate.update(
                """
                insert into planos (
                    id, codigo, nome, descricao, valor_mensal, limite_usuarios, limite_clientes,
                    limite_profissionais, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                plano.id(),
                plano.codigo(),
                plano.nome(),
                plano.descricao(),
                plano.valorMensal(),
                plano.limiteUsuarios(),
                plano.limiteClientes(),
                plano.limiteProfissionais(),
                plano.ativo(),
                Timestamp.from(plano.criadoEm()),
                Timestamp.from(plano.atualizadoEm())
        );
        salvarModulos(plano);
    }

    @Override
    @Transactional
    public void atualizarPlano(PlanoAssinatura plano) {
        jdbcTemplate.update(
                """
                update planos
                set codigo = ?,
                    nome = ?,
                    descricao = ?,
                    valor_mensal = ?,
                    limite_usuarios = ?,
                    limite_clientes = ?,
                    limite_profissionais = ?,
                    ativo = ?,
                    atualizado_em = ?
                where id = ?
                """,
                plano.codigo(),
                plano.nome(),
                plano.descricao(),
                plano.valorMensal(),
                plano.limiteUsuarios(),
                plano.limiteClientes(),
                plano.limiteProfissionais(),
                plano.ativo(),
                Timestamp.from(plano.atualizadoEm()),
                plano.id()
        );
        jdbcTemplate.update("delete from plano_modulos where plano_id = ?", plano.id());
        salvarModulos(plano);
    }

    @Override
    public Optional<PlanoAssinatura> carregarPlanoPorId(UUID planoId) {
        return carregarPlano(
                """
                select id, codigo, nome, descricao, valor_mensal, limite_usuarios, limite_clientes,
                       limite_profissionais, ativo, criado_em, atualizado_em
                from planos
                where id = ?
                """,
                planoId
        );
    }

    @Override
    public Optional<PlanoAssinatura> carregarPlanoPorCodigo(String codigo) {
        return carregarPlano(
                """
                select id, codigo, nome, descricao, valor_mensal, limite_usuarios, limite_clientes,
                       limite_profissionais, ativo, criado_em, atualizado_em
                from planos
                where codigo = ?
                """,
                PlanoAssinatura.normalizarCodigo(codigo)
        );
    }

    @Override
    public ResultadoPaginado<PlanoAssinatura> listarPlanos(Paginacao paginacao, String busca, Boolean ativo) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(busca, ativo, parametros);

        Long total = jdbcTemplate.queryForObject(
                "select count(*) from planos " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var planos = jdbcTemplate.query(
                """
                select id, codigo, nome, descricao, valor_mensal, limite_usuarios, limite_clientes,
                       limite_profissionais, ativo, criado_em, atualizado_em
                from planos
                %s
                order by valor_mensal asc, nome asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> new PlanoAssinatura(
                        rs.getObject("id", UUID.class),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("valor_mensal"),
                        rs.getInt("limite_usuarios"),
                        rs.getInt("limite_clientes"),
                        rs.getInt("limite_profissionais"),
                        rs.getBoolean("ativo"),
                        carregarModulos(rs.getObject("id", UUID.class)),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(planos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private Optional<PlanoAssinatura> carregarPlano(String sql, Object parametro) {
        try {
            PlanoAssinatura plano = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new PlanoAssinatura(
                            rs.getObject("id", UUID.class),
                            rs.getString("codigo"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getBigDecimal("valor_mensal"),
                            rs.getInt("limite_usuarios"),
                            rs.getInt("limite_clientes"),
                            rs.getInt("limite_profissionais"),
                            rs.getBoolean("ativo"),
                            carregarModulos(rs.getObject("id", UUID.class)),
                            rs.getTimestamp("criado_em").toInstant(),
                            rs.getTimestamp("atualizado_em").toInstant()
                    ),
                    parametro
            );
            return Optional.of(plano);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private Set<ModuloPlano> carregarModulos(UUID planoId) {
        return jdbcTemplate.query(
                "select modulo from plano_modulos where plano_id = ? order by modulo",
                (rs, rowNum) -> ModuloPlano.deCodigo(rs.getString("modulo")),
                planoId
        ).stream().collect(Collectors.toUnmodifiableSet());
    }

    private void salvarModulos(PlanoAssinatura plano) {
        plano.modulos().forEach(modulo -> jdbcTemplate.update(
                "insert into plano_modulos (plano_id, modulo) values (?, ?)",
                plano.id(),
                modulo.codigo()
        ));
    }

    private String montarFiltro(String busca, Boolean ativo, ArrayList<Object> parametros) {
        var filtro = new StringBuilder("where 1 = 1");
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(codigo) like ? or lower(nome) like ?)");
            parametros.add(termo);
            parametros.add(termo);
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }
}
