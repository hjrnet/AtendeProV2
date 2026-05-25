package br.com.atendepro.modules.assinatura.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.assinatura.application.port.out.AtualizarAssinaturaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaAtivaPorEmpresaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaPorIdPort;
import br.com.atendepro.modules.assinatura.application.port.out.ListarAssinaturasPort;
import br.com.atendepro.modules.assinatura.application.port.out.SalvarAssinaturaPort;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcAssinaturaAdapter implements
        SalvarAssinaturaPort,
        AtualizarAssinaturaPort,
        CarregarAssinaturaAtivaPorEmpresaPort,
        CarregarAssinaturaPorIdPort,
        ListarAssinaturasPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAssinaturaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarAssinatura(AssinaturaSaas assinatura) {
        jdbcTemplate.update(
                """
                insert into assinaturas (
                    id, empresa_id, plano_id, status, iniciado_em, cancelado_em, bloqueado_em, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                assinatura.id(),
                assinatura.empresaId(),
                assinatura.planoId(),
                assinatura.status().name(),
                Timestamp.from(assinatura.iniciadoEm()),
                assinatura.canceladoEm() == null ? null : Timestamp.from(assinatura.canceladoEm()),
                assinatura.bloqueadoEm() == null ? null : Timestamp.from(assinatura.bloqueadoEm()),
                Timestamp.from(assinatura.criadoEm()),
                Timestamp.from(assinatura.atualizadoEm())
        );
    }

    @Override
    public void atualizarAssinatura(AssinaturaSaas assinatura) {
        jdbcTemplate.update(
                """
                update assinaturas
                set plano_id = ?,
                    status = ?,
                    cancelado_em = ?,
                    bloqueado_em = ?,
                    atualizado_em = ?
                where id = ?
                """,
                assinatura.planoId(),
                assinatura.status().name(),
                assinatura.canceladoEm() == null ? null : Timestamp.from(assinatura.canceladoEm()),
                assinatura.bloqueadoEm() == null ? null : Timestamp.from(assinatura.bloqueadoEm()),
                Timestamp.from(assinatura.atualizadoEm()),
                assinatura.id()
        );
    }

    @Override
    public Optional<AssinaturaSaas> carregarAssinaturaAtivaPorEmpresa(UUID empresaId) {
        return carregarAssinatura(
                """
                select id, empresa_id, plano_id, status, iniciado_em, cancelado_em, bloqueado_em, criado_em, atualizado_em
                from assinaturas
                where empresa_id = ? and status in ('ATIVA', 'BLOQUEADA')
                order by criado_em desc
                limit 1
                """,
                empresaId
        );
    }

    @Override
    public Optional<AssinaturaSaas> carregarAssinaturaPorId(UUID assinaturaId) {
        return carregarAssinatura(
                """
                select id, empresa_id, plano_id, status, iniciado_em, cancelado_em, bloqueado_em, criado_em, atualizado_em
                from assinaturas
                where id = ?
                """,
                assinaturaId
        );
    }

    @Override
    public ResultadoPaginado<AssinaturaSaas> listarAssinaturas(Paginacao paginacao, AssinaturaStatus status) {
        var parametros = new ArrayList<>();
        String filtro = "";
        if (status != null) {
            filtro = "where status = ?";
            parametros.add(status.name());
        }
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from assinaturas " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var assinaturas = jdbcTemplate.query(
                """
                select id, empresa_id, plano_id, status, iniciado_em, cancelado_em, bloqueado_em, criado_em, atualizado_em
                from assinaturas
                %s
                order by criado_em desc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearAssinatura(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("plano_id", UUID.class),
                        AssinaturaStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("iniciado_em"),
                        rs.getTimestamp("cancelado_em"),
                        rs.getTimestamp("bloqueado_em"),
                        rs.getTimestamp("criado_em"),
                        rs.getTimestamp("atualizado_em")
                ),
                parametros.toArray()
        );

        return new ResultadoPaginado<>(assinaturas, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private Optional<AssinaturaSaas> carregarAssinatura(String sql, Object parametro) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> mapearAssinatura(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("plano_id", UUID.class),
                            AssinaturaStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("iniciado_em"),
                            rs.getTimestamp("cancelado_em"),
                            rs.getTimestamp("bloqueado_em"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")
                    ),
                    parametro
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private AssinaturaSaas mapearAssinatura(
            UUID id,
            UUID empresaId,
            UUID planoId,
            AssinaturaStatus status,
            Timestamp iniciadoEm,
            Timestamp canceladoEm,
            Timestamp bloqueadoEm,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new AssinaturaSaas(
                id,
                empresaId,
                planoId,
                status,
                iniciadoEm.toInstant(),
                canceladoEm == null ? null : canceladoEm.toInstant(),
                bloqueadoEm == null ? null : bloqueadoEm.toInstant(),
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }
}
