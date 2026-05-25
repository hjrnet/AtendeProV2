package br.com.atendepro.modules.nutri.adapter.out.persistence;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.nutri.application.port.out.CarregarAvaliacaoAntropometricaNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarPlanoAlimentarNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarVisaoNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarProntuarioNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarAvaliacoesAntropometricasNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarPlanosAlimentaresNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarPacientesNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.SalvarAvaliacaoAntropometricaNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.SalvarPlanoAlimentarNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.VerificarPacienteNutriProPort;
import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;
import br.com.atendepro.modules.nutri.domain.model.ItemPlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.ObjetivoNutricionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.PlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.RefeicaoPlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.SexoBiologicoNutriPro;
import br.com.atendepro.modules.nutri.domain.model.StatusPlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemPlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.application.result.DadosProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.MetricasNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;
import br.com.atendepro.modules.nutri.application.result.ResumoProntuarioNutriProResult;

@Repository
@Profile("!test")
public class JdbcVisaoNutriProAdapter implements
        CarregarVisaoNutriProPort,
        ListarPacientesNutriProPort,
        CarregarProntuarioNutriProPort,
        VerificarPacienteNutriProPort,
        SalvarAvaliacaoAntropometricaNutriProPort,
        ListarAvaliacoesAntropometricasNutriProPort,
        CarregarAvaliacaoAntropometricaNutriProPort,
        SalvarPlanoAlimentarNutriProPort,
        ListarPlanosAlimentaresNutriProPort,
        CarregarPlanoAlimentarNutriProPort {

    private static final String AREA_NUTRI = "NUTRI";

    private final JdbcTemplate jdbcTemplate;

    public JdbcVisaoNutriProAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasNutriProResult carregarVisaoNutriPro(UUID empresaId, LocalDate hoje) {
        LocalDate daqui7Dias = hoje.plusDays(7);
        return new MetricasNutriProResult(
                carregarNomeEmpresa(empresaId),
                contar("select count(*) from clientes_pacientes where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_NUTRI),
                contarAgendaNutri(empresaId, hoje, hoje),
                contarAgendaNutri(empresaId, hoje, daqui7Dias),
                contar("select count(*) from servicos_procedimentos where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_NUTRI),
                contarDocumentosNutri(empresaId),
                contarAvaliacoesAntropometricas(empresaId),
                contarDocumentosNutriPorTipo(empresaId, "SOLICITACAO_EXAMES"),
                contarDocumentosNutriPorTipo(empresaId, "PRESCRICAO"),
                contarSimulacoesNutri(empresaId, false),
                contarSimulacoesNutri(empresaId, true),
                contarPlanosAtivosNutri(empresaId),
                listarPacientesRecentes(empresaId)
        );
    }

    @Override
    public List<PacienteNutriResumoResult> listarPacientesNutriPro(UUID empresaId, String busca) {
        String termo = busca == null || busca.isBlank() ? null : "%" + busca.trim().toLowerCase() + "%";
        if (termo == null) {
            return jdbcTemplate.query("""
                    select id, nome, telefone, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where empresa_id = ?
                      and area = ?
                    order by ativo desc, nome
                    limit 30
                    """, this::mapearPaciente, empresaId, AREA_NUTRI);
        }
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                  and (lower(nome) like ? or lower(coalesce(email, '')) like ? or lower(coalesce(telefone, '')) like ?)
                order by ativo desc, nome
                limit 30
                """, this::mapearPaciente, empresaId, AREA_NUTRI, termo, termo, termo);
    }

    @Override
    public Optional<DadosProntuarioNutriProResult> carregarProntuarioNutriPro(UUID empresaId, UUID pacienteId, LocalDate hoje) {
        return carregarPacienteProntuario(empresaId, pacienteId, hoje)
                .map(paciente -> new DadosProntuarioNutriProResult(
                        paciente,
                        new ResumoProntuarioNutriProResult(
                                contar("select count(*) from documentos_profissionais where empresa_id = ? and cliente_paciente_id = ? and ativo = true", empresaId, pacienteId),
                                contar("""
                                        select count(*)
                                        from agenda_compromissos
                                        where empresa_id = ?
                                          and cliente_paciente_id = ?
                                          and status <> 'CANCELADO'
                                          and inicio::date >= ?
                                        """, empresaId, pacienteId, hoje),
                                contarSimulacoesNutri(empresaId, false),
                                contarPlanosAtivosPaciente(empresaId, pacienteId),
                                contarPlanosAtivosPaciente(empresaId, pacienteId) > 0 ? "ATIVO" : "PREPARADO",
                                "PREPARADO",
                                existeAvaliacaoAntropometrica(empresaId, pacienteId) ? "DISPONIVEL" : "PROXIMA_TASK",
                                existeAvaliacaoAntropometrica(empresaId, pacienteId) ? "DISPONIVEL" : "PROXIMA_TASK",
                                existeDocumentoNutriTipo(empresaId, pacienteId, "SOLICITACAO_EXAMES") ? "DISPONIVEL" : "PREPARADO",
                                carregarUltimaConsulta(empresaId, pacienteId)
                        )
                ));
    }

    @Override
    public boolean existePacienteNutriPro(UUID empresaId, UUID pacienteId) {
        return contar("""
                select count(*)
                from clientes_pacientes
                where empresa_id = ?
                  and id = ?
                  and area = ?
                """, empresaId, pacienteId, AREA_NUTRI) > 0;
    }

    @Override
    public void salvarAvaliacaoAntropometrica(AvaliacaoAntropometricaNutriPro avaliacao) {
        jdbcTemplate.update("""
                insert into nutri_avaliacoes_antropometricas (
                    id, empresa_id, paciente_id, peso_kg, altura_cm, idade, sexo, imc, objetivo,
                    fator_atividade, geb_kcal, tmb_kcal, get_kcal, meta_energetica_kcal,
                    formula, aviso, observacoes, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                avaliacao.id(),
                avaliacao.empresaId(),
                avaliacao.pacienteId(),
                avaliacao.pesoKg(),
                avaliacao.alturaCm(),
                avaliacao.idade(),
                avaliacao.sexo().name(),
                avaliacao.imc(),
                avaliacao.objetivo().name(),
                avaliacao.fatorAtividade(),
                avaliacao.gebKcal(),
                avaliacao.tmbKcal(),
                avaliacao.getKcal(),
                avaliacao.metaEnergeticaKcal(),
                avaliacao.formula(),
                avaliacao.aviso(),
                avaliacao.observacoes(),
                Timestamp.from(avaliacao.criadoEm()),
                Timestamp.from(avaliacao.atualizadoEm())
        );
    }

    @Override
    public List<AvaliacaoAntropometricaNutriPro> listarAvaliacoesAntropometricas(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_avaliacoes_antropometricas
                where empresa_id = ?
                  and paciente_id = ?
                order by criado_em desc
                limit 20
                """, this::mapearAvaliacaoAntropometrica, empresaId, pacienteId);
    }

    @Override
    public Optional<AvaliacaoAntropometricaNutriPro> carregarAvaliacaoAntropometrica(UUID empresaId, UUID pacienteId, UUID avaliacaoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_avaliacoes_antropometricas
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearAvaliacaoAntropometrica, avaliacaoId, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void salvarPlanoAlimentar(PlanoAlimentarNutriPro plano) {
        jdbcTemplate.update("""
                insert into nutri_planos_alimentares (
                    id, empresa_id, paciente_id, objetivo, descricao, status,
                    energia_total_kcal, proteinas_total, carboidratos_total, lipidios_total,
                    criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                plano.id(),
                plano.empresaId(),
                plano.pacienteId(),
                plano.objetivo(),
                plano.descricao(),
                plano.status().name(),
                plano.energiaTotalKcal(),
                plano.proteinasTotal(),
                plano.carboidratosTotal(),
                plano.lipidiosTotal(),
                Timestamp.from(plano.criadoEm()),
                Timestamp.from(plano.atualizadoEm())
        );
        for (RefeicaoPlanoAlimentarNutriPro refeicao : plano.refeicoes()) {
            salvarRefeicao(refeicao);
        }
    }

    @Override
    public List<PlanoAlimentarNutriPro> listarPlanosAlimentares(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_planos_alimentares
                where empresa_id = ?
                  and paciente_id = ?
                order by criado_em desc
                limit 10
                """, this::mapearPlanoAlimentar, empresaId, pacienteId);
    }

    @Override
    public Optional<PlanoAlimentarNutriPro> carregarPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_planos_alimentares
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearPlanoAlimentar, planoId, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private void salvarRefeicao(RefeicaoPlanoAlimentarNutriPro refeicao) {
        jdbcTemplate.update("""
                insert into nutri_plano_refeicoes (
                    id, empresa_id, plano_id, nome, horario, observacoes, ordenacao,
                    energia_total_kcal, proteinas_total, carboidratos_total, lipidios_total
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                refeicao.id(),
                refeicao.empresaId(),
                refeicao.planoId(),
                refeicao.nome(),
                refeicao.horario(),
                refeicao.observacoes(),
                refeicao.ordenacao(),
                refeicao.energiaTotalKcal(),
                refeicao.proteinasTotal(),
                refeicao.carboidratosTotal(),
                refeicao.lipidiosTotal()
        );
        for (ItemPlanoAlimentarNutriPro item : refeicao.itens()) {
            salvarItemRefeicao(item);
            registrarItemNoBancoPersonalizado(item);
        }
    }

    private void salvarItemRefeicao(ItemPlanoAlimentarNutriPro item) {
        jdbcTemplate.update("""
                insert into nutri_refeicao_itens (
                    id, empresa_id, refeicao_id, tipo_item, nome, grupo, unidade_medida,
                    quantidade_base, quantidade, energia_kcal_base, proteinas_base, carboidratos_base,
                    lipidios_base, energia_kcal, proteinas, carboidratos, lipidios, observacoes, ordenacao
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                item.id(),
                item.empresaId(),
                item.refeicaoId(),
                item.tipoItem().name(),
                item.nome(),
                item.grupo(),
                item.unidadeMedida(),
                item.quantidadeBase(),
                item.quantidade(),
                item.energiaKcalBase(),
                item.proteinasBase(),
                item.carboidratosBase(),
                item.lipidiosBase(),
                item.energiaKcal(),
                item.proteinas(),
                item.carboidratos(),
                item.lipidios(),
                item.observacoes(),
                item.ordenacao()
        );
    }

    private void registrarItemNoBancoPersonalizado(ItemPlanoAlimentarNutriPro item) {
        if (item.tipoItem() == TipoItemPlanoAlimentarNutriPro.ALIMENTO) {
            registrarAlimentoPersonalizado(item);
            return;
        }
        registrarSuplementoPersonalizado(item);
    }

    private void registrarAlimentoPersonalizado(ItemPlanoAlimentarNutriPro item) {
        jdbcTemplate.update("""
                insert into nutri_alimentos_personalizados (
                    id, empresa_id, nome, grupo, unidade_medida, quantidade_base, energia_kcal_base,
                    proteinas_base, carboidratos_base, lipidios_base, ativo, criado_em, atualizado_em
                )
                select ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, now(), now()
                where not exists (
                    select 1 from nutri_alimentos_personalizados
                    where empresa_id = ? and lower(nome) = lower(?)
                )
                """,
                UUID.randomUUID(),
                item.empresaId(),
                item.nome(),
                item.grupo(),
                item.unidadeMedida(),
                item.quantidadeBase(),
                item.energiaKcalBase(),
                item.proteinasBase(),
                item.carboidratosBase(),
                item.lipidiosBase(),
                item.empresaId(),
                item.nome()
        );
    }

    private void registrarSuplementoPersonalizado(ItemPlanoAlimentarNutriPro item) {
        jdbcTemplate.update("""
                insert into nutri_suplementos_formulacoes (
                    id, empresa_id, nome, tipo, unidade_medida, quantidade_base, energia_kcal_base,
                    proteinas_base, carboidratos_base, lipidios_base, orientacao_uso, ativo, criado_em, atualizado_em
                )
                select ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, now(), now()
                where not exists (
                    select 1 from nutri_suplementos_formulacoes
                    where empresa_id = ? and lower(nome) = lower(?)
                )
                """,
                UUID.randomUUID(),
                item.empresaId(),
                item.nome(),
                item.grupo(),
                item.unidadeMedida(),
                item.quantidadeBase(),
                item.energiaKcalBase(),
                item.proteinasBase(),
                item.carboidratosBase(),
                item.lipidiosBase(),
                item.observacoes(),
                item.empresaId(),
                item.nome()
        );
    }

    private String carregarNomeEmpresa(UUID empresaId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select nome_fantasia from empresas where id = ?",
                    String.class,
                    empresaId
            );
        } catch (EmptyResultDataAccessException exception) {
            return "Empresa selecionada";
        }
    }

    private long contarAgendaNutri(UUID empresaId, LocalDate inicio, LocalDate fim) {
        return contar("""
                select count(*)
                from agenda_compromissos agenda
                left join clientes_pacientes cliente on cliente.id = agenda.cliente_paciente_id
                where agenda.empresa_id = ?
                  and agenda.status <> 'CANCELADO'
                  and agenda.inicio::date between ? and ?
                  and (cliente.area = ? or agenda.profissional_nome ilike '%nutri%')
                """, empresaId, inicio, fim, AREA_NUTRI);
    }

    private long contarDocumentosNutri(UUID empresaId) {
        return contar("""
                select count(*)
                from documentos_profissionais documento
                left join clientes_pacientes cliente on cliente.id = documento.cliente_paciente_id
                where documento.empresa_id = ?
                  and documento.ativo = true
                  and (
                    cliente.area = ?
                    or documento.tipo in ('SOLICITACAO_EXAMES', 'PRESCRICAO', 'PLANO_ALIMENTAR')
                    or documento.titulo ilike '%nutri%'
                  )
                """, empresaId, AREA_NUTRI);
    }

    private long contarAvaliacoesAntropometricas(UUID empresaId) {
        return contar("""
                select count(*)
                from nutri_avaliacoes_antropometricas avaliacao
                join clientes_pacientes paciente on paciente.id = avaliacao.paciente_id
                where avaliacao.empresa_id = ?
                  and paciente.area = ?
                """, empresaId, AREA_NUTRI);
    }

    private long contarDocumentosNutriPorTipo(UUID empresaId, String tipo) {
        return contar("""
                select count(*)
                from documentos_profissionais documento
                join clientes_pacientes paciente on paciente.id = documento.cliente_paciente_id
                where documento.empresa_id = ?
                  and documento.ativo = true
                  and paciente.area = ?
                  and documento.tipo = ?
                """, empresaId, AREA_NUTRI, tipo);
    }

    private boolean existeDocumentoNutriTipo(UUID empresaId, UUID pacienteId, String tipo) {
        return contar("""
                select count(*)
                from documentos_profissionais
                where empresa_id = ?
                  and cliente_paciente_id = ?
                  and ativo = true
                  and tipo = ?
                """, empresaId, pacienteId, tipo) > 0;
    }

    private long contarSimulacoesNutri(UUID empresaId, boolean somenteAlertas) {
        String filtroAlerta = somenteAlertas ? " and simulacao.status_margem <> 'SAUDAVEL'" : "";
        return contar("""
                select count(*)
                from precificacao_simulacoes simulacao
                left join servicos_procedimentos servico on servico.id = simulacao.servico_procedimento_id
                where simulacao.empresa_id = ?
                  and simulacao.ativo = true
                  and (servico.area = ? or simulacao.nome_procedimento ilike '%nutri%')
                """ + filtroAlerta, empresaId, AREA_NUTRI);
    }

    private long contarPlanosAtivosNutri(UUID empresaId) {
        return contar("""
                select count(*)
                from nutri_planos_alimentares plano
                join clientes_pacientes paciente on paciente.id = plano.paciente_id
                where plano.empresa_id = ?
                  and paciente.area = ?
                  and plano.status = 'ATIVO'
                """, empresaId, AREA_NUTRI);
    }

    private long contarPlanosAtivosPaciente(UUID empresaId, UUID pacienteId) {
        return contar("""
                select count(*)
                from nutri_planos_alimentares
                where empresa_id = ?
                  and paciente_id = ?
                  and status = 'ATIVO'
                """, empresaId, pacienteId);
    }

    private List<PacienteNutriResumoResult> listarPacientesRecentes(UUID empresaId) {
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                order by atualizado_em desc
                limit 5
                """, this::mapearPaciente, empresaId, AREA_NUTRI);
    }

    private Optional<PacienteProntuarioNutriProResult> carregarPacienteProntuario(UUID empresaId, UUID pacienteId, LocalDate hoje) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select id, empresa_id, nome, email, telefone, data_nascimento, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where id = ?
                      and empresa_id = ?
                      and area = ?
                    """, (rs, rowNum) -> mapearPacienteProntuario(rs, hoje), pacienteId, empresaId, AREA_NUTRI));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private boolean existeAvaliacaoAntropometrica(UUID empresaId, UUID pacienteId) {
        return contar("""
                select count(*)
                from nutri_avaliacoes_antropometricas
                where empresa_id = ?
                  and paciente_id = ?
                """, empresaId, pacienteId) > 0;
    }

    private PacienteProntuarioNutriProResult mapearPacienteProntuario(ResultSet rs, LocalDate hoje) throws SQLException {
        LocalDate dataNascimento = rs.getObject("data_nascimento", LocalDate.class);
        return new PacienteProntuarioNutriProResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("telefone"),
                dataNascimento,
                idade(dataNascimento, hoje),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private java.time.Instant carregarUltimaConsulta(UUID empresaId, UUID pacienteId) {
        try {
            var timestamp = jdbcTemplate.queryForObject("""
                    select max(inicio)
                    from agenda_compromissos
                    where empresa_id = ?
                      and cliente_paciente_id = ?
                      and status <> 'CANCELADO'
                      and inicio < now()
                    """, java.sql.Timestamp.class, empresaId, pacienteId);
            return timestamp == null ? null : timestamp.toInstant();
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private PacienteNutriResumoResult mapearPaciente(ResultSet rs, int rowNum) throws SQLException {
        return new PacienteNutriResumoResult(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                rs.getString("telefone"),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private AvaliacaoAntropometricaNutriPro mapearAvaliacaoAntropometrica(ResultSet rs, int rowNum) throws SQLException {
        return new AvaliacaoAntropometricaNutriPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                bigDecimal(rs, "peso_kg"),
                bigDecimal(rs, "altura_cm"),
                rs.getInt("idade"),
                SexoBiologicoNutriPro.deCodigo(rs.getString("sexo")),
                bigDecimal(rs, "imc"),
                ObjetivoNutricionalNutriPro.deCodigo(rs.getString("objetivo")),
                bigDecimal(rs, "fator_atividade"),
                bigDecimal(rs, "geb_kcal"),
                bigDecimal(rs, "tmb_kcal"),
                bigDecimal(rs, "get_kcal"),
                bigDecimal(rs, "meta_energetica_kcal"),
                rs.getString("formula"),
                rs.getString("aviso"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private PlanoAlimentarNutriPro mapearPlanoAlimentar(ResultSet rs, int rowNum) throws SQLException {
        UUID planoId = rs.getObject("id", UUID.class);
        UUID empresaId = rs.getObject("empresa_id", UUID.class);
        return new PlanoAlimentarNutriPro(
                planoId,
                empresaId,
                rs.getObject("paciente_id", UUID.class),
                rs.getString("objetivo"),
                rs.getString("descricao"),
                StatusPlanoAlimentarNutriPro.deCodigo(rs.getString("status")),
                carregarRefeicoesPlano(empresaId, planoId),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private List<RefeicaoPlanoAlimentarNutriPro> carregarRefeicoesPlano(UUID empresaId, UUID planoId) {
        return jdbcTemplate.query("""
                select *
                from nutri_plano_refeicoes
                where empresa_id = ?
                  and plano_id = ?
                order by ordenacao, nome
                """, this::mapearRefeicaoPlano, empresaId, planoId);
    }

    private RefeicaoPlanoAlimentarNutriPro mapearRefeicaoPlano(ResultSet rs, int rowNum) throws SQLException {
        UUID refeicaoId = rs.getObject("id", UUID.class);
        UUID empresaId = rs.getObject("empresa_id", UUID.class);
        return new RefeicaoPlanoAlimentarNutriPro(
                refeicaoId,
                empresaId,
                rs.getObject("plano_id", UUID.class),
                rs.getString("nome"),
                rs.getString("horario"),
                rs.getString("observacoes"),
                rs.getInt("ordenacao"),
                carregarItensRefeicao(empresaId, refeicaoId),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private List<ItemPlanoAlimentarNutriPro> carregarItensRefeicao(UUID empresaId, UUID refeicaoId) {
        return jdbcTemplate.query("""
                select *
                from nutri_refeicao_itens
                where empresa_id = ?
                  and refeicao_id = ?
                order by ordenacao, nome
                """, this::mapearItemRefeicao, empresaId, refeicaoId);
    }

    private ItemPlanoAlimentarNutriPro mapearItemRefeicao(ResultSet rs, int rowNum) throws SQLException {
        return new ItemPlanoAlimentarNutriPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("refeicao_id", UUID.class),
                TipoItemPlanoAlimentarNutriPro.deCodigo(rs.getString("tipo_item")),
                rs.getString("nome"),
                rs.getString("grupo"),
                rs.getString("unidade_medida"),
                bigDecimal(rs, "quantidade_base"),
                bigDecimal(rs, "quantidade"),
                bigDecimal(rs, "energia_kcal_base"),
                bigDecimal(rs, "proteinas_base"),
                bigDecimal(rs, "carboidratos_base"),
                bigDecimal(rs, "lipidios_base"),
                bigDecimal(rs, "energia_kcal"),
                bigDecimal(rs, "proteinas"),
                bigDecimal(rs, "carboidratos"),
                bigDecimal(rs, "lipidios"),
                rs.getString("observacoes"),
                rs.getInt("ordenacao")
        );
    }

    private BigDecimal bigDecimal(ResultSet rs, String coluna) throws SQLException {
        return rs.getBigDecimal(coluna);
    }

    private Integer idade(LocalDate dataNascimento, LocalDate hoje) {
        if (dataNascimento == null) {
            return null;
        }
        return Period.between(dataNascimento, hoje).getYears();
    }

    private long contar(String sql, Object... parametros) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class, parametros);
        return total == null ? 0 : total;
    }
}
