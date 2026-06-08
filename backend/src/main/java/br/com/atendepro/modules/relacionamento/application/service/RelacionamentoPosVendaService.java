package br.com.atendepro.modules.relacionamento.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.relacionamento.application.command.CriarTarefaRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarContatoRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.command.RegistrarPesquisaNpsRelacionamentoCommand;
import br.com.atendepro.modules.relacionamento.application.port.in.RelacionamentoPosVendaUseCase;
import br.com.atendepro.modules.relacionamento.application.port.out.DadosPosVenda;
import br.com.atendepro.modules.relacionamento.application.port.out.RelacionamentoPosVendaPort;
import br.com.atendepro.modules.relacionamento.application.result.PainelPosVendaResult;
import br.com.atendepro.modules.relacionamento.domain.model.CanalContatoRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.StatusTarefaRelacionamento;
import br.com.atendepro.modules.relacionamento.domain.model.TipoTarefaRelacionamento;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class RelacionamentoPosVendaService implements RelacionamentoPosVendaUseCase {

    private static final int DIAS_RETORNO_NUTRI = 30;
    private static final int DIAS_RETORNO_BEAUTY = 21;
    private static final int DIAS_SEM_CONTATO = 30;
    private static final int DIAS_INATIVIDADE = 90;

    private final RelacionamentoPosVendaPort relacionamentoPosVendaPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public RelacionamentoPosVendaService(
            RelacionamentoPosVendaPort relacionamentoPosVendaPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.relacionamentoPosVendaPort = relacionamentoPosVendaPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public PainelPosVendaResult consultarPainel(UUID empresaId, AreaCliente area, String busca) {
        validarPermissao();
        AreaCliente areaValidada = validarAreaPrioritaria(area);
        UUID empresaResolvida = resolverEmpresaId(empresaId, "RELACIONAMENTO_EMPRESA_OBRIGATORIA");
        LocalDate hoje = LocalDate.now(clock);
        Instant agora = Instant.now(clock);
        DadosPosVenda dados = relacionamentoPosVendaPort.carregarDadosPosVenda(empresaResolvida, areaValidada, busca, hoje);
        List<PainelPosVendaResult.Cliente> clientes = dados.clientes().stream()
                .map(cliente -> mapearCliente(cliente, hoje))
                .sorted(Comparator.comparing(PainelPosVendaResult.Cliente::riscoAbandono).reversed())
                .toList();
        List<PainelPosVendaResult.Tarefa> tarefas = new ArrayList<>();
        tarefas.addAll(dados.tarefas().stream().map(this::mapearTarefa).toList());
        tarefas.addAll(gerarTarefasAutomaticas(clientes, agora));
        tarefas.sort(Comparator
                .comparing(PainelPosVendaResult.Tarefa::dataRecomendada, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(PainelPosVendaResult.Tarefa::titulo));
        return new PainelPosVendaResult(
                empresaResolvida,
                areaValidada,
                calcularMetricas(clientes),
                clientes,
                tarefas.stream().limit(24).toList(),
                templates(areaValidada),
                dados.contatosRecentes().stream().map(this::mapearContato).toList(),
                dados.npsRecentes().stream().map(this::mapearNps).toList(),
                segmentos(clientes, areaValidada),
                agora
        );
    }

    @Override
    public PainelPosVendaResult.Contato registrarContato(RegistrarContatoRelacionamentoCommand command) {
        validarPermissao();
        validarCommandContato(command);
        UUID empresaResolvida = resolverEmpresaId(command.empresaId(), "RELACIONAMENTO_EMPRESA_OBRIGATORIA");
        DadosPosVenda.Contato contato = relacionamentoPosVendaPort.salvarContato(
                new RegistrarContatoRelacionamentoCommand(
                        empresaResolvida,
                        command.clienteId(),
                        validarAreaPrioritaria(command.area()),
                        command.canal() == null ? CanalContatoRelacionamento.WHATSAPP : command.canal(),
                        command.templateCodigo(),
                        command.mensagem(),
                        command.observacoes()
                ),
                Instant.now(clock)
        );
        return mapearContato(contato);
    }

    @Override
    public PainelPosVendaResult.PesquisaNps registrarPesquisaNps(RegistrarPesquisaNpsRelacionamentoCommand command) {
        validarPermissao();
        validarCommandNps(command);
        UUID empresaResolvida = resolverEmpresaId(command.empresaId(), "RELACIONAMENTO_EMPRESA_OBRIGATORIA");
        DadosPosVenda.PesquisaNps pesquisa = relacionamentoPosVendaPort.salvarPesquisaNps(
                new RegistrarPesquisaNpsRelacionamentoCommand(
                        empresaResolvida,
                        command.clienteId(),
                        validarAreaPrioritaria(command.area()),
                        command.nota(),
                        command.comentario(),
                        command.origem()
                ),
                Instant.now(clock)
        );
        return mapearNps(pesquisa);
    }

    @Override
    public PainelPosVendaResult.Tarefa criarTarefa(CriarTarefaRelacionamentoCommand command) {
        validarPermissao();
        validarCommandTarefa(command);
        UUID empresaResolvida = resolverEmpresaId(command.empresaId(), "RELACIONAMENTO_EMPRESA_OBRIGATORIA");
        DadosPosVenda.Tarefa tarefa = relacionamentoPosVendaPort.salvarTarefa(
                new CriarTarefaRelacionamentoCommand(
                        empresaResolvida,
                        command.clienteId(),
                        validarAreaPrioritaria(command.area()),
                        command.tipo() == null ? TipoTarefaRelacionamento.OUTRO : command.tipo(),
                        command.titulo(),
                        command.descricao(),
                        command.dataRecomendada(),
                        command.origem()
                ),
                Instant.now(clock)
        );
        return mapearTarefa(tarefa);
    }

    @Override
    public Optional<PainelPosVendaResult.Tarefa> concluirTarefa(UUID empresaId, UUID tarefaId) {
        validarPermissao();
        if (tarefaId == null) {
            throw new BusinessException("RELACIONAMENTO_TAREFA_OBRIGATORIA", "Tarefa e obrigatoria para concluir pos-venda.");
        }
        UUID empresaResolvida = resolverEmpresaId(empresaId, "RELACIONAMENTO_EMPRESA_OBRIGATORIA");
        return relacionamentoPosVendaPort.concluirTarefa(empresaResolvida, tarefaId, Instant.now(clock))
                .map(this::mapearTarefa);
    }

    private PainelPosVendaResult.Cliente mapearCliente(DadosPosVenda.Cliente cliente, LocalDate hoje) {
        LocalDate retornoRecomendado = calcularRetornoRecomendado(cliente);
        String status = statusAcompanhamento(cliente, retornoRecomendado, hoje);
        boolean aniversarioProximo = aniversarioProximo(cliente.dataNascimento(), hoje);
        boolean oportunidadeRecorrencia = cliente.protocolosAtivos() > 0 || cliente.planosAtivos() > 0 || "RETORNO_PENDENTE".equals(status);
        return new PainelPosVendaResult.Cliente(
                cliente.id(),
                cliente.nome(),
                cliente.area(),
                cliente.email(),
                cliente.telefone(),
                cliente.dataNascimento(),
                cliente.ultimaConsultaEm(),
                cliente.proximaConsultaEm(),
                cliente.ultimoContatoEm(),
                cliente.faltasRecentes(),
                cliente.ultimaNotaNps(),
                status,
                rotuloStatus(status),
                retornoRecomendado,
                motivoRetorno(cliente, status),
                risco(cliente, status),
                aniversarioProximo,
                oportunidadeRecorrencia
        );
    }

    private PainelPosVendaResult.Tarefa mapearTarefa(DadosPosVenda.Tarefa tarefa) {
        return new PainelPosVendaResult.Tarefa(
                tarefa.id(),
                tarefa.clienteId(),
                tarefa.clienteNome(),
                tarefa.area(),
                tarefa.tipo(),
                tarefa.titulo(),
                tarefa.descricao(),
                tarefa.dataRecomendada(),
                tarefa.status(),
                tarefa.origem(),
                tarefa.criadoEm(),
                tarefa.atualizadoEm()
        );
    }

    private PainelPosVendaResult.Contato mapearContato(DadosPosVenda.Contato contato) {
        return new PainelPosVendaResult.Contato(
                contato.id(),
                contato.clienteId(),
                contato.clienteNome(),
                contato.area(),
                contato.canal(),
                contato.templateCodigo(),
                contato.mensagem(),
                contato.observacoes(),
                contato.criadoEm()
        );
    }

    private PainelPosVendaResult.PesquisaNps mapearNps(DadosPosVenda.PesquisaNps pesquisa) {
        return new PainelPosVendaResult.PesquisaNps(
                pesquisa.id(),
                pesquisa.clienteId(),
                pesquisa.clienteNome(),
                pesquisa.area(),
                pesquisa.nota(),
                pesquisa.comentario(),
                pesquisa.origem(),
                pesquisa.criadoEm()
        );
    }

    private List<PainelPosVendaResult.Tarefa> gerarTarefasAutomaticas(List<PainelPosVendaResult.Cliente> clientes, Instant agora) {
        LocalDate hoje = LocalDate.now(clock);
        return clientes.stream()
                .flatMap(cliente -> tarefasAutomaticasCliente(cliente, hoje, agora).stream())
                .toList();
    }

    private List<PainelPosVendaResult.Tarefa> tarefasAutomaticasCliente(PainelPosVendaResult.Cliente cliente, LocalDate hoje, Instant agora) {
        List<PainelPosVendaResult.Tarefa> tarefas = new ArrayList<>();
        if ("FALTA_RECENTE".equals(cliente.statusAcompanhamento())) {
            tarefas.add(tarefaAutomatica(cliente, TipoTarefaRelacionamento.REATIVACAO, "Reativar apos falta", "Enviar mensagem acolhedora e oferecer novo horario.", hoje, agora));
        }
        if ("RETORNO_PENDENTE".equals(cliente.statusAcompanhamento())) {
            tarefas.add(tarefaAutomatica(cliente, TipoTarefaRelacionamento.RETORNO, "Agendar retorno", cliente.motivoRetorno(), cliente.retornoRecomendadoEm(), agora));
        }
        if ("SEM_CONTATO".equals(cliente.statusAcompanhamento())) {
            tarefas.add(tarefaAutomatica(cliente, TipoTarefaRelacionamento.CHECKIN, "Enviar check-in", "Cliente sem contato recente. Fazer acompanhamento ativo antes de virar abandono.", hoje.plusDays(1), agora));
        }
        if (cliente.ultimaNotaNps() != null && cliente.ultimaNotaNps() <= 6) {
            tarefas.add(tarefaAutomatica(cliente, TipoTarefaRelacionamento.NPS, "Acolher NPS detrator", "Entrar em contato para entender insatisfacao e recuperar experiencia.", hoje, agora));
        }
        if (cliente.aniversarioProximo()) {
            tarefas.add(tarefaAutomatica(cliente, TipoTarefaRelacionamento.ANIVERSARIO, "Mensagem de aniversario", "Enviar mensagem humana e convite leve para retorno.", hoje.plusDays(2), agora));
        }
        return tarefas;
    }

    private PainelPosVendaResult.Tarefa tarefaAutomatica(
            PainelPosVendaResult.Cliente cliente,
            TipoTarefaRelacionamento tipo,
            String titulo,
            String descricao,
            LocalDate dataRecomendada,
            Instant agora
    ) {
        return new PainelPosVendaResult.Tarefa(
                null,
                cliente.id(),
                cliente.nome(),
                cliente.area(),
                tipo,
                titulo,
                descricao,
                dataRecomendada,
                StatusTarefaRelacionamento.PENDENTE,
                "AUTOMATICA",
                agora,
                agora
        );
    }

    private PainelPosVendaResult.Metricas calcularMetricas(List<PainelPosVendaResult.Cliente> clientes) {
        int retornos = (int) clientes.stream().filter(cliente -> "RETORNO_PENDENTE".equals(cliente.statusAcompanhamento())).count();
        int inativos = (int) clientes.stream().filter(cliente -> "INATIVO".equals(cliente.statusAcompanhamento())).count();
        int faltas = clientes.stream().mapToInt(PainelPosVendaResult.Cliente::faltasRecentes).sum();
        int semContato = (int) clientes.stream().filter(cliente -> "SEM_CONTATO".equals(cliente.statusAcompanhamento())).count();
        int oportunidades = (int) clientes.stream().filter(PainelPosVendaResult.Cliente::oportunidadeRecorrencia).count();
        var notas = clientes.stream().map(PainelPosVendaResult.Cliente::ultimaNotaNps).filter(Objects::nonNull).toList();
        double npsMedio = notas.isEmpty() ? 0 : notas.stream().mapToInt(Integer::intValue).average().orElse(0);
        int detratores = (int) notas.stream().filter(nota -> nota <= 6).count();
        return new PainelPosVendaResult.Metricas(clientes.size(), retornos, inativos, faltas, semContato, oportunidades, npsMedio, detratores);
    }

    private List<PainelPosVendaResult.SegmentoCampanha> segmentos(List<PainelPosVendaResult.Cliente> clientes, AreaCliente area) {
        List<PainelPosVendaResult.SegmentoCampanha> segmentos = new ArrayList<>();
        segmentos.add(segmento("retorno-pendente", "Retornos pendentes", "Clientes com janela recomendada de retorno aberta.", clientes, cliente -> "RETORNO_PENDENTE".equals(cliente.statusAcompanhamento()), "Enviar convite direto para agenda"));
        segmentos.add(segmento("sem-contato", "Sem contato recente", "Carteira sem toque humano nos ultimos 30 dias.", clientes, cliente -> "SEM_CONTATO".equals(cliente.statusAcompanhamento()), "Disparar check-in manual"));
        segmentos.add(segmento("nps-detrator", "NPS detrator", "Notas ate 6 exigem acolhimento rapido.", clientes, cliente -> cliente.ultimaNotaNps() != null && cliente.ultimaNotaNps() <= 6, "Ligar ou mandar mensagem personalizada"));
        segmentos.add(segmento("recorrencia", area == AreaCliente.BEAUTY ? "Pacotes e manutencao" : "Planos ativos e retorno", "Oportunidades de continuidade sem venda agressiva.", clientes, PainelPosVendaResult.Cliente::oportunidadeRecorrencia, "Oferecer proximo passo do acompanhamento"));
        return segmentos;
    }

    private PainelPosVendaResult.SegmentoCampanha segmento(
            String codigo,
            String titulo,
            String descricao,
            List<PainelPosVendaResult.Cliente> clientes,
            java.util.function.Predicate<PainelPosVendaResult.Cliente> filtro,
            String acao
    ) {
        return new PainelPosVendaResult.SegmentoCampanha(codigo, titulo, descricao, (int) clientes.stream().filter(filtro).count(), acao);
    }

    private LocalDate calcularRetornoRecomendado(DadosPosVenda.Cliente cliente) {
        Instant base = cliente.ultimaConsultaEm() == null ? cliente.atualizadoEm() : cliente.ultimaConsultaEm();
        int dias = cliente.area() == AreaCliente.BEAUTY ? DIAS_RETORNO_BEAUTY : DIAS_RETORNO_NUTRI;
        return base.atZone(ZoneOffset.UTC).toLocalDate().plusDays(dias);
    }

    private String statusAcompanhamento(DadosPosVenda.Cliente cliente, LocalDate retornoRecomendado, LocalDate hoje) {
        if (cliente.proximaConsultaEm() != null && cliente.proximaConsultaEm().isAfter(Instant.now(clock))) {
            return "ACOMPANHAMENTO_ATIVO";
        }
        if (cliente.faltasRecentes() > 0) {
            return "FALTA_RECENTE";
        }
        Instant referenciaInatividade = cliente.ultimaConsultaEm() == null ? cliente.atualizadoEm() : cliente.ultimaConsultaEm();
        if (referenciaInatividade.isBefore(Instant.now(clock).minus(DIAS_INATIVIDADE, ChronoUnit.DAYS))) {
            return "INATIVO";
        }
        if (!retornoRecomendado.isAfter(hoje)) {
            return "RETORNO_PENDENTE";
        }
        if (cliente.ultimoContatoEm() == null || cliente.ultimoContatoEm().isBefore(Instant.now(clock).minus(DIAS_SEM_CONTATO, ChronoUnit.DAYS))) {
            return "SEM_CONTATO";
        }
        return "ACOMPANHAMENTO_ATIVO";
    }

    private String rotuloStatus(String status) {
        return switch (status) {
            case "RETORNO_PENDENTE" -> "Retorno pendente";
            case "FALTA_RECENTE" -> "Falta/cancelamento recente";
            case "INATIVO" -> "Risco de abandono";
            case "SEM_CONTATO" -> "Sem contato recente";
            default -> "Acompanhamento ativo";
        };
    }

    private String motivoRetorno(DadosPosVenda.Cliente cliente, String status) {
        return switch (status) {
            case "FALTA_RECENTE" -> "Cliente teve cancelamento/falta recente. Priorizar reativacao sem tom de cobranca.";
            case "INATIVO" -> "Cliente passou da janela de acompanhamento e precisa de reconexao.";
            case "SEM_CONTATO" -> "Cliente esta sem contato recente. Enviar check-in consultivo.";
            default -> cliente.area() == AreaCliente.BEAUTY
                    ? "Janela ideal para manutencao, nova sessao ou orientacao pos-procedimento."
                    : "Janela ideal para retorno nutricional, ajuste de plano ou revisao de exames.";
        };
    }

    private String risco(DadosPosVenda.Cliente cliente, String status) {
        if (cliente.ultimaNotaNps() != null && cliente.ultimaNotaNps() <= 6) {
            return "ALTO";
        }
        if ("INATIVO".equals(status) || "FALTA_RECENTE".equals(status)) {
            return "ALTO";
        }
        if ("RETORNO_PENDENTE".equals(status) || "SEM_CONTATO".equals(status)) {
            return "MEDIO";
        }
        return "BAIXO";
    }

    private boolean aniversarioProximo(LocalDate nascimento, LocalDate hoje) {
        if (nascimento == null) {
            return false;
        }
        LocalDate aniversario = nascimento.withYear(hoje.getYear());
        if (aniversario.isBefore(hoje)) {
            aniversario = aniversario.plusYears(1);
        }
        return !aniversario.isAfter(hoje.plusDays(30));
    }

    private List<PainelPosVendaResult.TemplateMensagem> templates(AreaCliente area) {
        List<PainelPosVendaResult.TemplateMensagem> templates = new ArrayList<>();
        if (area == null || area == AreaCliente.NUTRI) {
            templates.add(new PainelPosVendaResult.TemplateMensagem("nutri-checkin", AreaCliente.NUTRI, "Check-in do plano", "Acompanhar adesao", "Oi, {{nome}}! Como foi sua semana com o plano alimentar? Se quiser, me mande um ponto facil e um ponto dificil para ajustarmos juntos.", List.of("nome")));
            templates.add(new PainelPosVendaResult.TemplateMensagem("nutri-retorno", AreaCliente.NUTRI, "Convite para retorno", "Trazer paciente de volta", "Oi, {{nome}}! Ja estamos na janela ideal para revisar seu progresso e ajustar o plano. Quer que eu te envie dois horarios para retorno?", List.of("nome")));
            templates.add(new PainelPosVendaResult.TemplateMensagem("nutri-exames", AreaCliente.NUTRI, "Lembrete de exames", "Apoiar continuidade clinica", "Oi, {{nome}}! Passando para lembrar dos exames combinados. Com eles consigo ajustar sua conduta com mais seguranca.", List.of("nome")));
        }
        if (area == null || area == AreaCliente.BEAUTY) {
            templates.add(new PainelPosVendaResult.TemplateMensagem("beauty-pos-procedimento", AreaCliente.BEAUTY, "Pos-procedimento", "Cuidado e seguranca", "Oi, {{nome}}! Como sua pele/regiao tratada esta hoje? Lembre de seguir os cuidados combinados e me chame se notar qualquer desconforto fora do esperado.", List.of("nome")));
            templates.add(new PainelPosVendaResult.TemplateMensagem("beauty-manutencao", AreaCliente.BEAUTY, "Manutencao do resultado", "Recorrencia sem pressao", "Oi, {{nome}}! Estamos chegando na janela ideal para manter seu resultado. Quer que eu te envie opcoes de horario?", List.of("nome")));
            templates.add(new PainelPosVendaResult.TemplateMensagem("beauty-homecare", AreaCliente.BEAUTY, "Home care", "Orientar uso correto", "Oi, {{nome}}! Passando para reforcar o uso dos produtos orientados. A constancia em casa ajuda muito no resultado do procedimento.", List.of("nome")));
        }
        templates.add(new PainelPosVendaResult.TemplateMensagem("nps-satisfacao", area == null ? AreaCliente.GERAL : area, "Pesquisa rapida", "Medir satisfacao", "Oi, {{nome}}! Em uma escala de 0 a 10, quanto voce indicaria meu atendimento? Se puder, me diga tambem o principal motivo da nota.", List.of("nome")));
        return templates;
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada, String codigoErro) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException(codigoErro, "Empresa e obrigatoria para operar pos-venda.");
        }
        return empresaIdSolicitada;
    }

    private AreaCliente validarAreaPrioritaria(AreaCliente area) {
        if (area == null) {
            return null;
        }
        if (area != AreaCliente.NUTRI && area != AreaCliente.BEAUTY) {
            throw new BusinessException("RELACIONAMENTO_AREA_INVALIDA", "Pos-venda R16 atende primeiro Nutri e Beauty.");
        }
        return area;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_CLIENTES);
    }

    private void validarCommandContato(RegistrarContatoRelacionamentoCommand command) {
        if (command == null || command.clienteId() == null || command.mensagem() == null || command.mensagem().isBlank()) {
            throw new BusinessException("RELACIONAMENTO_CONTATO_INVALIDO", "Cliente e mensagem sao obrigatorios para registrar contato.");
        }
    }

    private void validarCommandNps(RegistrarPesquisaNpsRelacionamentoCommand command) {
        if (command == null || command.clienteId() == null || command.nota() < 0 || command.nota() > 10) {
            throw new BusinessException("RELACIONAMENTO_NPS_INVALIDO", "NPS deve ter cliente e nota entre 0 e 10.");
        }
    }

    private void validarCommandTarefa(CriarTarefaRelacionamentoCommand command) {
        if (command == null || command.clienteId() == null || command.titulo() == null || command.titulo().isBlank()) {
            throw new BusinessException("RELACIONAMENTO_TAREFA_INVALIDA", "Cliente e titulo sao obrigatorios para criar tarefa.");
        }
    }
}
