package br.com.atendepro.modules.pagamento.adapter.out.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.pagamento.application.port.out.AtualizarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.AtualizarPagamentoAssinaturaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarCobrancaPagamentoPorReferenciaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarPagamentoAssinaturaPorAssinaturaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.ListarPagamentosSandboxPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarPagamentoAssinaturaPort;
import br.com.atendepro.modules.pagamento.application.result.PagamentoSandboxResumoResult;
import br.com.atendepro.modules.pagamento.domain.model.AmbientePagamento;
import br.com.atendepro.modules.pagamento.domain.model.CobrancaPagamento;
import br.com.atendepro.modules.pagamento.domain.model.EventoPagamentoGateway;
import br.com.atendepro.modules.pagamento.domain.model.PagamentoAssinatura;
import br.com.atendepro.modules.pagamento.domain.model.ProvedorPagamento;
import br.com.atendepro.modules.pagamento.domain.model.StatusCobrancaPagamento;
import br.com.atendepro.modules.pagamento.domain.model.StatusPagamentoAssinatura;
import br.com.atendepro.modules.pagamento.domain.model.TipoEventoPagamentoGateway;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcPagamentoAdapter implements
        SalvarPagamentoAssinaturaPort,
        AtualizarPagamentoAssinaturaPort,
        SalvarCobrancaPagamentoPort,
        AtualizarCobrancaPagamentoPort,
        SalvarEventoPagamentoGatewayPort,
        CarregarEventoPagamentoGatewayPort,
        CarregarPagamentoAssinaturaPorAssinaturaExternaPort,
        CarregarCobrancaPagamentoPorReferenciaExternaPort,
        ListarPagamentosSandboxPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPagamentoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarPagamentoAssinatura(PagamentoAssinatura pagamento) {
        jdbcTemplate.update(
                """
                insert into pagamento_assinaturas (
                    id, empresa_id, plano_id, assinatura_interna_id, provedor, ambiente, status,
                    cliente_externo_id, assinatura_externa_id, checkout_externo_id, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                pagamento.id(),
                pagamento.empresaId(),
                pagamento.planoId(),
                pagamento.assinaturaInternaId(),
                pagamento.provedor().name(),
                pagamento.ambiente().name(),
                pagamento.status().name(),
                pagamento.clienteExternoId(),
                pagamento.assinaturaExternaId(),
                pagamento.checkoutExternoId(),
                Timestamp.from(pagamento.criadoEm()),
                Timestamp.from(pagamento.atualizadoEm())
        );
    }

    @Override
    public void atualizarPagamentoAssinatura(PagamentoAssinatura pagamento) {
        jdbcTemplate.update(
                """
                update pagamento_assinaturas
                set status = ?, atualizado_em = ?
                where id = ?
                """,
                pagamento.status().name(),
                Timestamp.from(pagamento.atualizadoEm()),
                pagamento.id()
        );
    }

    @Override
    public void salvarCobrancaPagamento(CobrancaPagamento cobranca) {
        jdbcTemplate.update(
                """
                insert into pagamento_cobrancas (
                    id, pagamento_assinatura_id, cobranca_externa_id, status, valor, vencimento,
                    forma_pagamento, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                cobranca.id(),
                cobranca.pagamentoAssinaturaId(),
                cobranca.cobrancaExternaId(),
                cobranca.status().name(),
                cobranca.valor(),
                cobranca.vencimento() == null ? null : Date.valueOf(cobranca.vencimento()),
                cobranca.formaPagamento(),
                Timestamp.from(cobranca.criadoEm()),
                Timestamp.from(cobranca.atualizadoEm())
        );
    }

    @Override
    public void atualizarCobrancaPagamento(CobrancaPagamento cobranca) {
        jdbcTemplate.update(
                """
                update pagamento_cobrancas
                set status = ?, atualizado_em = ?
                where id = ?
                """,
                cobranca.status().name(),
                Timestamp.from(cobranca.atualizadoEm()),
                cobranca.id()
        );
    }

    @Override
    public void salvarEventoPagamentoGateway(EventoPagamentoGateway evento) {
        jdbcTemplate.update(
                """
                insert into pagamento_gateway_eventos (
                    id, pagamento_assinatura_id, provedor, ambiente, tipo, evento_externo_id,
                    referencia_externa_id, payload_sanitizado, processado, criado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?)
                """,
                evento.id(),
                evento.pagamentoAssinaturaId(),
                evento.provedor().name(),
                evento.ambiente().name(),
                evento.tipo().name(),
                evento.eventoExternoId(),
                evento.referenciaExternaId(),
                evento.payloadSanitizado(),
                evento.processado(),
                Timestamp.from(evento.criadoEm())
        );
    }

    @Override
    public Optional<EventoPagamentoGateway> carregarEvento(
            ProvedorPagamento provedor,
            TipoEventoPagamentoGateway tipo,
            String eventoExternoId
    ) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, pagamento_assinatura_id, provedor, ambiente, tipo, evento_externo_id,
                           referencia_externa_id, payload_sanitizado::text as payload_sanitizado, processado, criado_em
                    from pagamento_gateway_eventos
                    where provedor = ? and tipo = ? and evento_externo_id = ?
                    """,
                    (rs, rowNum) -> new EventoPagamentoGateway(
                            rs.getObject("id", UUID.class),
                            rs.getObject("pagamento_assinatura_id", UUID.class),
                            ProvedorPagamento.valueOf(rs.getString("provedor")),
                            AmbientePagamento.valueOf(rs.getString("ambiente")),
                            TipoEventoPagamentoGateway.valueOf(rs.getString("tipo")),
                            rs.getString("evento_externo_id"),
                            rs.getString("referencia_externa_id"),
                            rs.getString("payload_sanitizado"),
                            rs.getBoolean("processado"),
                            rs.getTimestamp("criado_em").toInstant()
                    ),
                    provedor.name(),
                    tipo.name(),
                    eventoExternoId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PagamentoAssinatura> carregarPorAssinaturaExterna(String assinaturaExternaId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, plano_id, assinatura_interna_id, provedor, ambiente, status,
                           cliente_externo_id, assinatura_externa_id, checkout_externo_id, criado_em, atualizado_em
                    from pagamento_assinaturas
                    where assinatura_externa_id = ?
                    """,
                    (rs, rowNum) -> new PagamentoAssinatura(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("plano_id", UUID.class),
                            rs.getObject("assinatura_interna_id", UUID.class),
                            ProvedorPagamento.valueOf(rs.getString("provedor")),
                            AmbientePagamento.valueOf(rs.getString("ambiente")),
                            StatusPagamentoAssinatura.valueOf(rs.getString("status")),
                            rs.getString("cliente_externo_id"),
                            rs.getString("assinatura_externa_id"),
                            rs.getString("checkout_externo_id"),
                            rs.getTimestamp("criado_em").toInstant(),
                            rs.getTimestamp("atualizado_em").toInstant()
                    ),
                    assinaturaExternaId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CobrancaPagamento> carregarPorCobrancaExterna(String cobrancaExternaId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, pagamento_assinatura_id, cobranca_externa_id, status, valor, vencimento,
                           forma_pagamento, criado_em, atualizado_em
                    from pagamento_cobrancas
                    where cobranca_externa_id = ?
                    """,
                    (rs, rowNum) -> new CobrancaPagamento(
                            rs.getObject("id", UUID.class),
                            rs.getObject("pagamento_assinatura_id", UUID.class),
                            rs.getString("cobranca_externa_id"),
                            StatusCobrancaPagamento.valueOf(rs.getString("status")),
                            rs.getBigDecimal("valor"),
                            rs.getDate("vencimento") == null ? null : rs.getDate("vencimento").toLocalDate(),
                            rs.getString("forma_pagamento"),
                            rs.getTimestamp("criado_em").toInstant(),
                            rs.getTimestamp("atualizado_em").toInstant()
                    ),
                    cobrancaExternaId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<PagamentoSandboxResumoResult> listarPagamentosSandbox(
            Paginacao paginacao,
            UUID empresaId,
            String status
    ) {
        var filtros = new ArrayList<String>();
        var parametros = new ArrayList<>();
        filtros.add("pa.ambiente = 'SANDBOX'");
        if (empresaId != null) {
            filtros.add("pa.empresa_id = ?");
            parametros.add(empresaId);
        }
        if (status != null && !status.isBlank()) {
            filtros.add("pa.status = ?");
            parametros.add(status.trim().toUpperCase());
        }
        String where = "where " + String.join(" and ", filtros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from pagamento_assinaturas pa " + where,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var itens = jdbcTemplate.query(
                """
                select
                    pa.id as pagamento_assinatura_id,
                    pa.empresa_id,
                    pa.plano_id,
                    pa.assinatura_interna_id,
                    pa.provedor,
                    pa.ambiente,
                    pa.status as status_assinatura,
                    pa.cliente_externo_id,
                    pa.assinatura_externa_id,
                    pa.checkout_externo_id,
                    pc.id as cobranca_id,
                    pc.cobranca_externa_id,
                    pc.status as status_cobranca,
                    pc.valor,
                    pc.vencimento,
                    pc.forma_pagamento,
                    pe.id as ultimo_evento_id,
                    pe.tipo as ultimo_evento_tipo,
                    pe.processado as ultimo_evento_processado,
                    pe.criado_em as ultimo_evento_em,
                    pa.criado_em,
                    pa.atualizado_em
                from pagamento_assinaturas pa
                left join lateral (
                    select id, cobranca_externa_id, status, valor, vencimento, forma_pagamento
                    from pagamento_cobrancas
                    where pagamento_assinatura_id = pa.id
                    order by criado_em desc
                    limit 1
                ) pc on true
                left join lateral (
                    select id, tipo, processado, criado_em
                    from pagamento_gateway_eventos
                    where pagamento_assinatura_id = pa.id
                    order by criado_em desc
                    limit 1
                ) pe on true
                %s
                order by pa.criado_em desc
                limit ? offset ?
                """.formatted(where),
                (rs, rowNum) -> new PagamentoSandboxResumoResult(
                        rs.getObject("pagamento_assinatura_id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("plano_id", UUID.class),
                        rs.getObject("assinatura_interna_id", UUID.class),
                        rs.getString("provedor"),
                        rs.getString("ambiente"),
                        rs.getString("status_assinatura"),
                        rs.getString("cliente_externo_id"),
                        rs.getString("assinatura_externa_id"),
                        rs.getString("checkout_externo_id"),
                        rs.getObject("cobranca_id", UUID.class),
                        rs.getString("cobranca_externa_id"),
                        rs.getString("status_cobranca"),
                        rs.getBigDecimal("valor"),
                        rs.getDate("vencimento") == null ? null : rs.getDate("vencimento").toLocalDate(),
                        rs.getString("forma_pagamento"),
                        rs.getObject("ultimo_evento_id", UUID.class),
                        rs.getString("ultimo_evento_tipo"),
                        rs.getBoolean("ultimo_evento_processado"),
                        rs.getTimestamp("ultimo_evento_em") == null ? null : rs.getTimestamp("ultimo_evento_em").toInstant(),
                        rs.getTimestamp("criado_em").toInstant(),
                        rs.getTimestamp("atualizado_em").toInstant()
                ),
                parametros.toArray()
        );

        return new ResultadoPaginado<>(itens, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }
}
