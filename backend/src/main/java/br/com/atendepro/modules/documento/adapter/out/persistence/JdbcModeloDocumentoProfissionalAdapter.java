package br.com.atendepro.modules.documento.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.documento.application.port.out.CarregarModeloDocumentoProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.ListarModelosDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.ModeloDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcModeloDocumentoProfissionalAdapter implements
        CarregarModeloDocumentoProfissionalPorIdPort,
        ListarModelosDocumentoProfissionalPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcModeloDocumentoProfissionalAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ModeloDocumentoProfissional> carregarModeloPorId(UUID modeloId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, nome, descricao, tipo, titulo_padrao,
                           conteudo_padrao, ativo, criado_em, atualizado_em
                    from documentos_modelos_profissionais
                    where id = ?
                    """,
                    (rs, rowNum) -> mapearModelo(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getString("tipo"),
                            rs.getString("titulo_padrao"),
                            rs.getString("conteudo_padrao"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")
                    ),
                    modeloId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<ModeloDocumentoProfissional> listarModelos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, tipo, ativo, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from documentos_modelos_profissionais " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var modelos = jdbcTemplate.query(
                """
                select id, empresa_id, nome, descricao, tipo, titulo_padrao,
                       conteudo_padrao, ativo, criado_em, atualizado_em
                from documentos_modelos_profissionais
                %s
                order by case when empresa_id is null then 0 else 1 end, nome asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearModelo(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("tipo"),
                        rs.getString("titulo_padrao"),
                        rs.getString("conteudo_padrao"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em"),
                        rs.getTimestamp("atualizado_em")
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(modelos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            TipoDocumentoProfissional tipo,
            Boolean ativo,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where (empresa_id is null or empresa_id = ?)");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(nome) like ? or lower(descricao) like ? or lower(titulo_padrao) like ?)");
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        if (tipo != null) {
            filtro.append(" and tipo = ?");
            parametros.add(tipo.name());
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }

    private ModeloDocumentoProfissional mapearModelo(
            UUID id,
            UUID empresaId,
            String nome,
            String descricao,
            String tipo,
            String tituloPadrao,
            String conteudoPadrao,
            boolean ativo,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new ModeloDocumentoProfissional(
                id,
                empresaId,
                nome,
                descricao,
                TipoDocumentoProfissional.deCodigo(tipo),
                tituloPadrao,
                conteudoPadrao,
                ativo,
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }
}
