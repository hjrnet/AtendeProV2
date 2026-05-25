package br.com.atendepro.modules.documento.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.documento.application.port.out.CarregarMarcaDaguaAcademicaPlanoPort;

@Repository
@Profile("!test")
public class JdbcMarcaDaguaAcademicaPlanoAdapter implements CarregarMarcaDaguaAcademicaPlanoPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMarcaDaguaAcademicaPlanoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<String> carregarMarcaDaguaAcademica(UUID empresaId) {
        try {
            String marcaDagua = jdbcTemplate.queryForObject(
                    """
                    select p.marca_dagua_academica
                    from assinaturas a
                    join planos p on p.id = a.plano_id
                    where a.empresa_id = ?
                      and a.status = 'ATIVA'
                      and p.estudante = true
                      and p.marca_dagua_academica is not null
                      and trim(p.marca_dagua_academica) <> ''
                    order by a.iniciado_em desc
                    limit 1
                    """,
                    String.class,
                    empresaId
            );
            return Optional.ofNullable(marcaDagua);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
