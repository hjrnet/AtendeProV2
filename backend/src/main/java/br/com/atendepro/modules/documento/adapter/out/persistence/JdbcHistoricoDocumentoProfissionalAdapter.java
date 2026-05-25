package br.com.atendepro.modules.documento.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.documento.application.port.out.ListarHistoricoDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.port.out.RegistrarHistoricoDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.AcaoHistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.HistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcHistoricoDocumentoProfissionalAdapter implements
        RegistrarHistoricoDocumentoProfissionalPort,
        ListarHistoricoDocumentoProfissionalPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcHistoricoDocumentoProfissionalAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void registrarHistorico(HistoricoDocumentoProfissional historico) {
        jdbcTemplate.update(
                """
                insert into documentos_profissionais_historico (
                    id, documento_id, empresa_id, versao_anterior, versao_nova, acao,
                    titulo_anterior, conteudo_anterior, status_anterior,
                    titulo_novo, conteudo_novo, status_novo,
                    motivo, usuario_id, criado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                historico.id(),
                historico.documentoId(),
                historico.empresaId(),
                historico.versaoAnterior(),
                historico.versaoNova(),
                historico.acao().name(),
                historico.tituloAnterior(),
                historico.conteudoAnterior(),
                historico.statusAnterior().name(),
                historico.tituloNovo(),
                historico.conteudoNovo(),
                historico.statusNovo().name(),
                historico.motivo(),
                historico.usuarioId(),
                Timestamp.from(historico.criadoEm())
        );
    }

    @Override
    public ResultadoPaginado<HistoricoDocumentoProfissional> listarHistorico(
            UUID documentoId,
            UUID empresaId,
            Paginacao paginacao
    ) {
        Long total = jdbcTemplate.queryForObject(
                """
                select count(*)
                from documentos_profissionais_historico
                where documento_id = ? and empresa_id = ?
                """,
                Long.class,
                documentoId,
                empresaId
        );
        var itens = jdbcTemplate.query(
                """
                select id, documento_id, empresa_id, versao_anterior, versao_nova, acao,
                       titulo_anterior, conteudo_anterior, status_anterior,
                       titulo_novo, conteudo_novo, status_novo,
                       motivo, usuario_id, criado_em
                from documentos_profissionais_historico
                where documento_id = ? and empresa_id = ?
                order by criado_em desc, versao_nova desc
                limit ? offset ?
                """,
                (rs, rowNum) -> new HistoricoDocumentoProfissional(
                        rs.getObject("id", UUID.class),
                        rs.getObject("documento_id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getInt("versao_anterior"),
                        rs.getInt("versao_nova"),
                        AcaoHistoricoDocumentoProfissional.deCodigo(rs.getString("acao")),
                        rs.getString("titulo_anterior"),
                        rs.getString("conteudo_anterior"),
                        StatusDocumentoProfissional.deCodigo(rs.getString("status_anterior")),
                        rs.getString("titulo_novo"),
                        rs.getString("conteudo_novo"),
                        StatusDocumentoProfissional.deCodigo(rs.getString("status_novo")),
                        rs.getString("motivo"),
                        rs.getObject("usuario_id", UUID.class),
                        rs.getTimestamp("criado_em").toInstant()
                ),
                documentoId,
                empresaId,
                paginacao.tamanho(),
                paginacao.offset()
        );
        return new ResultadoPaginado<>(itens, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }
}
