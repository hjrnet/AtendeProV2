package br.com.atendepro.modules.beauty.adapter.out.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.atendepro.modules.beauty.application.port.out.AtualizarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.AtualizarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarClienteBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarVisaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarClientesBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarEvidenciasEvolucaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarFichasEsteticasBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarProdutosEstoqueBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarProdutosUtilizadosBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarProtocolosBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarSessoesProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarTermosConsentimentoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarEvidenciaEvolucaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarProdutoUtilizadoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarSessaoProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarTermoConsentimentoBeautyProPort;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyProntuarioResult;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ProdutoBeautyEstoqueResult;
import br.com.atendepro.modules.beauty.domain.model.EvidenciaEvolucaoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ObjetivoEsteticoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ProdutoUtilizadoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.SessaoProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.StatusPacoteBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.StatusTermoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TermoConsentimentoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TipoPlaceholderEvolucaoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TipoProtocoloBeautyPro;

@Repository
@Profile("!test")
public class JdbcVisaoBeautyProAdapter implements
        CarregarVisaoBeautyProPort,
        ListarClientesBeautyProPort,
        CarregarClienteBeautyProPort,
        CarregarFichaEsteticaBeautyProPort,
        SalvarFichaEsteticaBeautyProPort,
        AtualizarFichaEsteticaBeautyProPort,
        ListarFichasEsteticasBeautyProPort,
        SalvarProtocoloBeautyProPort,
        AtualizarProtocoloBeautyProPort,
        CarregarProtocoloBeautyProPort,
        ListarProtocolosBeautyProPort,
        SalvarSessaoProtocoloBeautyProPort,
        ListarSessoesProtocoloBeautyProPort,
        SalvarTermoConsentimentoBeautyProPort,
        ListarTermosConsentimentoBeautyProPort,
        SalvarEvidenciaEvolucaoBeautyProPort,
        ListarEvidenciasEvolucaoBeautyProPort,
        SalvarProdutoUtilizadoBeautyProPort,
        ListarProdutosUtilizadosBeautyProPort,
        ListarProdutosEstoqueBeautyProPort {

    private static final String AREA_BEAUTY = "BEAUTY";

    private final JdbcTemplate jdbcTemplate;

    public JdbcVisaoBeautyProAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MetricasBeautyProResult carregarVisaoBeautyPro(UUID empresaId, LocalDate hoje) {
        LocalDate daqui7Dias = hoje.plusDays(7);
        return new MetricasBeautyProResult(
                carregarNomeEmpresa(empresaId),
                contar("select count(*) from clientes_pacientes where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_BEAUTY),
                contarAgendaBeauty(empresaId, hoje, hoje),
                contarAgendaBeauty(empresaId, hoje, daqui7Dias),
                contar("select count(*) from servicos_procedimentos where empresa_id = ? and area = ? and ativo = true", empresaId, AREA_BEAUTY),
                contar("select count(*) from estoque_produtos where empresa_id = ? and ativo = true", empresaId),
                contar("select count(*) from equipamentos where empresa_id = ? and ativo = true", empresaId),
                contarSimulacoesBeauty(empresaId, false),
                contarSimulacoesBeauty(empresaId, true),
                contar("select count(*) from beauty_protocolos where empresa_id = ? and status = 'ATIVO'", empresaId),
                contar("select count(*) from beauty_sessoes_protocolos where empresa_id = ?", empresaId),
                contar("select count(*) from beauty_termos_consentimento where empresa_id = ?", empresaId),
                contar("select count(*) from beauty_evidencias_evolucao where empresa_id = ?", empresaId),
                contar("select count(*) from beauty_produtos_utilizados where empresa_id = ?", empresaId),
                contar("""
                        select count(*)
                        from beauty_produtos_utilizados
                        where empresa_id = ?
                          and (alerta_validade = true or alerta_estoque_baixo = true)
                        """, empresaId),
                listarClientesRecentes(empresaId)
        );
    }

    @Override
    public List<ClienteBeautyResumoResult> listarClientesBeautyPro(UUID empresaId, String busca) {
        String termo = busca == null || busca.isBlank() ? null : "%" + busca.trim().toLowerCase() + "%";
        if (termo == null) {
            return jdbcTemplate.query("""
                    select id, nome, telefone, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where empresa_id = ?
                      and area = ?
                    order by ativo desc, nome
                    limit 30
                    """, this::mapearCliente, empresaId, AREA_BEAUTY);
        }
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                  and (lower(nome) like ? or lower(coalesce(email, '')) like ? or lower(coalesce(telefone, '')) like ?)
                order by ativo desc, nome
                limit 30
                """, this::mapearCliente, empresaId, AREA_BEAUTY, termo, termo, termo);
    }

    @Override
    public Optional<ClienteBeautyProntuarioResult> carregarClienteBeautyPro(UUID empresaId, UUID clienteId, LocalDate hoje) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select id, empresa_id, nome, email, telefone, data_nascimento, observacoes, ativo, atualizado_em
                    from clientes_pacientes
                    where id = ?
                      and empresa_id = ?
                      and area = ?
                    """, (rs, rowNum) -> mapearClienteProntuario(rs, hoje), clienteId, empresaId, AREA_BEAUTY));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<FichaEsteticaBeautyPro> carregarFichaAtual(UUID empresaId, UUID clienteId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from beauty_fichas_esteticas
                    where empresa_id = ?
                      and cliente_id = ?
                    order by atualizado_em desc
                    limit 1
                    """, this::mapearFichaEstetica, empresaId, clienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<FichaEsteticaBeautyPro> carregarFichaEstetica(UUID empresaId, UUID clienteId, UUID fichaId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from beauty_fichas_esteticas
                    where id = ?
                      and empresa_id = ?
                      and cliente_id = ?
                    """, this::mapearFichaEstetica, fichaId, empresaId, clienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void salvarFichaEstetica(FichaEsteticaBeautyPro ficha) {
        jdbcTemplate.update("""
                insert into beauty_fichas_esteticas (
                    id, empresa_id, cliente_id, objetivo, queixa_principal, historico_estetico,
                    alergias, medicamentos, gestante, lactante, sensibilidade_pele, usa_acidos,
                    exposicao_solar_intensa, procedimentos_recentes, contraindicacoes, observacoes,
                    criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ficha.id(),
                ficha.empresaId(),
                ficha.clienteId(),
                ficha.objetivo().name(),
                ficha.queixaPrincipal(),
                ficha.historicoEstetico(),
                ficha.alergias(),
                ficha.medicamentos(),
                ficha.gestante(),
                ficha.lactante(),
                ficha.sensibilidadePele(),
                ficha.usaAcidos(),
                ficha.exposicaoSolarIntensa(),
                ficha.procedimentosRecentes(),
                ficha.contraindicacoes(),
                ficha.observacoes(),
                Timestamp.from(ficha.criadoEm()),
                Timestamp.from(ficha.atualizadoEm())
        );
    }

    @Override
    public void atualizarFichaEstetica(FichaEsteticaBeautyPro ficha) {
        jdbcTemplate.update("""
                update beauty_fichas_esteticas
                set objetivo = ?,
                    queixa_principal = ?,
                    historico_estetico = ?,
                    alergias = ?,
                    medicamentos = ?,
                    gestante = ?,
                    lactante = ?,
                    sensibilidade_pele = ?,
                    usa_acidos = ?,
                    exposicao_solar_intensa = ?,
                    procedimentos_recentes = ?,
                    contraindicacoes = ?,
                    observacoes = ?,
                    atualizado_em = ?
                where id = ?
                  and empresa_id = ?
                  and cliente_id = ?
                """,
                ficha.objetivo().name(),
                ficha.queixaPrincipal(),
                ficha.historicoEstetico(),
                ficha.alergias(),
                ficha.medicamentos(),
                ficha.gestante(),
                ficha.lactante(),
                ficha.sensibilidadePele(),
                ficha.usaAcidos(),
                ficha.exposicaoSolarIntensa(),
                ficha.procedimentosRecentes(),
                ficha.contraindicacoes(),
                ficha.observacoes(),
                Timestamp.from(ficha.atualizadoEm()),
                ficha.id(),
                ficha.empresaId(),
                ficha.clienteId()
        );
    }

    @Override
    public List<FichaEsteticaBeautyPro> listarFichasEsteticas(UUID empresaId, UUID clienteId) {
        return jdbcTemplate.query("""
                select *
                from beauty_fichas_esteticas
                where empresa_id = ?
                  and cliente_id = ?
                order by atualizado_em desc
                limit 20
                """, this::mapearFichaEstetica, empresaId, clienteId);
    }

    @Override
    public void salvarProtocolo(ProtocoloBeautyPro protocolo) {
        jdbcTemplate.update("""
                insert into beauty_protocolos (
                    id, empresa_id, cliente_id, servico_procedimento_id, nome, tipo, objetivo,
                    quantidade_sessoes_previstas, sessoes_realizadas, status, observacoes,
                    criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                protocolo.id(),
                protocolo.empresaId(),
                protocolo.clienteId(),
                protocolo.servicoProcedimentoId(),
                protocolo.nome(),
                protocolo.tipo().name(),
                protocolo.objetivo(),
                protocolo.quantidadeSessoesPrevistas(),
                protocolo.sessoesRealizadas(),
                protocolo.status().name(),
                protocolo.observacoes(),
                Timestamp.from(protocolo.criadoEm()),
                Timestamp.from(protocolo.atualizadoEm())
        );
    }

    @Override
    public void atualizarProtocolo(ProtocoloBeautyPro protocolo) {
        jdbcTemplate.update("""
                update beauty_protocolos
                set sessoes_realizadas = ?,
                    status = ?,
                    atualizado_em = ?
                where id = ?
                  and empresa_id = ?
                  and cliente_id = ?
                """,
                protocolo.sessoesRealizadas(),
                protocolo.status().name(),
                Timestamp.from(protocolo.atualizadoEm()),
                protocolo.id(),
                protocolo.empresaId(),
                protocolo.clienteId()
        );
    }

    @Override
    public Optional<ProtocoloBeautyPro> carregarProtocolo(UUID empresaId, UUID clienteId, UUID protocoloId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select *
                    from beauty_protocolos
                    where id = ?
                      and empresa_id = ?
                      and cliente_id = ?
                    """, this::mapearProtocolo, protocoloId, empresaId, clienteId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<ProtocoloBeautyPro> listarProtocolos(UUID empresaId, UUID clienteId) {
        return jdbcTemplate.query("""
                select *
                from beauty_protocolos
                where empresa_id = ?
                  and cliente_id = ?
                order by atualizado_em desc
                limit 20
                """, this::mapearProtocolo, empresaId, clienteId);
    }

    @Override
    public void salvarSessao(SessaoProtocoloBeautyPro sessao) {
        jdbcTemplate.update("""
                insert into beauty_sessoes_protocolos (
                    id, empresa_id, protocolo_id, cliente_id, agenda_compromisso_id, numero_sessao,
                    realizada_em, descricao_execucao, evolucao_cliente, produtos_utilizados,
                    orientacoes, criado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                sessao.id(),
                sessao.empresaId(),
                sessao.protocoloId(),
                sessao.clienteId(),
                sessao.agendaCompromissoId(),
                sessao.numeroSessao(),
                Timestamp.from(sessao.realizadaEm()),
                sessao.descricaoExecucao(),
                sessao.evolucaoCliente(),
                sessao.produtosUtilizados(),
                sessao.orientacoes(),
                Timestamp.from(sessao.criadoEm())
        );
    }

    @Override
    public List<SessaoProtocoloBeautyPro> listarSessoes(UUID empresaId, UUID protocoloId) {
        return jdbcTemplate.query("""
                select *
                from beauty_sessoes_protocolos
                where empresa_id = ?
                  and protocolo_id = ?
                order by numero_sessao desc
                limit 50
                """, this::mapearSessao, empresaId, protocoloId);
    }

    @Override
    public void salvarTermoConsentimento(TermoConsentimentoBeautyPro termo) {
        jdbcTemplate.update("""
                insert into beauty_termos_consentimento (
                    id, empresa_id, cliente_id, protocolo_id, titulo, conteudo, status,
                    aceite_profissional, criado_em, atualizado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                termo.id(),
                termo.empresaId(),
                termo.clienteId(),
                termo.protocoloId(),
                termo.titulo(),
                termo.conteudo(),
                termo.status().name(),
                termo.aceiteProfissional(),
                Timestamp.from(termo.criadoEm()),
                Timestamp.from(termo.atualizadoEm())
        );
    }

    @Override
    public List<TermoConsentimentoBeautyPro> listarTermosConsentimento(UUID empresaId, UUID clienteId) {
        return jdbcTemplate.query("""
                select *
                from beauty_termos_consentimento
                where empresa_id = ?
                  and cliente_id = ?
                order by criado_em desc
                limit 20
                """, this::mapearTermoConsentimento, empresaId, clienteId);
    }

    @Override
    public void salvarEvidenciaEvolucao(EvidenciaEvolucaoBeautyPro evidencia) {
        jdbcTemplate.update("""
                insert into beauty_evidencias_evolucao (
                    id, empresa_id, cliente_id, protocolo_id, sessao_id, tipo_placeholder,
                    titulo, descricao, observacoes_privacidade, criado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                evidencia.id(),
                evidencia.empresaId(),
                evidencia.clienteId(),
                evidencia.protocoloId(),
                evidencia.sessaoId(),
                evidencia.tipoPlaceholder().name(),
                evidencia.titulo(),
                evidencia.descricao(),
                evidencia.observacoesPrivacidade(),
                Timestamp.from(evidencia.criadoEm())
        );
    }

    @Override
    public List<EvidenciaEvolucaoBeautyPro> listarEvidenciasEvolucao(UUID empresaId, UUID clienteId) {
        return jdbcTemplate.query("""
                select *
                from beauty_evidencias_evolucao
                where empresa_id = ?
                  and cliente_id = ?
                order by criado_em desc
                limit 20
                """, this::mapearEvidenciaEvolucao, empresaId, clienteId);
    }

    @Override
    public void salvarProdutoUtilizado(ProdutoUtilizadoBeautyPro produto) {
        jdbcTemplate.update("""
                insert into beauty_produtos_utilizados (
                    id, empresa_id, cliente_id, protocolo_id, sessao_id, produto_estoque_id,
                    nome_produto, lote, validade, quantidade, unidade, alerta_validade,
                    alerta_estoque_baixo, observacoes, criado_em
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                produto.id(),
                produto.empresaId(),
                produto.clienteId(),
                produto.protocoloId(),
                produto.sessaoId(),
                produto.produtoEstoqueId(),
                produto.nomeProduto(),
                produto.lote(),
                produto.validade(),
                produto.quantidade(),
                produto.unidade(),
                produto.alertaValidade(),
                produto.alertaEstoqueBaixo(),
                produto.observacoes(),
                Timestamp.from(produto.criadoEm())
        );
    }

    @Override
    public List<ProdutoUtilizadoBeautyPro> listarProdutosUtilizados(UUID empresaId, UUID clienteId) {
        return jdbcTemplate.query("""
                select *
                from beauty_produtos_utilizados
                where empresa_id = ?
                  and cliente_id = ?
                order by criado_em desc
                limit 30
                """, this::mapearProdutoUtilizado, empresaId, clienteId);
    }

    @Override
    public List<ProdutoBeautyEstoqueResult> listarProdutosEstoqueBeauty(UUID empresaId, LocalDate hoje) {
        return jdbcTemplate.query("""
                select id, nome, categoria, lote, validade, unidade, quantidade_atual, estoque_minimo
                from estoque_produtos
                where empresa_id = ?
                  and ativo = true
                order by
                  case
                    when lower(coalesce(categoria, '')) like '%estet%' then 0
                    when lower(coalesce(categoria, '')) like '%beleza%' then 1
                    when lower(coalesce(categoria, '')) like '%cosmet%' then 2
                    else 3
                  end,
                  nome
                limit 50
                """, (rs, rowNum) -> mapearProdutoEstoqueBeauty(rs, hoje), empresaId);
    }

    private String carregarNomeEmpresa(UUID empresaId) {
        String nome = jdbcTemplate.queryForObject("select nome_fantasia from empresas where id = ?", String.class, empresaId);
        if (nome == null || nome.isBlank()) {
            return "Empresa selecionada";
        }
        return nome;
    }

    private long contarAgendaBeauty(UUID empresaId, LocalDate inicio, LocalDate fim) {
        return contar("""
                select count(*)
                from agenda_compromissos agenda
                left join clientes_pacientes cliente on cliente.id = agenda.cliente_paciente_id
                where agenda.empresa_id = ?
                  and agenda.status <> 'CANCELADO'
                  and agenda.inicio::date between ? and ?
                  and (cliente.area = ? or agenda.profissional_nome ilike '%estetic%')
                """, empresaId, inicio, fim, AREA_BEAUTY);
    }

    private long contarSimulacoesBeauty(UUID empresaId, boolean somenteAlertas) {
        String filtroAlerta = somenteAlertas ? " and simulacao.status_margem <> 'SAUDAVEL'" : "";
        return contar("""
                select count(*)
                from precificacao_simulacoes simulacao
                left join servicos_procedimentos servico on servico.id = simulacao.servico_procedimento_id
                where simulacao.empresa_id = ?
                  and simulacao.ativo = true
                  and (
                    servico.area = ?
                    or simulacao.nome_procedimento ilike '%pele%'
                    or simulacao.nome_procedimento ilike '%massagem%'
                    or simulacao.nome_procedimento ilike '%peeling%'
                    or simulacao.nome_procedimento ilike '%estet%'
                  )
                """ + filtroAlerta, empresaId, AREA_BEAUTY);
    }

    private List<ClienteBeautyResumoResult> listarClientesRecentes(UUID empresaId) {
        return jdbcTemplate.query("""
                select id, nome, telefone, observacoes, ativo, atualizado_em
                from clientes_pacientes
                where empresa_id = ?
                  and area = ?
                order by atualizado_em desc
                limit 5
                """, this::mapearCliente, empresaId, AREA_BEAUTY);
    }

    private ClienteBeautyResumoResult mapearCliente(ResultSet rs, int rowNum) throws SQLException {
        return new ClienteBeautyResumoResult(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                rs.getString("telefone"),
                rs.getString("observacoes"),
                rs.getBoolean("ativo"),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private ClienteBeautyProntuarioResult mapearClienteProntuario(ResultSet rs, LocalDate hoje) throws SQLException {
        LocalDate dataNascimento = rs.getObject("data_nascimento", LocalDate.class);
        return new ClienteBeautyProntuarioResult(
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

    private FichaEsteticaBeautyPro mapearFichaEstetica(ResultSet rs, int rowNum) throws SQLException {
        return new FichaEsteticaBeautyPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                ObjetivoEsteticoBeautyPro.deCodigo(rs.getString("objetivo")),
                rs.getString("queixa_principal"),
                rs.getString("historico_estetico"),
                rs.getString("alergias"),
                rs.getString("medicamentos"),
                rs.getBoolean("gestante"),
                rs.getBoolean("lactante"),
                rs.getBoolean("sensibilidade_pele"),
                rs.getBoolean("usa_acidos"),
                rs.getBoolean("exposicao_solar_intensa"),
                rs.getString("procedimentos_recentes"),
                rs.getString("contraindicacoes"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private ProtocoloBeautyPro mapearProtocolo(ResultSet rs, int rowNum) throws SQLException {
        return new ProtocoloBeautyPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getObject("servico_procedimento_id", UUID.class),
                rs.getString("nome"),
                TipoProtocoloBeautyPro.deCodigo(rs.getString("tipo")),
                rs.getString("objetivo"),
                rs.getInt("quantidade_sessoes_previstas"),
                rs.getInt("sessoes_realizadas"),
                StatusPacoteBeautyPro.deCodigo(rs.getString("status")),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private SessaoProtocoloBeautyPro mapearSessao(ResultSet rs, int rowNum) throws SQLException {
        return new SessaoProtocoloBeautyPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("protocolo_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getObject("agenda_compromisso_id", UUID.class),
                rs.getInt("numero_sessao"),
                rs.getTimestamp("realizada_em").toInstant(),
                rs.getString("descricao_execucao"),
                rs.getString("evolucao_cliente"),
                rs.getString("produtos_utilizados"),
                rs.getString("orientacoes"),
                rs.getTimestamp("criado_em").toInstant()
        );
    }

    private TermoConsentimentoBeautyPro mapearTermoConsentimento(ResultSet rs, int rowNum) throws SQLException {
        return new TermoConsentimentoBeautyPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getObject("protocolo_id", UUID.class),
                rs.getString("titulo"),
                rs.getString("conteudo"),
                StatusTermoBeautyPro.deCodigo(rs.getString("status")),
                rs.getBoolean("aceite_profissional"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant()
        );
    }

    private EvidenciaEvolucaoBeautyPro mapearEvidenciaEvolucao(ResultSet rs, int rowNum) throws SQLException {
        return new EvidenciaEvolucaoBeautyPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getObject("protocolo_id", UUID.class),
                rs.getObject("sessao_id", UUID.class),
                TipoPlaceholderEvolucaoBeautyPro.deCodigo(rs.getString("tipo_placeholder")),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("observacoes_privacidade"),
                rs.getTimestamp("criado_em").toInstant()
        );
    }

    private ProdutoUtilizadoBeautyPro mapearProdutoUtilizado(ResultSet rs, int rowNum) throws SQLException {
        return new ProdutoUtilizadoBeautyPro(
                rs.getObject("id", UUID.class),
                rs.getObject("empresa_id", UUID.class),
                rs.getObject("cliente_id", UUID.class),
                rs.getObject("protocolo_id", UUID.class),
                rs.getObject("sessao_id", UUID.class),
                rs.getObject("produto_estoque_id", UUID.class),
                rs.getString("nome_produto"),
                rs.getString("lote"),
                rs.getObject("validade", LocalDate.class),
                rs.getBigDecimal("quantidade"),
                rs.getString("unidade"),
                rs.getBoolean("alerta_validade"),
                rs.getBoolean("alerta_estoque_baixo"),
                rs.getString("observacoes"),
                rs.getTimestamp("criado_em").toInstant()
        );
    }

    private ProdutoBeautyEstoqueResult mapearProdutoEstoqueBeauty(ResultSet rs, LocalDate hoje) throws SQLException {
        LocalDate validade = rs.getObject("validade", LocalDate.class);
        BigDecimal quantidadeAtual = rs.getBigDecimal("quantidade_atual");
        BigDecimal estoqueMinimo = rs.getBigDecimal("estoque_minimo");
        return new ProdutoBeautyEstoqueResult(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getString("lote"),
                validade,
                rs.getString("unidade"),
                quantidadeAtual,
                estoqueMinimo,
                quantidadeAtual != null && estoqueMinimo != null && quantidadeAtual.compareTo(estoqueMinimo) <= 0,
                validade != null && !validade.isAfter(hoje.plusDays(30))
        );
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
