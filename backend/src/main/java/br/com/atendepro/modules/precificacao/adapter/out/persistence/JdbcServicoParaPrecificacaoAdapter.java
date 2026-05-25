package br.com.atendepro.modules.precificacao.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.precificacao.application.port.out.CarregarServicoParaPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.result.ServicoPrecificacaoResult;

@Repository
@Profile("!test")
public class JdbcServicoParaPrecificacaoAdapter implements CarregarServicoParaPrecificacaoPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcServicoParaPrecificacaoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ServicoPrecificacaoResult> carregarServicoParaPrecificacao(
            UUID empresaId,
            UUID servicoProcedimentoId
    ) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, nome, duracao_minutos, preco_base
                    from servicos_procedimentos
                    where id = ?
                      and empresa_id = ?
                      and ativo = true
                    """,
                    (rs, rowNum) -> new ServicoPrecificacaoResult(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getString("nome"),
                            rs.getInt("duracao_minutos"),
                            rs.getBigDecimal("preco_base")
                    ),
                    servicoProcedimentoId,
                    empresaId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
