package br.com.atendepro.modules.adminsaas.adapter.out.persistence;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.adminsaas.application.port.out.AtualizarBloqueioEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.ContarUsuariosEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.ListarEmpresasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasResumoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcAdminSaasEmpresaAdapter implements
        ListarEmpresasAdminSaasPort,
        CarregarEmpresaAdminSaasPort,
        AtualizarBloqueioEmpresaAdminSaasPort,
        ContarUsuariosEmpresaAdminSaasPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAdminSaasEmpresaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ResultadoPaginado<EmpresaAdminSaasResumoResult> listarEmpresas(Paginacao paginacao, String busca) {
        var parametros = new ArrayList<>();
        String filtroBusca = montarFiltroBusca(busca, parametros);

        Long total = jdbcTemplate.queryForObject(
                "select count(*) from empresas " + filtroBusca,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var empresas = jdbcTemplate.query(
                """
                select id, nome_fantasia, documento, email, ativo, criado_em
                from empresas
                %s
                order by criado_em desc
                limit ? offset ?
                """.formatted(filtroBusca),
                (rs, rowNum) -> new EmpresaAdminSaasResumoResult(
                        rs.getObject("id", UUID.class),
                        rs.getString("nome_fantasia"),
                        rs.getString("documento"),
                        rs.getString("email"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em").toInstant()
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(empresas, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    @Override
    public Optional<EmpresaAdminSaasDetalheResult> carregarEmpresa(UUID empresaId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, nome_fantasia, razao_social, documento, email, telefone, ativo, criado_em
                    from empresas
                    where id = ?
                    """,
                    (rs, rowNum) -> new EmpresaAdminSaasDetalheResult(
                            rs.getObject("id", UUID.class),
                            rs.getString("nome_fantasia"),
                            rs.getString("razao_social"),
                            rs.getString("documento"),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant()
                    ),
                    empresaId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<EmpresaAdminSaasDetalheResult> atualizarBloqueioEmpresa(UUID empresaId, boolean bloqueada) {
        int alterados = jdbcTemplate.update(
                "update empresas set ativo = ?, atualizado_em = now() where id = ?",
                !bloqueada,
                empresaId
        );
        if (alterados == 0) {
            return Optional.empty();
        }
        return carregarEmpresa(empresaId);
    }

    @Override
    public long contarUsuariosVinculados(UUID empresaId) {
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from auth_usuarios where empresa_id = ?",
                Long.class,
                empresaId
        );
        return total == null ? 0 : total;
    }

    private String montarFiltroBusca(String busca, ArrayList<Object> parametros) {
        if (busca == null || busca.isBlank()) {
            return "";
        }
        String termo = "%" + busca.trim().toLowerCase() + "%";
        parametros.add(termo);
        parametros.add(termo);
        parametros.add(termo);
        return """
                where lower(nome_fantasia) like ?
                   or lower(documento) like ?
                   or lower(coalesce(email, '')) like ?
                """;
    }
}
