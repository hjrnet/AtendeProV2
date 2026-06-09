package br.com.atendepro.modules.nutri.adapter.out.persistence;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.nutri.application.port.out.BancoAlimentosNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.BancoAlimentosNutriProPort.NovoItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.ItemBancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.OrigemItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemBancoAlimentosNutriPro;

@Repository
@Profile("!test")
public class JdbcBancoAlimentosNutriProAdapter implements BancoAlimentosNutriProPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBancoAlimentosNutriProAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ItemBancoAlimentosNutriProResult> listarItens(
            UUID empresaId,
            String busca,
            TipoItemBancoAlimentosNutriPro tipoItem,
            OrigemItemBancoAlimentosNutriPro origem,
            Boolean ativo
    ) {
        StringBuilder sql = new StringBuilder("""
                select *
                from nutri_banco_alimentos
                where (empresa_id is null or empresa_id = ?)
                """);
        List<Object> parametros = new ArrayList<>();
        parametros.add(empresaId);

        if (ativo != null) {
            sql.append(" and ativo = ?");
            parametros.add(ativo);
        }
        if (tipoItem != null) {
            sql.append(" and tipo_item = ?");
            parametros.add(tipoItem.name());
        }
        if (origem != null) {
            sql.append(" and origem = ?");
            parametros.add(origem.name());
        }
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            sql.append("""
                     and (
                        lower(nome) like ?
                        or lower(coalesce(grupo, '')) like ?
                        or lower(coalesce(categoria_clinica, '')) like ?
                        or lower(coalesce(marca_fabricante, '')) like ?
                        or lower(coalesce(fonte_dados, '')) like ?
                     )
                    """);
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }

        sql.append("""
                 order by
                    case origem when 'PADRAO' then 0 else 1 end,
                    tipo_item,
                    nome
                 limit 80
                """);

        return jdbcTemplate.query(sql.toString(), this::mapearItem, parametros.toArray());
    }

    @Override
    public Optional<ItemBancoAlimentosNutriProResult> carregarItem(UUID empresaId, UUID itemId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_banco_alimentos
                    where id = ?
                      and (empresa_id is null or empresa_id = ?)
                    """, this::mapearItem, itemId, empresaId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public ItemBancoAlimentosNutriProResult salvarItem(NovoItemBancoAlimentosNutriPro novoItem) {
        jdbcTemplate.update("""
                insert into nutri_banco_alimentos (
                    id, empresa_id, tipo_item, origem, nome, grupo, categoria_clinica,
                    unidade_medida, quantidade_base, energia_kcal_base, proteinas_base,
                    carboidratos_base, lipidios_base, fibras_base, sodio_mg_base,
                    fonte_dados, marca_fabricante, orientacao_uso, observacoes,
                    ativo, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, ?, ?)
                """,
                novoItem.id(),
                novoItem.empresaId(),
                novoItem.tipoItem().name(),
                novoItem.origem().name(),
                novoItem.nome(),
                novoItem.grupo(),
                novoItem.categoriaClinica(),
                novoItem.unidadeMedida(),
                novoItem.quantidadeBase(),
                novoItem.energiaKcalBase(),
                novoItem.proteinasBase(),
                novoItem.carboidratosBase(),
                novoItem.lipidiosBase(),
                novoItem.fibrasBase(),
                novoItem.sodioMgBase(),
                novoItem.fonteDados(),
                novoItem.marcaFabricante(),
                novoItem.orientacaoUso(),
                novoItem.observacoes(),
                Timestamp.from(novoItem.agora()),
                Timestamp.from(novoItem.agora())
        );
        return carregarItem(novoItem.empresaId(), novoItem.id()).orElseThrow();
    }

    private ItemBancoAlimentosNutriProResult mapearItem(ResultSet rs, int rowNum) throws SQLException {
        TipoItemBancoAlimentosNutriPro tipoItem = TipoItemBancoAlimentosNutriPro.deCodigo(rs.getString("tipo_item"));
        OrigemItemBancoAlimentosNutriPro origem = OrigemItemBancoAlimentosNutriPro.deCodigo(rs.getString("origem"));
        return ItemBancoAlimentosNutriProResult.de(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                tipoItem,
                origem,
                rs.getString("nome"),
                rs.getString("grupo"),
                rs.getString("categoria_clinica"),
                rs.getString("unidade_medida"),
                bigDecimal(rs, "quantidade_base"),
                bigDecimal(rs, "energia_kcal_base"),
                bigDecimal(rs, "proteinas_base"),
                bigDecimal(rs, "carboidratos_base"),
                bigDecimal(rs, "lipidios_base"),
                bigDecimal(rs, "fibras_base"),
                bigDecimal(rs, "sodio_mg_base"),
                rs.getString("fonte_dados"),
                rs.getString("marca_fabricante"),
                rs.getString("orientacao_uso"),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private BigDecimal bigDecimal(ResultSet rs, String coluna) throws SQLException {
        return rs.getBigDecimal(coluna);
    }
}
