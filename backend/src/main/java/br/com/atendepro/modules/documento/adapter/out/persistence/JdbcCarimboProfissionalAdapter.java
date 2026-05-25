package br.com.atendepro.modules.documento.adapter.out.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.documento.application.port.out.CarregarCarimboProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.ListarCarimbosProfissionaisPort;
import br.com.atendepro.modules.documento.application.port.out.SalvarCarimboProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcCarimboProfissionalAdapter implements
        SalvarCarimboProfissionalPort,
        CarregarCarimboProfissionalPorIdPort,
        ListarCarimbosProfissionaisPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCarimboProfissionalAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarCarimbo(CarimboProfissional carimbo) {
        jdbcTemplate.update(
                """
                insert into documentos_carimbos_profissionais (
                    id, empresa_id, profissional_id, profissional_nome, conselho, uf,
                    numero_registro, assinatura_texto, clinica_nome, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                carimbo.id(),
                carimbo.empresaId(),
                carimbo.profissionalId(),
                carimbo.profissionalNome(),
                carimbo.conselho().name(),
                carimbo.uf(),
                carimbo.numeroRegistro(),
                carimbo.assinaturaTexto(),
                carimbo.clinicaNome(),
                carimbo.ativo(),
                Timestamp.from(carimbo.criadoEm()),
                Timestamp.from(carimbo.atualizadoEm())
        );
    }

    @Override
    public Optional<CarimboProfissional> carregarCarimboPorId(UUID carimboId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, profissional_id, profissional_nome, conselho, uf,
                           numero_registro, assinatura_texto, clinica_nome, ativo, criado_em, atualizado_em
                    from documentos_carimbos_profissionais
                    where id = ?
                    """,
                    (rs, rowNum) -> mapearCarimbo(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getObject("profissional_id", UUID.class),
                            rs.getString("profissional_nome"),
                            rs.getString("conselho"),
                            rs.getString("uf"),
                            rs.getString("numero_registro"),
                            rs.getString("assinatura_texto"),
                            rs.getString("clinica_nome"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")
                    ),
                    carimboId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<CarimboProfissional> listarCarimbos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            ConselhoProfissional conselho,
            String uf,
            UUID profissionalId,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, conselho, uf, profissionalId, ativo, parametros);
        Long total = jdbcTemplate.queryForObject(
                "select count(*) from documentos_carimbos_profissionais " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var carimbos = jdbcTemplate.query(
                """
                select id, empresa_id, profissional_id, profissional_nome, conselho, uf,
                       numero_registro, assinatura_texto, clinica_nome, ativo, criado_em, atualizado_em
                from documentos_carimbos_profissionais
                %s
                order by profissional_nome asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearCarimbo(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getObject("profissional_id", UUID.class),
                        rs.getString("profissional_nome"),
                        rs.getString("conselho"),
                        rs.getString("uf"),
                        rs.getString("numero_registro"),
                        rs.getString("assinatura_texto"),
                        rs.getString("clinica_nome"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em"),
                        rs.getTimestamp("atualizado_em")
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(carimbos, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private CarimboProfissional mapearCarimbo(
            UUID id,
            UUID empresaId,
            UUID profissionalId,
            String profissionalNome,
            String conselho,
            String uf,
            String numeroRegistro,
            String assinaturaTexto,
            String clinicaNome,
            boolean ativo,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new CarimboProfissional(
                id,
                empresaId,
                profissionalId,
                profissionalNome,
                ConselhoProfissional.deCodigo(conselho),
                uf,
                numeroRegistro,
                assinaturaTexto,
                clinicaNome,
                ativo,
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            ConselhoProfissional conselho,
            String uf,
            UUID profissionalId,
            Boolean ativo,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(profissional_nome) like ? or lower(numero_registro) like ? or lower(clinica_nome) like ?)");
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        if (conselho != null) {
            filtro.append(" and conselho = ?");
            parametros.add(conselho.name());
        }
        if (uf != null && !uf.isBlank()) {
            filtro.append(" and uf = ?");
            parametros.add(uf.trim().toUpperCase());
        }
        if (profissionalId != null) {
            filtro.append(" and profissional_id = ?");
            parametros.add(profissionalId);
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }
}
