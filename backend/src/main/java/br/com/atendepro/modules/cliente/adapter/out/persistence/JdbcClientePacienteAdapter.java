package br.com.atendepro.modules.cliente.adapter.out.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.cliente.application.port.out.CarregarClientePacientePorIdPort;
import br.com.atendepro.modules.cliente.application.port.out.ListarClientesPacientesPort;
import br.com.atendepro.modules.cliente.application.port.out.SalvarClientePacientePort;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.modules.cliente.domain.model.TipoCliente;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Repository
@Profile("!test")
public class JdbcClientePacienteAdapter implements
        SalvarClientePacientePort,
        CarregarClientePacientePorIdPort,
        ListarClientesPacientesPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClientePacienteAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarClientePaciente(ClientePaciente cliente) {
        jdbcTemplate.update(
                """
                insert into clientes_pacientes (
                    id, empresa_id, nome, tipo, area, documento, email, telefone, data_nascimento,
                    observacoes, ativo, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                cliente.id(),
                cliente.empresaId(),
                cliente.nome(),
                cliente.tipo().name(),
                cliente.area().name(),
                cliente.documento(),
                cliente.email(),
                cliente.telefone(),
                cliente.dataNascimento() == null ? null : Date.valueOf(cliente.dataNascimento()),
                cliente.observacoes(),
                cliente.ativo(),
                Timestamp.from(cliente.criadoEm()),
                Timestamp.from(cliente.atualizadoEm())
        );
    }

    @Override
    public Optional<ClientePaciente> carregarClientePacientePorId(UUID clienteId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    """
                    select id, empresa_id, nome, tipo, area, documento, email, telefone, data_nascimento,
                           observacoes, ativo, criado_em, atualizado_em
                    from clientes_pacientes
                    where id = ?
                    """,
                    (rs, rowNum) -> mapearCliente(
                            rs.getObject("id", UUID.class),
                            rs.getObject("empresa_id", UUID.class),
                            rs.getString("nome"),
                            TipoCliente.valueOf(rs.getString("tipo")),
                            AreaCliente.deCodigo(rs.getString("area")),
                            rs.getString("documento"),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getDate("data_nascimento"),
                            rs.getString("observacoes"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("criado_em"),
                            rs.getTimestamp("atualizado_em")
                    ),
                    clienteId
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public ResultadoPaginado<ClientePaciente> listarClientesPacientes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    ) {
        var parametros = new ArrayList<>();
        String filtro = montarFiltro(empresaId, busca, area, ativo, parametros);

        Long total = jdbcTemplate.queryForObject(
                "select count(*) from clientes_pacientes " + filtro,
                Long.class,
                parametros.toArray()
        );

        parametros.add(paginacao.tamanho());
        parametros.add(paginacao.offset());
        var clientes = jdbcTemplate.query(
                """
                select id, empresa_id, nome, tipo, area, documento, email, telefone, data_nascimento,
                       observacoes, ativo, criado_em, atualizado_em
                from clientes_pacientes
                %s
                order by nome asc
                limit ? offset ?
                """.formatted(filtro),
                (rs, rowNum) -> mapearCliente(
                        rs.getObject("id", UUID.class),
                        rs.getObject("empresa_id", UUID.class),
                        rs.getString("nome"),
                        TipoCliente.valueOf(rs.getString("tipo")),
                        AreaCliente.deCodigo(rs.getString("area")),
                        rs.getString("documento"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getDate("data_nascimento"),
                        rs.getString("observacoes"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("criado_em"),
                        rs.getTimestamp("atualizado_em")
                ),
                parametros.toArray()
        );
        return new ResultadoPaginado<>(clientes, total == null ? 0 : total, paginacao.pagina(), paginacao.tamanho());
    }

    private String montarFiltro(
            UUID empresaId,
            String busca,
            AreaCliente area,
            Boolean ativo,
            ArrayList<Object> parametros
    ) {
        var filtro = new StringBuilder("where empresa_id = ?");
        parametros.add(empresaId);
        if (busca != null && !busca.isBlank()) {
            String termo = "%" + busca.trim().toLowerCase() + "%";
            filtro.append(" and (lower(nome) like ? or lower(email) like ? or documento like ?)");
            parametros.add(termo);
            parametros.add(termo);
            parametros.add("%" + busca.replaceAll("\\D", "") + "%");
        }
        if (area != null) {
            filtro.append(" and area = ?");
            parametros.add(area.name());
        }
        if (ativo != null) {
            filtro.append(" and ativo = ?");
            parametros.add(ativo);
        }
        return filtro.toString();
    }

    private ClientePaciente mapearCliente(
            UUID id,
            UUID empresaId,
            String nome,
            TipoCliente tipo,
            AreaCliente area,
            String documento,
            String email,
            String telefone,
            Date dataNascimento,
            String observacoes,
            boolean ativo,
            Timestamp criadoEm,
            Timestamp atualizadoEm
    ) {
        return new ClientePaciente(
                id,
                empresaId,
                nome,
                tipo,
                area,
                documento,
                email,
                telefone,
                dataNascimento == null ? null : dataNascimento.toLocalDate(),
                observacoes,
                ativo,
                criadoEm.toInstant(),
                atualizadoEm.toInstant()
        );
    }
}
