package br.com.atendepro.modules.nutri.adapter.out.persistence;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import br.com.atendepro.modules.nutri.application.port.out.ExperienciaPacienteNutriProPort;
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
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.EvolucaoPacienteResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ExameAvancadoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.GrupoListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.IndicadorGerencialNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ItemListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.LembreteAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MaterialEducativoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MensagemAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MetaAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.PerfilCarteiraNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RelatorioGerencialNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RegistroDiarioResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.SubstituicaoAlimentarResult;
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
        CarregarPlanoAlimentarNutriProPort,
        ExperienciaPacienteNutriProPort {

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

    @Override
    public Optional<PlanoAlimentarNutriPro> publicarPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId) {
        Optional<PlanoAlimentarNutriPro> planoExistente = carregarPlanoAlimentar(empresaId, pacienteId, planoId);
        if (planoExistente.isEmpty()) {
            return Optional.empty();
        }

        jdbcTemplate.update("""
                update nutri_planos_alimentares
                set status = 'SUBSTITUIDO',
                    atualizado_em = now()
                where empresa_id = ?
                  and paciente_id = ?
                  and status = 'ATIVO'
                  and id <> ?
                """, empresaId, pacienteId, planoId);

        jdbcTemplate.update("""
                update nutri_planos_alimentares
                set status = 'ATIVO',
                    atualizado_em = now()
                where id = ?
                  and empresa_id = ?
                  and paciente_id = ?
                """, planoId, empresaId, pacienteId);

        return carregarPlanoAlimentar(empresaId, pacienteId, planoId);
    }

    @Override
    public Optional<PlanoAlimentarNutriPro> carregarPlanoPublicado(UUID empresaId, UUID pacienteId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_planos_alimentares
                    where empresa_id = ?
                      and paciente_id = ?
                      and status = 'ATIVO'
                    order by atualizado_em desc, criado_em desc
                    limit 1
                    """, this::mapearPlanoAlimentar, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PlanoAlimentarNutriPro> arquivarPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId) {
        Optional<PlanoAlimentarNutriPro> planoExistente = carregarPlanoAlimentar(empresaId, pacienteId, planoId);
        if (planoExistente.isEmpty()) {
            return Optional.empty();
        }

        jdbcTemplate.update("""
                update nutri_planos_alimentares
                set status = 'ARQUIVADO',
                    atualizado_em = now()
                where id = ?
                  and empresa_id = ?
                  and paciente_id = ?
                """, planoId, empresaId, pacienteId);

        return carregarPlanoAlimentar(empresaId, pacienteId, planoId);
    }

    @Override
    public void reorganizarRefeicoesPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId, List<UUID> refeicaoIds) {
        for (int i = 0; i < refeicaoIds.size(); i++) {
            jdbcTemplate.update("""
                    update nutri_plano_refeicoes
                    set ordenacao = ?
                    where id = ?
                      and plano_id = ?
                      and empresa_id = ?
                    """, i + 1, refeicaoIds.get(i), planoId, empresaId);
        }
    }

    @Override
    public Optional<ListaComprasResult> consultarListaCompras(UUID empresaId, UUID pacienteId, Clock clock) {
        Optional<PlanoAlimentarNutriPro> planoPublicado = carregarPlanoPublicado(empresaId, pacienteId);
        if (planoPublicado.isEmpty()) {
            return Optional.empty();
        }

        PlanoAlimentarNutriPro plano = planoPublicado.get();
        List<ItemListaComprasResult> itens = jdbcTemplate.query("""
                select
                    coalesce(nullif(item.grupo, ''), 'Geral') as categoria,
                    item.nome,
                    item.unidade_medida,
                    sum(item.quantidade) as quantidade,
                    string_agg(distinct refeicao.nome, ', ' order by refeicao.nome) as refeicoes,
                    string_agg(distinct nullif(item.observacoes, ''), ' | ') as observacoes
                from nutri_refeicao_itens item
                join nutri_plano_refeicoes refeicao on refeicao.id = item.refeicao_id
                where item.empresa_id = ?
                  and refeicao.plano_id = ?
                  and item.tipo_item = 'ALIMENTO'
                group by coalesce(nullif(item.grupo, ''), 'Geral'), item.nome, item.unidade_medida
                order by categoria, item.nome
                """, (rs, rowNum) -> new ItemListaComprasResult(
                rs.getString("nome"),
                rs.getString("categoria"),
                bigDecimal(rs, "quantidade"),
                rs.getString("unidade_medida"),
                rs.getString("refeicoes"),
                rs.getString("observacoes")
        ), empresaId, plano.id());

        Map<String, List<ItemListaComprasResult>> porCategoria = new LinkedHashMap<>();
        for (ItemListaComprasResult item : itens) {
            porCategoria.computeIfAbsent(item.categoria(), chave -> new ArrayList<>()).add(item);
        }

        List<GrupoListaComprasResult> grupos = porCategoria.entrySet().stream()
                .map(entry -> new GrupoListaComprasResult(entry.getKey(), entry.getValue()))
                .toList();

        return Optional.of(new ListaComprasResult(
                empresaId,
                pacienteId,
                plano.id(),
                plano.objetivo(),
                grupos,
                Instant.now(clock)
        ));
    }

    @Override
    public List<SubstituicaoAlimentarResult> listarSubstituicoesAlimentares(UUID empresaId, UUID pacienteId, UUID planoId) {
        return jdbcTemplate.query("""
                select *
                from nutri_substituicoes_alimentares
                where empresa_id = ?
                  and paciente_id = ?
                  and plano_id = ?
                  and ativo = true
                order by criado_em desc
                limit 100
                """, this::mapearSubstituicaoAlimentar, empresaId, pacienteId, planoId);
    }

    @Override
    public SubstituicaoAlimentarResult criarSubstituicaoAlimentar(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            UUID refeicaoId,
            String alimentoOrigem,
            String alimentoSubstituto,
            String grupo,
            String objetivo,
            String restricaoAlimentar,
            BigDecimal quantidadeEquivalente,
            String unidadeMedida,
            String observacoes
    ) {
        jdbcTemplate.update("""
                insert into nutri_substituicoes_alimentares (
                    id, empresa_id, paciente_id, plano_id, refeicao_id, alimento_origem, alimento_substituto,
                    grupo, objetivo, restricao_alimentar, quantidade_equivalente, unidade_medida, observacoes,
                    ativo, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, now(), now())
                """,
                id, empresaId, pacienteId, planoId, refeicaoId, alimentoOrigem, alimentoSubstituto,
                grupo, objetivo, restricaoAlimentar, quantidadeEquivalente, unidadeMedida, observacoes);
        return carregarSubstituicaoAlimentar(empresaId, pacienteId, id).orElseThrow();
    }

    @Override
    public List<MaterialEducativoResult> listarMateriaisEducativos(UUID empresaId, UUID pacienteId, UUID planoId) {
        return jdbcTemplate.query("""
                select *
                from nutri_materiais_educativos
                where empresa_id = ?
                  and paciente_id = ?
                  and plano_id = ?
                  and ativo = true
                order by criado_em desc
                limit 100
                """, this::mapearMaterialEducativo, empresaId, pacienteId, planoId);
    }

    @Override
    public MaterialEducativoResult criarMaterialEducativo(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String tipo,
            String titulo,
            String objetivo,
            String conteudo,
            String linkAnexo,
            String observacoes
    ) {
        jdbcTemplate.update("""
                insert into nutri_materiais_educativos (
                    id, empresa_id, paciente_id, plano_id, tipo, titulo, objetivo, conteudo,
                    link_anexo, observacoes, ativo, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, now(), now())
                """,
                id, empresaId, pacienteId, planoId, tipo, titulo, objetivo, conteudo, linkAnexo, observacoes);
        return carregarMaterialEducativo(empresaId, pacienteId, id).orElseThrow();
    }

    @Override
    public List<ExameAvancadoResult> listarExamesAvancados(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_exames_avancados
                where empresa_id = ?
                  and paciente_id = ?
                order by data_exame desc, criado_em desc
                limit 120
                """, this::mapearExameAvancado, empresaId, pacienteId);
    }

    @Override
    public ExameAvancadoResult criarExameAvancado(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String nome,
            BigDecimal valor,
            String unidadeMedida,
            LocalDate dataExame,
            String status,
            String observacoes
    ) {
        jdbcTemplate.update("""
                insert into nutri_exames_avancados (
                    id, empresa_id, paciente_id, tipo, nome, valor, unidade_medida, data_exame,
                    status, observacoes, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())
                """,
                id, empresaId, pacienteId, tipo, nome, valor, unidadeMedida, dataExame, status, observacoes);
        return carregarExameAvancado(empresaId, pacienteId, id).orElseThrow();
    }

    @Override
    public RelatorioGerencialNutriProResult consultarRelatorioGerencial(UUID empresaId, Clock clock) {
        long pacientesAtivos = contar("select count(*) from clientes_pacientes where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_NUTRI);
        long planosEmitidos = contar("select count(*) from nutri_planos_alimentares where empresa_id = ?", empresaId);
        long planosAtivos = contar("select count(*) from nutri_planos_alimentares where empresa_id = ? and status = 'ATIVO'", empresaId);
        long diarios30Dias = contar("""
                select count(*)
                from nutri_diario_alimentar
                where empresa_id = ?
                  and registrado_em >= now() - interval '30 days'
                """, empresaId);
        long diariosRevisados30Dias = contar("""
                select count(*)
                from nutri_diario_alimentar
                where empresa_id = ?
                  and status_revisao = 'REVISADO'
                  and registrado_em >= now() - interval '30 days'
                """, empresaId);
        long retornos30Dias = contar("""
                select count(*)
                from agenda_compromissos agenda
                join clientes_pacientes paciente on paciente.id = agenda.cliente_paciente_id
                where agenda.empresa_id = ?
                  and paciente.area = ?
                  and agenda.status <> 'CANCELADO'
                  and agenda.inicio::date between current_date and current_date + interval '30 days'
                """, empresaId, AREA_NUTRI);

        BigDecimal adesaoRevisao = diarios30Dias == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(diariosRevisados30Dias)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(diarios30Dias), 2, java.math.RoundingMode.HALF_EVEN);

        List<IndicadorGerencialNutriProResult> indicadores = List.of(
                new IndicadorGerencialNutriProResult("Pacientes ativos", BigDecimal.valueOf(pacientesAtivos), "pacientes"),
                new IndicadorGerencialNutriProResult("Planos emitidos", BigDecimal.valueOf(planosEmitidos), "planos"),
                new IndicadorGerencialNutriProResult("Planos ativos", BigDecimal.valueOf(planosAtivos), "planos"),
                new IndicadorGerencialNutriProResult("Registros de diario 30d", BigDecimal.valueOf(diarios30Dias), "registros"),
                new IndicadorGerencialNutriProResult("Adesao revisada 30d", adesaoRevisao, "%"),
                new IndicadorGerencialNutriProResult("Retornos proximos 30d", BigDecimal.valueOf(retornos30Dias), "retornos")
        );

        List<PerfilCarteiraNutriProResult> perfil = List.of(
                new PerfilCarteiraNutriProResult("Com plano ativo", contarPacientesComPlanoAtivo(empresaId)),
                new PerfilCarteiraNutriProResult("Com diario nos ultimos 30 dias", contarPacientesComDiarioRecente(empresaId)),
                new PerfilCarteiraNutriProResult("Com retorno agendado", contarPacientesComRetornoFuturo(empresaId)),
                new PerfilCarteiraNutriProResult("Sem plano ativo", Math.max(0, pacientesAtivos - contarPacientesComPlanoAtivo(empresaId)))
        );

        return new RelatorioGerencialNutriProResult(empresaId, Instant.now(clock), indicadores, perfil);
    }

    @Override
    public List<RegistroDiarioResult> listarDiarioAlimentar(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_diario_alimentar
                where empresa_id = ?
                  and paciente_id = ?
                order by registrado_em desc
                limit 60
                """, this::mapearRegistroDiario, empresaId, pacienteId);
    }

    @Override
    public RegistroDiarioResult criarRegistroDiario(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String refeicaoNome,
            String texto,
            String evidenciaUrl,
            String criadoPor,
            Clock clock
    ) {
        Instant agora = Instant.now(clock);
        jdbcTemplate.update("""
                insert into nutri_diario_alimentar (
                    id, empresa_id, paciente_id, plano_id, refeicao_nome, texto, evidencia_url,
                    status_revisao, parecer_profissional, criado_por, registrado_em, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, 'PENDENTE', null, ?, ?, ?, ?)
                """,
                id,
                empresaId,
                pacienteId,
                planoId,
                refeicaoNome,
                texto,
                evidenciaUrl,
                criadoPor,
                Timestamp.from(agora),
                Timestamp.from(agora),
                Timestamp.from(agora)
        );
        return carregarRegistroDiario(empresaId, pacienteId, id).orElseThrow();
    }

    @Override
    public Optional<RegistroDiarioResult> revisarRegistroDiario(UUID empresaId, UUID pacienteId, UUID registroId, String parecerProfissional) {
        int alterados = jdbcTemplate.update("""
                update nutri_diario_alimentar
                set status_revisao = 'REVISADO',
                    parecer_profissional = ?,
                    atualizado_em = now()
                where id = ?
                  and empresa_id = ?
                  and paciente_id = ?
                """, parecerProfissional, registroId, empresaId, pacienteId);
        if (alterados == 0) {
            return Optional.empty();
        }
        return carregarRegistroDiario(empresaId, pacienteId, registroId);
    }

    @Override
    public List<MetaAcompanhamentoResult> listarMetas(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_metas_acompanhamento
                where empresa_id = ?
                  and paciente_id = ?
                order by status, data_alvo nulls last, criado_em desc
                limit 50
                """, this::mapearMeta, empresaId, pacienteId);
    }

    @Override
    public MetaAcompanhamentoResult criarMeta(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String descricao,
            BigDecimal valorMeta,
            String unidade,
            LocalDate dataAlvo,
            Clock clock
    ) {
        LocalDate hoje = LocalDate.now(clock);
        Instant agora = Instant.now(clock);
        jdbcTemplate.update("""
                insert into nutri_metas_acompanhamento (
                    id, empresa_id, paciente_id, tipo, descricao, valor_meta, unidade,
                    data_inicio, data_alvo, status, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ATIVA', ?, ?)
                """,
                id,
                empresaId,
                pacienteId,
                tipo,
                descricao,
                valorMeta,
                unidade,
                hoje,
                dataAlvo,
                Timestamp.from(agora),
                Timestamp.from(agora)
        );
        return carregarMeta(empresaId, pacienteId, id).orElseThrow();
    }

    @Override
    public List<LembreteAcompanhamentoResult> listarLembretes(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_lembretes_acompanhamento
                where empresa_id = ?
                  and paciente_id = ?
                order by status, horario nulls last, criado_em desc
                limit 50
                """, this::mapearLembrete, empresaId, pacienteId);
    }

    @Override
    public LembreteAcompanhamentoResult criarLembrete(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String titulo,
            String descricao,
            String horario,
            String frequencia,
            Clock clock
    ) {
        Instant agora = Instant.now(clock);
        jdbcTemplate.update("""
                insert into nutri_lembretes_acompanhamento (
                    id, empresa_id, paciente_id, titulo, descricao, horario,
                    frequencia, status, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, 'ATIVO', ?, ?)
                """,
                id,
                empresaId,
                pacienteId,
                titulo,
                descricao,
                horario,
                frequencia,
                Timestamp.from(agora),
                Timestamp.from(agora)
        );
        return carregarLembrete(empresaId, pacienteId, id).orElseThrow();
    }

    @Override
    public List<MensagemAcompanhamentoResult> listarMensagens(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select *
                from nutri_mensagens_acompanhamento
                where empresa_id = ?
                  and paciente_id = ?
                order by enviada_em desc
                limit 80
                """, this::mapearMensagem, empresaId, pacienteId);
    }

    @Override
    public MensagemAcompanhamentoResult enviarMensagem(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String remetenteTipo,
            String remetenteNome,
            String texto,
            String contexto,
            Clock clock
    ) {
        Instant agora = Instant.now(clock);
        boolean lidaPeloPaciente = "PACIENTE".equals(remetenteTipo);
        boolean lidaPeloProfissional = "PROFISSIONAL".equals(remetenteTipo);
        jdbcTemplate.update("""
                insert into nutri_mensagens_acompanhamento (
                    id, empresa_id, paciente_id, remetente_tipo, remetente_nome, texto,
                    contexto, lida_pelo_paciente, lida_pelo_profissional, enviada_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                empresaId,
                pacienteId,
                remetenteTipo,
                remetenteNome,
                texto,
                contexto,
                lidaPeloPaciente,
                lidaPeloProfissional,
                Timestamp.from(agora)
        );
        return carregarMensagem(empresaId, pacienteId, id).orElseThrow();
    }

    @Override
    public void marcarMensagensLidas(UUID empresaId, UUID pacienteId, String leitor) {
        String coluna = "PACIENTE".equals(leitor) ? "lida_pelo_paciente" : "lida_pelo_profissional";
        String sql = """
                update nutri_mensagens_acompanhamento
                set %s = true
                where empresa_id = ?
                  and paciente_id = ?
                """.formatted(coluna);
        jdbcTemplate.update(sql, empresaId, pacienteId);
    }

    @Override
    public List<EvolucaoPacienteResult> listarEvolucao(UUID empresaId, UUID pacienteId) {
        return jdbcTemplate.query("""
                select 'PLANO' as tipo, objetivo as titulo, coalesce(descricao, 'Plano alimentar ativo no acompanhamento.') as descricao,
                       status, atualizado_em as data
                from nutri_planos_alimentares
                where empresa_id = ? and paciente_id = ? and status = 'ATIVO'
                union all
                select 'DIARIO' as tipo, coalesce(refeicao_nome, 'Registro alimentar') as titulo, texto as descricao,
                       status_revisao as status, registrado_em as data
                from nutri_diario_alimentar
                where empresa_id = ? and paciente_id = ?
                union all
                select 'META' as tipo, tipo as titulo, descricao, status, atualizado_em as data
                from nutri_metas_acompanhamento
                where empresa_id = ? and paciente_id = ?
                union all
                select 'MENSAGEM' as tipo, contexto as titulo, texto as descricao, remetente_tipo as status, enviada_em as data
                from nutri_mensagens_acompanhamento
                where empresa_id = ? and paciente_id = ?
                order by data desc
                limit 80
                """, this::mapearEvolucao, empresaId, pacienteId, empresaId, pacienteId, empresaId, pacienteId, empresaId, pacienteId);
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

    private Optional<SubstituicaoAlimentarResult> carregarSubstituicaoAlimentar(UUID empresaId, UUID pacienteId, UUID id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_substituicoes_alimentares
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearSubstituicaoAlimentar, id, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private SubstituicaoAlimentarResult mapearSubstituicaoAlimentar(ResultSet rs, int rowNum) throws SQLException {
        return new SubstituicaoAlimentarResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                rs.getObject("plano_id", UUID.class),
                rs.getObject("refeicao_id", UUID.class),
                rs.getString("alimento_origem"),
                rs.getString("alimento_substituto"),
                rs.getString("grupo"),
                rs.getString("objetivo"),
                rs.getString("restricao_alimentar"),
                bigDecimal(rs, "quantidade_equivalente"),
                rs.getString("unidade_medida"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private Optional<MaterialEducativoResult> carregarMaterialEducativo(UUID empresaId, UUID pacienteId, UUID id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_materiais_educativos
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearMaterialEducativo, id, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private MaterialEducativoResult mapearMaterialEducativo(ResultSet rs, int rowNum) throws SQLException {
        return new MaterialEducativoResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                rs.getObject("plano_id", UUID.class),
                rs.getString("tipo"),
                rs.getString("titulo"),
                rs.getString("objetivo"),
                rs.getString("conteudo"),
                rs.getString("link_anexo"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private Optional<ExameAvancadoResult> carregarExameAvancado(UUID empresaId, UUID pacienteId, UUID id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_exames_avancados
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearExameAvancado, id, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private ExameAvancadoResult mapearExameAvancado(ResultSet rs, int rowNum) throws SQLException {
        return new ExameAvancadoResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                rs.getString("tipo"),
                rs.getString("nome"),
                bigDecimal(rs, "valor"),
                rs.getString("unidade_medida"),
                rs.getObject("data_exame", LocalDate.class),
                rs.getString("status"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private Optional<RegistroDiarioResult> carregarRegistroDiario(UUID empresaId, UUID pacienteId, UUID registroId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_diario_alimentar
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearRegistroDiario, registroId, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private Optional<MetaAcompanhamentoResult> carregarMeta(UUID empresaId, UUID pacienteId, UUID metaId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_metas_acompanhamento
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearMeta, metaId, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private Optional<LembreteAcompanhamentoResult> carregarLembrete(UUID empresaId, UUID pacienteId, UUID lembreteId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_lembretes_acompanhamento
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearLembrete, lembreteId, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private Optional<MensagemAcompanhamentoResult> carregarMensagem(UUID empresaId, UUID pacienteId, UUID mensagemId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from nutri_mensagens_acompanhamento
                    where id = ?
                      and empresa_id = ?
                      and paciente_id = ?
                    """, this::mapearMensagem, mensagemId, empresaId, pacienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private RegistroDiarioResult mapearRegistroDiario(ResultSet rs, int rowNum) throws SQLException {
        return new RegistroDiarioResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                rs.getObject("plano_id", UUID.class),
                rs.getString("refeicao_nome"),
                rs.getString("texto"),
                rs.getString("evidencia_url"),
                rs.getString("status_revisao"),
                rs.getString("parecer_profissional"),
                rs.getString("criado_por"),
                rs.getTimestamp("registrado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private MetaAcompanhamentoResult mapearMeta(ResultSet rs, int rowNum) throws SQLException {
        return new MetaAcompanhamentoResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                rs.getString("tipo"),
                rs.getString("descricao"),
                bigDecimal(rs, "valor_meta"),
                rs.getString("unidade"),
                rs.getObject("data_inicio", LocalDate.class),
                rs.getObject("data_alvo", LocalDate.class),
                rs.getString("status"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private LembreteAcompanhamentoResult mapearLembrete(ResultSet rs, int rowNum) throws SQLException {
        return new LembreteAcompanhamentoResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("horario"),
                rs.getString("frequencia"),
                rs.getString("status"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private MensagemAcompanhamentoResult mapearMensagem(ResultSet rs, int rowNum) throws SQLException {
        return new MensagemAcompanhamentoResult(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("paciente_id", UUID.class),
                rs.getString("remetente_tipo"),
                rs.getString("remetente_nome"),
                rs.getString("texto"),
                rs.getString("contexto"),
                rs.getBoolean("lida_pelo_paciente"),
                rs.getBoolean("lida_pelo_profissional"),
                rs.getTimestamp("enviada_em").toInstant()
        );
    }

    private EvolucaoPacienteResult mapearEvolucao(ResultSet rs, int rowNum) throws SQLException {
        return new EvolucaoPacienteResult(
                rs.getString("tipo"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("status"),
                rs.getTimestamp("data").toInstant()
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

    private long contarPacientesComPlanoAtivo(UUID empresaId) {
        return contar("""
                select count(distinct paciente.id)
                from clientes_pacientes paciente
                join nutri_planos_alimentares plano on plano.paciente_id = paciente.id
                where paciente.empresa_id = ?
                  and paciente.area = ?
                  and paciente.ativo = true
                  and plano.status = 'ATIVO'
                """, empresaId, AREA_NUTRI);
    }

    private long contarPacientesComDiarioRecente(UUID empresaId) {
        return contar("""
                select count(distinct paciente.id)
                from clientes_pacientes paciente
                join nutri_diario_alimentar diario on diario.paciente_id = paciente.id
                where paciente.empresa_id = ?
                  and paciente.area = ?
                  and paciente.ativo = true
                  and diario.registrado_em >= now() - interval '30 days'
                """, empresaId, AREA_NUTRI);
    }

    private long contarPacientesComRetornoFuturo(UUID empresaId) {
        return contar("""
                select count(distinct paciente.id)
                from clientes_pacientes paciente
                join agenda_compromissos agenda on agenda.cliente_paciente_id = paciente.id
                where paciente.empresa_id = ?
                  and paciente.area = ?
                  and paciente.ativo = true
                  and agenda.status <> 'CANCELADO'
                  and agenda.inicio::date >= current_date
                """, empresaId, AREA_NUTRI);
    }

    private long contar(String sql, Object... parametros) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class, parametros);
        return total == null ? 0 : total;
    }
}
