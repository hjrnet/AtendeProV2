package br.com.atendepro.modules.growth.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.AtualizarEtapaLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.AtualizarVinculosLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.command.GrowthCommands.RegistrarLeadGrowthCommand;
import br.com.atendepro.modules.growth.application.port.in.GrowthUseCase;
import br.com.atendepro.modules.growth.application.port.out.GrowthPort;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ApresentacaoDemoGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.ClientePosVendaGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.IndicadorVerticalGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.LeadGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.PainelGrowthResult;
import br.com.atendepro.modules.growth.application.result.GrowthResults.SugestaoPosVendaIAResult;
import br.com.atendepro.modules.growth.domain.model.EtapaLeadGrowth;
import br.com.atendepro.modules.growth.domain.model.PerfilDemoGrowth;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class GrowthService implements GrowthUseCase {

    private static final int DIAS_RETORNO_NUTRI = 30;
    private static final int DIAS_RETORNO_BEAUTY = 21;
    private static final int DIAS_SEM_CONTATO = 30;
    private static final int DIAS_ABANDONO = 75;

    private final GrowthPort growthPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public GrowthService(
            GrowthPort growthPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.growthPort = growthPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public PainelGrowthResult consultarPainel(UUID empresaId) {
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        return new PainelGrowthResult(
                empresaResolvida,
                growthPort.listarLeads(empresaResolvida, null, null, null),
                listarSugestoesPosVenda(empresaResolvida, null),
                growthPort.carregarIndicadoresVerticais(empresaResolvida),
                growthPort.listarApresentacoesDemo(empresaResolvida, null),
                Instant.now(clock)
        );
    }

    @Override
    public List<LeadGrowthResult> listarLeads(UUID empresaId, AreaCliente vertical, EtapaLeadGrowth etapa, String busca) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        AreaCliente verticalValidada = validarVerticalOpcional(vertical);
        return growthPort.listarLeads(empresaResolvida, verticalValidada, etapa, busca);
    }

    @Override
    public LeadGrowthResult registrarLead(RegistrarLeadGrowthCommand command) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(command.empresaId());
        AreaCliente vertical = validarVerticalObrigatoria(command.vertical());
        String nome = validarTexto(command.nome(), "GROWTH_LEAD_NOME_OBRIGATORIO", "Nome do lead e obrigatorio.");
        String email = validarTexto(command.email(), "GROWTH_LEAD_EMAIL_OBRIGATORIO", "E-mail do lead e obrigatorio.");
        String origem = validarTexto(command.origem(), "GROWTH_LEAD_ORIGEM_OBRIGATORIA", "Origem do lead e obrigatoria.");
        BigDecimal potencial = command.potencialMensal() == null ? BigDecimal.ZERO : command.potencialMensal().max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        RegistrarLeadGrowthCommand normalizado = new RegistrarLeadGrowthCommand(
                empresaResolvida,
                nome,
                email,
                textoOuNulo(command.telefone()),
                vertical,
                origem,
                command.etapa() == null ? EtapaLeadGrowth.NOVO : command.etapa(),
                potencial,
                command.clientePacienteId(),
                command.compromissoAgendaId(),
                textoOuNulo(command.observacoes())
        );
        return growthPort.salvarLead(UUID.randomUUID(), normalizado, Instant.now(clock));
    }

    @Override
    public Optional<LeadGrowthResult> atualizarEtapa(AtualizarEtapaLeadGrowthCommand command) {
        validarPermissao();
        if (command.leadId() == null || command.etapa() == null) {
            throw new BusinessException("GROWTH_LEAD_ETAPA_OBRIGATORIA", "Lead e etapa sao obrigatorios para atualizar funil.");
        }
        UUID empresaResolvida = resolverEmpresaId(command.empresaId());
        return growthPort.atualizarEtapaLead(empresaResolvida, command.leadId(), command.etapa(), Instant.now(clock));
    }

    @Override
    public Optional<LeadGrowthResult> atualizarVinculos(AtualizarVinculosLeadGrowthCommand command) {
        validarPermissao();
        if (command.leadId() == null) {
            throw new BusinessException("GROWTH_LEAD_OBRIGATORIO", "Lead e obrigatorio para atualizar vinculos.");
        }
        UUID empresaResolvida = resolverEmpresaId(command.empresaId());
        return growthPort.atualizarVinculosLead(empresaResolvida, command.leadId(), command.clientePacienteId(), command.compromissoAgendaId(), Instant.now(clock));
    }

    @Override
    public List<SugestaoPosVendaIAResult> listarSugestoesPosVenda(UUID empresaId, AreaCliente vertical) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        AreaCliente verticalValidada = validarVerticalOpcional(vertical);
        LocalDate hoje = LocalDate.now(clock);
        Instant agora = Instant.now(clock);
        return growthPort.carregarClientesPosVenda(empresaResolvida, verticalValidada).stream()
                .map(cliente -> montarSugestao(cliente, hoje, agora))
                .sorted(Comparator.comparing(SugestaoPosVendaIAResult::prioridade))
                .limit(24)
                .toList();
    }

    @Override
    public List<IndicadorVerticalGrowthResult> listarIndicadoresVerticais(UUID empresaId) {
        validarPermissao();
        return growthPort.carregarIndicadoresVerticais(resolverEmpresaId(empresaId));
    }

    @Override
    public List<ApresentacaoDemoGrowthResult> listarApresentacoesDemo(UUID empresaId, PerfilDemoGrowth perfil) {
        validarPermissao();
        return growthPort.listarApresentacoesDemo(resolverEmpresaId(empresaId), perfil);
    }

    private SugestaoPosVendaIAResult montarSugestao(ClientePosVendaGrowthResult cliente, LocalDate hoje, Instant agora) {
        LocalDate retorno = calcularRetorno(cliente);
        String tipo = "CHECKIN";
        String prioridade = "3_BAIXA";
        String motivo = "Manter relacionamento ativo e registrar proximos passos.";
        String oportunidade = "Pacote de acompanhamento mensal";

        if (cliente.ultimaNotaNps() != null && cliente.ultimaNotaNps() <= 6) {
            tipo = "RECUPERACAO_EXPERIENCIA";
            prioridade = "1_ALTA";
            motivo = "NPS detrator exige contato humano rapido antes de perder o cliente.";
            oportunidade = "Acolhimento e replanejamento sem venda imediata";
        } else if (cliente.proximaConsultaEm() == null && referenciaInatividade(cliente).isBefore(agora.minus(DIAS_ABANDONO, ChronoUnit.DAYS))) {
            tipo = "RISCO_ABANDONO";
            prioridade = "1_ALTA";
            motivo = "Cliente sem retorno futuro e com janela longa desde ultimo atendimento.";
            oportunidade = cliente.vertical() == AreaCliente.BEAUTY ? "Manutencao ou pacote de sessoes" : "Retorno nutricional e revisao de plano";
        } else if (cliente.faltasRecentes() > 0) {
            tipo = "REATIVACAO_APOS_FALTA";
            prioridade = "2_MEDIA";
            motivo = "Falta ou cancelamento recente precisa de reagendamento acolhedor.";
            oportunidade = "Novo horario com orientacao curta de continuidade";
        } else if (!retorno.isAfter(hoje)) {
            tipo = "RETORNO_RECOMENDADO";
            prioridade = "2_MEDIA";
            motivo = "Cliente chegou na janela ideal de retorno para a vertical.";
            oportunidade = cliente.vertical() == AreaCliente.BEAUTY ? "Manutencao de protocolo" : "Ajuste de plano e metas";
        } else if (cliente.ultimoContatoEm() == null || cliente.ultimoContatoEm().isBefore(agora.minus(DIAS_SEM_CONTATO, ChronoUnit.DAYS))) {
            tipo = "CHECKIN_CONSULTIVO";
            prioridade = "3_BAIXA";
            motivo = "Cliente sem toque recente de pos-venda.";
        }

        return new SugestaoPosVendaIAResult(
                cliente.id(),
                cliente.nome(),
                cliente.vertical(),
                tipo,
                prioridade,
                motivo,
                retorno,
                mensagem(cliente, tipo),
                oportunidade
        );
    }

    private LocalDate calcularRetorno(ClientePosVendaGrowthResult cliente) {
        Instant referencia = referenciaInatividade(cliente);
        int dias = cliente.vertical() == AreaCliente.BEAUTY ? DIAS_RETORNO_BEAUTY : DIAS_RETORNO_NUTRI;
        return referencia.atZone(ZoneOffset.UTC).toLocalDate().plusDays(dias);
    }

    private Instant referenciaInatividade(ClientePosVendaGrowthResult cliente) {
        if (cliente.ultimaConsultaEm() != null) {
            return cliente.ultimaConsultaEm();
        }
        return cliente.atualizadoEm() == null ? Instant.now(clock) : cliente.atualizadoEm();
    }

    private String mensagem(ClientePosVendaGrowthResult cliente, String tipo) {
        String nome = cliente.nome();
        return switch (tipo) {
            case "RECUPERACAO_EXPERIENCIA" -> "Oi, " + nome + ". Vi seu feedback e queria entender melhor como podemos ajustar sua experiencia. Posso te ouvir rapidamente?";
            case "RISCO_ABANDONO" -> "Oi, " + nome + ". Passando para saber como voce esta e te ajudar a retomar o acompanhamento no melhor ritmo para voce.";
            case "REATIVACAO_APOS_FALTA" -> "Oi, " + nome + ". Tudo bem por ai? Posso te mandar algumas opcoes de horario para remarcarmos com tranquilidade?";
            case "RETORNO_RECOMENDADO" -> "Oi, " + nome + ". Chegou uma boa janela para revisarmos seus resultados e ajustar os proximos passos. Quer que eu veja um horario?";
            default -> "Oi, " + nome + ". Como voce esta depois do ultimo atendimento? Se quiser, me mande um ponto positivo e uma duvida para eu te orientar.";
        };
    }

    private AreaCliente validarVerticalObrigatoria(AreaCliente vertical) {
        AreaCliente validada = validarVerticalOpcional(vertical);
        if (validada == null) {
            throw new BusinessException("GROWTH_VERTICAL_OBRIGATORIA", "Vertical e obrigatoria para Growth.");
        }
        return validada;
    }

    private AreaCliente validarVerticalOpcional(AreaCliente vertical) {
        if (vertical == null) {
            return null;
        }
        if (vertical != AreaCliente.NUTRI && vertical != AreaCliente.BEAUTY) {
            throw new BusinessException("GROWTH_VERTICAL_PRIORITARIA", "Growth R19 atende primeiro NUTRI e BEAUTY.");
        }
        return vertical;
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada) {
        validarPermissao();
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException("GROWTH_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar Growth.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_VERTICAIS);
        permissaoAcessoService.validarPermissao(PermissaoAcesso.VISUALIZAR_EMPRESA);
    }

    private String validarTexto(String valor, String codigo, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(codigo, mensagem);
        }
        return valor.trim();
    }

    private String textoOuNulo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
