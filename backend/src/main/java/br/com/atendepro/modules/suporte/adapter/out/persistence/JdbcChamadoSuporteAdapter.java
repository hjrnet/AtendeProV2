package br.com.atendepro.modules.suporte.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.suporte.application.port.out.AtualizarChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.CarregarChamadoSuportePorIdPort;
import br.com.atendepro.modules.suporte.application.port.out.ListarChamadosSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.ListarMensagensChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.SalvarChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.SalvarMensagemChamadoSuportePort;
import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcChamadoSuporteAdapter implements
        SalvarChamadoSuportePort,
        AtualizarChamadoSuportePort,
        CarregarChamadoSuportePorIdPort,
        ListarChamadosSuportePort,
        SalvarMensagemChamadoSuportePort,
        ListarMensagensChamadoSuportePort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcChamadoSuporteAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarChamado(ChamadoSuporte chamado) {
        jdbcTemplate.update(
                """
                insert into chamados_suporte (
                    id, empresa_id, solicitante_usuario_id, solicitante_nome, solicitante_email,
                    titulo, descricao, prioridade, status, categoria, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                chamado.id(),
                chamado.empresaId(),
                chamado.solicitanteUsuarioId(),
                chamado.solicitanteNome(),
                chamado.solicitanteEmail(),
                chamado.titulo(),
                chamado.descricao(),
                chamado.prioridade().name(),
                chamado.status().name(),
                chamado.categoria(),
                Timestamp.from(chamado.criadoEm()),
                Timestamp.from(chamado.atualizadoEm())
        );
    }

    @Override
    public void atualizarChamado(ChamadoSuporte chamado) {
        jdbcTemplate.update(
                """
                update chamados_suporte
                set prioridade = ?,
                    status = ?,
                    atualizado_em = ?
                where id = ?
                """,
                chamado.prioridade().name(),
                chamado.status().name(),
                Timestamp.from(chamado.atualizadoEm()),
                chamado.id()
        );
    }

    @Override
    public Optional<ChamadoSuporte> carregarChamadoPorId(UUID chamadoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, solicitante_usuario_id, solicitante_nome, solicitante_email,
                           titulo, descricao, prioridade, status, categoria, criado_em, atualizado_em
                    from chamados_suporte
                    where id = ?
                    """,
                    (rs, rowNum) -> mapearChamado(rs.getObject("id", UUID.class), rs.getObject("empresa_id", UUID.class),
                            rs.getObject("solicitante_usuario_id", UUID.class), rs.getString("solicitante_nome"),
                            rs.getString("solicitante_email"), rs.getString("titulo"), rs.getString("descricao"),
                            rs.getString("prioridade"), rs.getString("status"), rs.getString("categoria"),
                            rs.getTimestamp("criado_em"), rs.getTimestamp("atualizado_em")),
                    chamadoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<ChamadoSuporte> listarChamados(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            StatusChamadoSuporte status,
            PrioridadeChamadoSuporte prioridade
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, status, prioridade, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from chamados_suporte " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        List<ChamadoSuporte> chamados = jdbcTemplate.query(
                """
                select id, empresa_id, solicitante_usuario_id, solicitante_nome, solicitante_email,
                       titulo, descricao, prioridade, status, categoria, criado_em, atualizado_em
                from chamados_suporte
                %s
                order by atualizado_em desc, criado_em desc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearChamado(rs.getObject("id", UUID.class), rs.getObject("empresa_id", UUID.class),
                        rs.getObject("solicitante_usuario_id", UUID.class), rs.getString("solicitante_nome"),
                        rs.getString("solicitante_email"), rs.getString("titulo"), rs.getString("descricao"),
                        rs.getString("prioridade"), rs.getString("status"), rs.getString("categoria"),
                        rs.getTimestamp("criado_em"), rs.getTimestamp("atualizado_em")),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(chamados, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    @Override
    public void salvarMensagem(MensagemChamadoSuporte mensagem) {
        jdbcTemplate.update(
                """
                insert into chamados_suporte_mensagens (
                    id, chamado_id, autor_usuario_id, autor_nome, origem, mensagem, criado_em
                )
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                mensagem.id(),
                mensagem.chamadoId(),
                mensagem.autorUsuarioId(),
                mensagem.autorNome(),
                mensagem.origem().name(),
                mensagem.mensagem(),
                Timestamp.from(mensagem.criadoEm())
        );
    }

    @Override
    public List<MensagemChamadoSuporte> listarMensagens(UUID chamadoId) {
        return jdbcTemplate.query(
                """
                select id, chamado_id, autor_usuario_id, autor_nome, origem, mensagem, criado_em
                from chamados_suporte_mensagens
                where chamado_id = ?
                order by criado_em asc
                """,
                (rs, rowNum) -> new MensagemChamadoSuporte(
                        rs.getObject("id", UUID.class),
                        rs.getObject("chamado_id", UUID.class),
                        rs.getObject("autor_usuario_id", UUID.class),
                        rs.getString("autor_nome"),
                        OrigemMensagemChamadoSuporte.valueOf(rs.getString("origem")),
                        rs.getString("mensagem"),
                        rs.getTimestamp("criado_em").toInstant()
                ),
                chamadoId
        );
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            StatusChamadoSuporte status,
            PrioridadeChamadoSuporte prioridade,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(titulo) like ? or lower(descricao) like ? or lower(categoria) like ?)");
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        if (status != null) {
            filtro.append(" and status = ?");
            parametros.add(status.name());
        }
        if (prioridade != null) {
            filtro.append(" and prioridade = ?");
            parametros.add(prioridade.name());
        }
        return filtro.toString();
    }

    private ChamadoSuporte mapearChamado(
            UUID id,
            UUID empresaId,
            UUID solicitanteUsuarioId,
            String solicitanteNome,
            String solicitanteEmail,
            String titulo,
            String descricao,
            String prioridade,
            String status,
            String categoria,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new ChamadoSuporte(
                id,
                empresaId,
                solicitanteUsuarioId,
                solicitanteNome,
                solicitanteEmail,
                titulo,
                descricao,
                PrioridadeChamadoSuporte.valueOf(prioridade),
                StatusChamadoSuporte.valueOf(status),
                categoria,
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }
}
