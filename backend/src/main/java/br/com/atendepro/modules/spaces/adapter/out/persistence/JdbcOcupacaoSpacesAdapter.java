package br.com.atendepro.modules.spaces.adapter.out.persistence;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.spaces.application.port.out.CarregarOcupacaoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.ListarOcupacoesSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.SalvarOcupacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.VerificarConflitoOcupacaoSpacesPort;
import br.com.atendepro.modules.spaces.domain.model.OcupacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcOcupacaoSpacesAdapter implements
        SalvarOcupacaoSpacesPort,
        CarregarOcupacaoSpacesPorIdPort,
        ListarOcupacoesSpacesPort,
        VerificarConflitoOcupacaoSpacesPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcOcupacaoSpacesAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarOcupacao(OcupacaoSpaces ocupacao) {
        jdbcTemplate.update(
                """
                insert into spaces_ocupacoes (
                    id, empresa_id, recurso_id, pacote_id, nome_parceiro,
                    inicio_em, fim_em, status, observacao, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ocupacao.id(),
                ocupacao.empresaId(),
                ocupacao.recursoId(),
                ocupacao.pacoteId(),
                ocupacao.nomeParceiro(),
                Timestamp.from(ocupacao.inicioEm()),
                Timestamp.from(ocupacao.fimEm()),
                ocupacao.status().name(),
                ocupacao.observacao(),
                Timestamp.from(ocupacao.criadoEm()),
                Timestamp.from(ocupacao.atualizadoEm())
        );
    }

    @Override
    public Optional<OcupacaoSpaces> carregarOcupacaoPorId(UUID ocupacaoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, recurso_id, pacote_id, nome_parceiro,
                           inicio_em, fim_em, status, observacao, criado_em, atualizado_em
                    from spaces_ocupacoes
                    where id = ?
                    """,
                    (rs, rowNum) -> mapearOcupacao(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("recurso_id", UUID.class),
                            rs.getObject("pacote_id", UUID.class),
                            rs.getString("nome_parceiro"),
                            rs.getTimestamp("inicio_em"),
                            rs.getTimestamp("fim_em"),
                            StatusOcupacaoSpaces.deCodigo(rs.getString("status")),
                            rs.getString("observacao"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")
                    ),
                    ocupacaoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<OcupacaoSpaces> listarOcupacoes(
            UUID empresaId,
            Paginacao paginacao,
            UUID recursoId,
            Instant inicioEm,
            Instant fimEm,
            StatusOcupacaoSpaces status
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, recursoId, inicioEm, fimEm, status, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from spaces_ocupacoes " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var ocupacoes = jdbcTemplate.query(
                """
                select id, empresa_id, recurso_id, pacote_id, nome_parceiro,
                       inicio_em, fim_em, status, observacao, criado_em, atualizado_em
                from spaces_ocupacoes
                %s
                order by inicio_em asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearOcupacao(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("recurso_id", UUID.class),
                        rs.getObject("pacote_id", UUID.class),
                        rs.getString("nome_parceiro"),
                        rs.getTimestamp("inicio_em"),
                        rs.getTimestamp("fim_em"),
                        StatusOcupacaoSpaces.deCodigo(rs.getString("status")),
                        rs.getString("observacao"),
                        rs.getTimestamp("criado_em"),
                        rs.getTimestamp("atualizado_em")
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(ocupacoes, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    @Override
    public boolean existeConflitoOcupacao(UUID empresaId, UUID recursoId, Instant inicioEm, Instant fimEm) {
        Long total = jdbcTemplate.queryForObject(
                """
                select count(*)
                from spaces_ocupacoes
                where empresa_id = ?
                  and recurso_id = ?
                  and status in ('RESERVADA', 'CONFIRMADA')
                  and inicio_em < ?
                  and fim_em > ?
                """,
                Long.class,
                empresaId,
                recursoId,
                Timestamp.from(fimEm),
                Timestamp.from(inicioEm)
        );
        return total != null && total > 0;
    }

    private String montarFiltro(
            UUID empresaId,
            UUID recursoId,
            Instant inicioEm,
            Instant fimEm,
            StatusOcupacaoSpaces status,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (recursoId != null) {
            filtro.append(" and recurso_id = ?");
            parametros.add(recursoId);
        }
        if (inicioEm != null) {
            filtro.append(" and fim_em > ?");
            parametros.add(Timestamp.from(inicioEm));
        }
        if (fimEm != null) {
            filtro.append(" and inicio_em < ?");
            parametros.add(Timestamp.from(fimEm));
        }
        if (status != null) {
            filtro.append(" and status = ?");
            parametros.add(status.name());
        }
        return filtro.toString();
    }

    private OcupacaoSpaces mapearOcupacao(
            UUID id,
            UUID empresaId,
            UUID recursoId,
            UUID pacoteId,
            String nomeParceiro,
            Timestamp inicioEm,
            Timestamp fimEm,
            StatusOcupacaoSpaces status,
            String observacao,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new OcupacaoSpaces(
                id,
                empresaId,
                recursoId,
                pacoteId,
                nomeParceiro,
                inicioEm.toInstant(),
                fimEm.toInstant(),
                status,
                observacao,
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }
}
