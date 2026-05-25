package br.com.atendepro.modules.empresa.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorDocumentoPort;
import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorIdPort;
import br.com.atendepro.modules.empresa.application.port.out.ListarEmpresasPort;
import br.com.atendepro.modules.empresa.application.port.out.SalvarEmpresaPort;
import br.com.atendepro.modules.empresa.domain.model.DocumentoEmpresa;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcEmpresaAdapter implements
        CarregarEmpresaPorDocumentoPort,
        CarregarEmpresaPorIdPort,
        ListarEmpresasPort,
        SalvarEmpresaPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcEmpresaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<EmpresaTenant> carregarEmpresaPorDocumento(DocumentoEmpresa documento) {
        return carregarEmpresa(
                """
                select id, nome_fantasia, razao_social, documento, email, telefone, ativo, criado_em
                from empresas
                where documento = ?
                """,
                documento.valor()
        );
    }

    @Override
    public Optional<EmpresaTenant> carregarEmpresaPorId(UUID empresaId) {
        return carregarEmpresa(
                """
                select id, nome_fantasia, razao_social, documento, email, telefone, ativo, criado_em
                from empresas
                where id = ?
                """,
                empresaId
        );
    }

    @Override
    public ResultadoPaginado<EmpresaTenant> listarEmpresas(Paginacao paginacao) {
        long total = jdbcTemplate.queryForObject("select count(*) from empresas", Long.class);
        var empresas = jdbcTemplate.query(
                """
                select id, nome_fantasia, razao_social, documento, email, telefone, ativo, criado_em
                from empresas
                order by criado_em desc
                limit ? offset ?
                """,
                (rs, rowNum) -> new EmpresaTenant(
                        rs.getObject("id", UUID.class),
                        rs.getString("nome_fantasia"),
                        rs.getString("razao_social"),
                        DocumentoEmpresa.de(rs.getString("documento")),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em").toInstant()
                ),
                paginacao.tamanho(),
                paginacao.offset()
        );
        return new ResultadoPaginado<>(empresas, total, paginacao.pagina(), paginacao.tamanho());
    }

    @Override
    public void salvarEmpresa(EmpresaTenant empresa) {
        jdbcTemplate.update(
                """
                insert into empresas (id, nome_fantasia, razao_social, documento, email, telefone, ativo, criado_em, atualizado_em)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                empresa.id(),
                empresa.nomeFantasia(),
                empresa.razaoSocial(),
                empresa.documento().valor(),
                empresa.email(),
                empresa.telefone(),
                empresa.ativo(),
                Timestamp.from(empresa.criadoEm()),
                Timestamp.from(empresa.criadoEm())
        );
    }

    private Optional<EmpresaTenant> carregarEmpresa(String sql, Object parametro) {
        try {
            EmpresaTenant empresa = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new EmpresaTenant(
                            rs.getObject("id", UUID.class),
                            rs.getString("nome_fantasia"),
                            rs.getString("razao_social"),
                            DocumentoEmpresa.de(rs.getString("documento")),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em").toInstant()
                    ),
                    parametro
            );
            return Optional.of(empresa);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
