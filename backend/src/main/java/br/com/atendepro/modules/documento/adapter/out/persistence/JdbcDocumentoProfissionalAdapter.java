package br.com.atendepro.modules.documento.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.documento.application.port.out.CarregarDocumentoProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.CarregarDocumentoProfissionalPorCodigoValidacaoPort;
import br.com.atendepro.modules.documento.application.port.out.ListarDocumentosProfissionaisPort;
import br.com.atendepro.modules.documento.application.port.out.SalvarDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcDocumentoProfissionalAdapter implements
        SalvarDocumentoProfissionalPort,
        CarregarDocumentoProfissionalPorCodigoValidacaoPort,
        CarregarDocumentoProfissionalPorIdPort,
        ListarDocumentosProfissionaisPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDocumentoProfissionalAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarDocumento(DocumentoProfissional documento) {
        jdbcTemplate.update(
                """
                insert into documentos_profissionais (
                    id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome,
                    titulo, tipo, conteudo, status, versao, codigo_validacao,
                    validacao_publica_ativa, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                documento.id(),
                documento.empresaId(),
                documento.clientePacienteId(),
                documento.profissionalId(),
                documento.profissionalNome(),
                documento.titulo(),
                documento.tipo().name(),
                documento.conteudo(),
                documento.status().name(),
                documento.versao(),
                documento.codigoValidacao(),
                documento.validacaoPublicaAtiva(),
                documento.ativo(),
                Timestamp.from(documento.criadoEm()),
                Timestamp.from(documento.atualizadoEm())
        );
    }

    @Override
    public Optional<DocumentoProfissional> carregarDocumentoPorId(UUID documentoId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome,
                           titulo, tipo, conteudo, status, versao, codigo_validacao,
                           validacao_publica_ativa, ativo, criado_em, atualizado_em
                    from documentos_profissionais
                    where id = ?
                    """,
                    (rs, rowNum) -> mapearDocumento(rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("cliente_paciente_id", UUID.class),
                            rs.getObject("profissional_id", UUID.class),
                            rs.getString("profissional_nome"),
                            rs.getString("titulo"),
                            rs.getString("tipo"),
                            rs.getString("conteudo"),
                            rs.getString("status"),
                            rs.getInt("versao"),
                            rs.getString("codigo_validacao"),
                            rs.getBoolean("validacao_publica_ativa"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")),
                    documentoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<DocumentoProfissional> carregarDocumentoPorCodigoValidacao(String codigoValidacao) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome,
                           titulo, tipo, conteudo, status, versao, codigo_validacao,
                           validacao_publica_ativa, ativo, criado_em, atualizado_em
                    from documentos_profissionais
                    where codigo_validacao = ?
                    """,
                    (rs, rowNum) -> mapearDocumento(rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("cliente_paciente_id", UUID.class),
                            rs.getObject("profissional_id", UUID.class),
                            rs.getString("profissional_nome"),
                            rs.getString("titulo"),
                            rs.getString("tipo"),
                            rs.getString("conteudo"),
                            rs.getString("status"),
                            rs.getInt("versao"),
                            rs.getString("codigo_validacao"),
                            rs.getBoolean("validacao_publica_ativa"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")),
                    codigoValidacao
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<DocumentoProfissional> listarDocumentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            StatusDocumentoProfissional status,
            UUID clientePacienteId,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, tipo, status, clientePacienteId, ativo, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from documentos_profissionais " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var documentos = jdbcTemplate.query(
                """
                select id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome,
                       titulo, tipo, conteudo, status, versao, codigo_validacao,
                       validacao_publica_ativa, ativo, criado_em, atualizado_em
                from documentos_profissionais
                %s
                order by atualizado_em desc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearDocumento(rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("cliente_paciente_id", UUID.class),
                        rs.getObject("profissional_id", UUID.class),
                        rs.getString("profissional_nome"),
                        rs.getString("titulo"),
                        rs.getString("tipo"),
                        rs.getString("conteudo"),
                        rs.getString("status"),
                        rs.getInt("versao"),
                        rs.getString("codigo_validacao"),
                        rs.getBoolean("validacao_publica_ativa"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em"),
                        rs.getTimestamp("atualizado_em")),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(documentos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private DocumentoProfissional mapearDocumento(
            UUID id,
            UUID empresaId,
            UUID clientePacienteId,
            UUID profissionalId,
            String profissionalNome,
            String titulo,
            String tipo,
            String conteudo,
            String status,
            int versao,
            String codigoValidacao,
            boolean validacaoPublicaAtiva,
            boolean ativo,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new DocumentoProfissional(
                id,
                empresaId,
                clientePacienteId,
                profissionalId,
                profissionalNome,
                titulo,
                TipoDocumentoProfissional.deCodigo(tipo),
                conteudo,
                StatusDocumentoProfissional.deCodigo(status),
                versao,
                codigoValidacao,
                validacaoPublicaAtiva,
                ativo,
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            TipoDocumentoProfissional tipo,
            StatusDocumentoProfissional status,
            UUID clientePacienteId,
            Boolean ativo,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(titulo) like ? or lower(profissional_nome) like ? or lower(conteudo) like ?)");
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        if (tipo != null) {
            filtro.append(" and tipo = ?");
            parametros.add(tipo.name());
        }
        if (status != null) {
            filtro.append(" and status = ?");
            parametros.add(status.name());
        }
        if (clientePacienteId != null) {
            filtro.append(" and cliente_paciente_id = ?");
            parametros.add(clientePacienteId);
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }
}
