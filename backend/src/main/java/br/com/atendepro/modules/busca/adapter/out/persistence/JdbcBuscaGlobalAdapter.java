package br.com.atendepro.modules.busca.adapter.out.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.busca.application.port.out.BuscarGlobalPort;
import br.com.atendepro.modules.busca.application.result.ResultadoBuscaGlobalItemResult;
import br.com.atendepro.modules.busca.domain.model.TipoResultadoBusca;

@Repository
@Profile("!test")
public class JdbcBuscaGlobalAdapter implements BuscarGlobalPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBuscaGlobalAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ResultadoBuscaGlobalItemResult> buscarGlobal(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limitePorTipo
    ) {
        var resultados = new ArrayList<ResultadoBuscaGlobalItemResult>();
        resultados.addAll(buscarClientesPacientes(empresaId, busca, categoria, status, limitePorTipo));
        resultados.addAll(buscarAgenda(empresaId, busca, categoria, status, limitePorTipo));
        resultados.addAll(buscarServicos(empresaId, busca, categoria, status, limitePorTipo));
        resultados.addAll(buscarCustosGerais(empresaId, busca, categoria, status, limitePorTipo));
        resultados.addAll(buscarCustosPessoais(empresaId, busca, categoria, status, limitePorTipo));
        resultados.addAll(buscarProdutosEstoque(empresaId, busca, categoria, status, limitePorTipo));
        resultados.addAll(buscarEquipamentos(empresaId, busca, categoria, status, limitePorTipo));
        return resultados;
    }

    private List<ResultadoBuscaGlobalItemResult> buscarClientesPacientes(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limite
    ) {
        Boolean ativo = ativoPorStatus(status);
        if (statusIncompativelComAtivo(status, ativo)) {
            return List.of();
        }
        var parametros = new ArrayList<>();
        var sql = new StringBuilder("""
                select id, nome as titulo, coalesce(email, '') as descricao, area as categoria,
                       case when ativo then 'ATIVO' else 'INATIVO' end as status,
                       '/app/clientes-pacientes/' || id as destino, criado_em as data_referencia
                from clientes_pacientes
                where empresa_id = ?
                """);
        parametros.add(empresaId);
        aplicarBusca(sql, parametros, busca, "nome", "email", "documento");
        aplicarCategoria(sql, parametros, categoria, "area");
        aplicarAtivo(sql, parametros, ativo);
        return consultar(sql, parametros, TipoResultadoBusca.CLIENTE_PACIENTE, limite);
    }

    private List<ResultadoBuscaGlobalItemResult> buscarAgenda(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limite
    ) {
        var parametros = new ArrayList<>();
        var sql = new StringBuilder("""
                select id, tipo || ' - ' || coalesce(profissional_nome, sala, 'Agenda') as titulo,
                       coalesce(observacoes, '') as descricao, tipo as categoria, status,
                       '/app/agenda/' || id as destino, inicio as data_referencia
                from agenda_compromissos
                where empresa_id = ?
                """);
        parametros.add(empresaId);
        aplicarBusca(sql, parametros, busca, "profissional_nome", "sala", "observacoes", "tipo");
        aplicarCategoria(sql, parametros, categoria, "tipo");
        aplicarStatusTexto(sql, parametros, status, "status");
        return consultar(sql, parametros, TipoResultadoBusca.COMPROMISSO_AGENDA, limite);
    }

    private List<ResultadoBuscaGlobalItemResult> buscarServicos(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limite
    ) {
        Boolean ativo = ativoPorStatus(status);
        if (statusIncompativelComAtivo(status, ativo)) {
            return List.of();
        }
        var parametros = new ArrayList<>();
        var sql = new StringBuilder("""
                select id, nome as titulo, coalesce(descricao, '') as descricao, area as categoria,
                       case when ativo then 'ATIVO' else 'INATIVO' end as status,
                       '/app/servicos-procedimentos/' || id as destino, criado_em as data_referencia
                from servicos_procedimentos
                where empresa_id = ?
                """);
        parametros.add(empresaId);
        aplicarBusca(sql, parametros, busca, "nome", "descricao");
        aplicarCategoria(sql, parametros, categoria, "area");
        aplicarAtivo(sql, parametros, ativo);
        return consultar(sql, parametros, TipoResultadoBusca.SERVICO_PROCEDIMENTO, limite);
    }

    private List<ResultadoBuscaGlobalItemResult> buscarCustosGerais(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limite
    ) {
        Boolean ativo = ativoPorStatus(status);
        if (statusIncompativelComAtivo(status, ativo)) {
            return List.of();
        }
        var parametros = new ArrayList<>();
        var sql = new StringBuilder("""
                select id, descricao as titulo, tipo as descricao, coalesce(categoria, tipo) as categoria,
                       case when ativo then 'ATIVO' else 'INATIVO' end as status,
                       '/app/custos/gerais/' || id as destino, criado_em as data_referencia
                from custos_gerais
                where empresa_id = ?
                """);
        parametros.add(empresaId);
        aplicarBusca(sql, parametros, busca, "descricao", "categoria", "tipo");
        aplicarCategoria(sql, parametros, categoria, "categoria", "tipo");
        aplicarAtivo(sql, parametros, ativo);
        return consultar(sql, parametros, TipoResultadoBusca.CUSTO, limite);
    }

    private List<ResultadoBuscaGlobalItemResult> buscarCustosPessoais(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limite
    ) {
        Boolean ativo = ativoPorStatus(status);
        if (statusIncompativelComAtivo(status, ativo)) {
            return List.of();
        }
        var parametros = new ArrayList<>();
        var sql = new StringBuilder("""
                select id, descricao as titulo, periodicidade as descricao, tipo as categoria,
                       case when ativo then 'ATIVO' else 'INATIVO' end as status,
                       '/app/custos/alimentacao-transporte/' || id as destino, criado_em as data_referencia
                from custos_alimentacao_transporte
                where empresa_id = ?
                """);
        parametros.add(empresaId);
        aplicarBusca(sql, parametros, busca, "descricao", "tipo", "periodicidade");
        aplicarCategoria(sql, parametros, categoria, "tipo");
        aplicarAtivo(sql, parametros, ativo);
        return consultar(sql, parametros, TipoResultadoBusca.CUSTO, limite);
    }

    private List<ResultadoBuscaGlobalItemResult> buscarProdutosEstoque(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limite
    ) {
        Boolean ativo = ativoPorStatus(status);
        if (statusIncompativelComAtivo(status, ativo)) {
            return List.of();
        }
        var parametros = new ArrayList<>();
        var sql = new StringBuilder("""
                select id, nome as titulo, coalesce(lote, unidade) as descricao, coalesce(categoria, 'Estoque') as categoria,
                       case when ativo then 'ATIVO' else 'INATIVO' end as status,
                       '/app/estoque/produtos/' || id as destino, criado_em as data_referencia
                from estoque_produtos
                where empresa_id = ?
                """);
        parametros.add(empresaId);
        aplicarBusca(sql, parametros, busca, "nome", "categoria", "lote", "unidade");
        aplicarCategoria(sql, parametros, categoria, "categoria");
        aplicarAtivo(sql, parametros, ativo);
        return consultar(sql, parametros, TipoResultadoBusca.PRODUTO_ESTOQUE, limite);
    }

    private List<ResultadoBuscaGlobalItemResult> buscarEquipamentos(
            UUID empresaId,
            String busca,
            String categoria,
            String status,
            int limite
    ) {
        Boolean ativo = ativoPorStatus(status);
        if (statusIncompativelComAtivo(status, ativo)) {
            return List.of();
        }
        var parametros = new ArrayList<>();
        var sql = new StringBuilder("""
                select id, nome as titulo, coalesce(marca, modelo, numero_serie, '') as descricao,
                       coalesce(categoria, 'Equipamento') as categoria,
                       case when ativo then 'ATIVO' else 'INATIVO' end as status,
                       '/app/equipamentos/' || id as destino, criado_em as data_referencia
                from equipamentos
                where empresa_id = ?
                """);
        parametros.add(empresaId);
        aplicarBusca(sql, parametros, busca, "nome", "categoria", "marca", "modelo", "numero_serie");
        aplicarCategoria(sql, parametros, categoria, "categoria");
        aplicarAtivo(sql, parametros, ativo);
        return consultar(sql, parametros, TipoResultadoBusca.EQUIPAMENTO, limite);
    }

    private void aplicarBusca(StringBuilder sql, ArrayList<Object> parametros, String busca, String... campos) {
        if (busca == null || busca.isBlank()) {
            return;
        }
        String termo = "%" + busca.trim().toLowerCase() + "%";
        sql.append(" and (");
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sql.append(" or ");
            }
            sql.append("lower(coalesce(").append(campos[i]).append(", '')) like ?");
            parametros.add(termo);
        }
        sql.append(")");
    }

    private void aplicarCategoria(StringBuilder sql, ArrayList<Object> parametros, String categoria, String... campos) {
        if (categoria == null || categoria.isBlank()) {
            return;
        }
        sql.append(" and (");
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sql.append(" or ");
            }
            sql.append("lower(coalesce(").append(campos[i]).append(", '')) = ?");
            parametros.add(categoria.trim().toLowerCase());
        }
        sql.append(")");
    }

    private void aplicarStatusTexto(StringBuilder sql, ArrayList<Object> parametros, String status, String campo) {
        if (status == null || status.isBlank()) {
            return;
        }
        sql.append(" and lower(").append(campo).append(") = ?");
        parametros.add(status.trim().toLowerCase());
    }

    private void aplicarAtivo(StringBuilder sql, ArrayList<Object> parametros, Boolean ativo) {
        if (ativo == null) {
            return;
        }
        sql.append(" and ativo = ?");
        parametros.add(ativo);
    }

    private Boolean ativoPorStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return switch (status.trim().toUpperCase()) {
            case "ATIVO" -> true;
            case "INATIVO" -> false;
            default -> null;
        };
    }

    private boolean statusIncompativelComAtivo(String status, Boolean ativo) {
        return status != null && !status.isBlank() && ativo == null;
    }

    private List<ResultadoBuscaGlobalItemResult> consultar(
            StringBuilder sql,
            ArrayList<Object> parametros,
            TipoResultadoBusca tipo,
            int limite
    ) {
        sql.append(" order by data_referencia desc limit ?");
        parametros.add(limite);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapearItem(rs, tipo), parametros.toArray());
    }

    private ResultadoBuscaGlobalItemResult mapearItem(ResultSet rs, TipoResultadoBusca tipo) throws SQLException {
        var dataReferencia = rs.getTimestamp("data_referencia");
        return new ResultadoBuscaGlobalItemResult(
                rs.getObject("id", UUID.class),
                tipo,
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("categoria"),
                rs.getString("status"),
                rs.getString("destino"),
                dataReferencia == null ? null : dataReferencia.toInstant()
        );
    }
}
