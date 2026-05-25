package br.com.atendepro.modules.equipamento.adapter.out.persistence;

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

import br.com.atendepro.modules.equipamento.application.port.out.CarregarEquipamentoPorIdPort;
import br.com.atendepro.modules.equipamento.application.port.out.ListarEquipamentosPort;
import br.com.atendepro.modules.equipamento.application.port.out.SalvarEquipamentoPort;
import br.com.atendepro.modules.equipamento.domain.model.Equipamento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcEquipamentoAdapter implements
        SalvarEquipamentoPort,
        CarregarEquipamentoPorIdPort,
        ListarEquipamentosPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcEquipamentoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarEquipamento(Equipamento equipamento) {
        jdbcTemplate.update(
                """
                insert into equipamentos (
                    id, empresa_id, nome, categoria, marca, modelo, numero_serie, valor_aquisicao,
                    data_aquisicao, vida_util_meses, proxima_manutencao_em, descricao_manutencao,
                    ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                equipamento.id(),
                equipamento.empresaId(),
                equipamento.nome(),
                equipamento.categoria(),
                equipamento.marca(),
                equipamento.modelo(),
                equipamento.numeroSerie(),
                equipamento.valorAquisicao(),
                equipamento.dataAquisicao(),
                equipamento.vidaUtilMeses(),
                equipamento.proximaManutencaoEm(),
                equipamento.descricaoManutencao(),
                equipamento.ativo(),
                Timestamp.from(equipamento.criadoEm()),
                Timestamp.from(equipamento.atualizadoEm())
        );
    }

    @Override
    public Optional<Equipamento> carregarEquipamentoPorId(UUID equipamentoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, nome, categoria, marca, modelo, numero_serie, valor_aquisicao,
                           data_aquisicao, vida_util_meses, proxima_manutencao_em, descricao_manutencao,
                           ativo, criado_em, atualizado_em
                    from equipamentos
                    where id = ?
                    """,
                    this::mapearEquipamento,
                    equipamentoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<Equipamento> listarEquipamentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate manutencaoAte
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, categoria, ativo, manutencaoAte, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from equipamentos " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var equipamentos = jdbcTemplate.query(
                """
                select id, empresa_id, nome, categoria, marca, modelo, numero_serie, valor_aquisicao,
                       data_aquisicao, vida_util_meses, proxima_manutencao_em, descricao_manutencao,
                       ativo, criado_em, atualizado_em
                from equipamentos
                %s
                order by nome asc
                limit ? offset ?
                """.formatted(filtro),
                this::mapearEquipamento,
                parametros.toArray()
        );
        return new ResultadoPaginado<>(equipamentos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate manutencaoAte,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(nome) like ? or lower(coalesce(marca, '')) like ?"
                    + " or lower(coalesce(modelo, '')) like ? or lower(coalesce(numero_serie, '')) like ?)");
            parametros.add(termo);
            parametros.add(termo);
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
        if (manutencaoAte != null) {
            filtro.append(" and proxima_manutencao_em is not null and proxima_manutencao_em <= ?");
            parametros.add(manutencaoAte);
        }
        return filtro.toString();
    }

    private Equipamento mapearEquipamento(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Date dataAquisicao = rs.getDate("data_aquisicao");
        Date proximaManutencao = rs.getDate("proxima_manutencao_em");
        return new Equipamento(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getString("numero_serie"),
                rs.getBigDecimal("valor_aquisicao"),
                dataAquisicao == null ? null : dataAquisicao.toLocalDate(),
                rs.getInt("vida_util_meses"),
                proximaManutencao == null ? null : proximaManutencao.toLocalDate(),
                rs.getString("descricao_manutencao"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }
}
