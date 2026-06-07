package br.com.atendepro.modules.suporte.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.suporte.application.port.in.GerarRespostaAssistidaChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.out.CarregarChamadoSuportePorIdPort;
import br.com.atendepro.modules.suporte.application.port.out.ListarMensagensChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.result.RespostaAssistidaChamadoSuporteResult;
import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class SuporteAssistidoService implements GerarRespostaAssistidaChamadoSuporteUseCase {

    private final CarregarChamadoSuportePorIdPort carregarChamadoSuportePorIdPort;
    private final ListarMensagensChamadoSuportePort listarMensagensChamadoSuportePort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public SuporteAssistidoService(
            CarregarChamadoSuportePorIdPort carregarChamadoSuportePorIdPort,
            ListarMensagensChamadoSuportePort listarMensagensChamadoSuportePort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarChamadoSuportePorIdPort = carregarChamadoSuportePorIdPort;
        this.listarMensagensChamadoSuportePort = listarMensagensChamadoSuportePort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public RespostaAssistidaChamadoSuporteResult gerarRespostaAssistida(UUID chamadoId) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_CHAMADOS);
        ChamadoSuporte chamado = carregarChamadoSuportePorIdPort.carregarChamadoPorId(chamadoId)
                .orElseThrow(() -> new BusinessException("SUPORTE_CHAMADO_NAO_ENCONTRADO", "Chamado de suporte nao encontrado."));
        tenantAccessService.validarAcessoEmpresa(chamado.empresaId());
        List<MensagemChamadoSuporte> mensagens = listarMensagensChamadoSuportePort.listarMensagens(chamado.id());
        String texto = textoAnalise(chamado, mensagens);
        PrioridadeChamadoSuporte prioridade = prioridadeSugerida(chamado, texto);
        String categoria = categoriaSugerida(chamado, texto);

        return new RespostaAssistidaChamadoSuporteResult(
                chamado.id(),
                chamado.empresaId(),
                chamado.titulo(),
                categoria,
                prioridade,
                StatusChamadoSuporte.EM_ATENDIMENTO,
                resumo(chamado, categoria, prioridade),
                respostaSugerida(chamado, categoria),
                proximasAcoes(categoria, prioridade),
                Instant.now(clock)
        );
    }

    private String textoAnalise(ChamadoSuporte chamado, List<MensagemChamadoSuporte> mensagens) {
        StringBuilder texto = new StringBuilder();
        texto.append(chamado.titulo()).append(' ').append(chamado.descricao()).append(' ');
        mensagens.forEach(mensagem -> texto.append(mensagem.mensagem()).append(' '));
        return texto.toString().toLowerCase(Locale.ROOT);
    }

    private PrioridadeChamadoSuporte prioridadeSugerida(ChamadoSuporte chamado, String texto) {
        if (texto.contains("fora do ar") || texto.contains("nao consigo acessar") || texto.contains("pagamento duplicado")) {
            return PrioridadeChamadoSuporte.CRITICA;
        }
        if (texto.contains("erro") || texto.contains("trav") || texto.contains("nao atualiza") || texto.contains("login")) {
            return PrioridadeChamadoSuporte.ALTA;
        }
        return chamado.prioridade();
    }

    private String categoriaSugerida(ChamadoSuporte chamado, String texto) {
        if (chamado.categoria() != null) {
            return chamado.categoria();
        }
        if (texto.contains("login") || texto.contains("senha") || texto.contains("acessar")) {
            return "acesso";
        }
        if (texto.contains("pagamento") || texto.contains("assinatura") || texto.contains("plano")) {
            return "assinatura";
        }
        if (texto.contains("agenda")) {
            return "agenda";
        }
        if (texto.contains("precificacao") || texto.contains("preco") || texto.contains("margem")) {
            return "precificacao";
        }
        return "operacional";
    }

    private String resumo(ChamadoSuporte chamado, String categoria, PrioridadeChamadoSuporte prioridade) {
        return "Chamado classificado como " + categoria + " com prioridade sugerida " + prioridade.name()
                + " a partir do titulo: " + chamado.titulo() + ".";
    }

    private String respostaSugerida(ChamadoSuporte chamado, String categoria) {
        String nome = chamado.solicitanteNome() == null ? "Olá" : "Olá, " + chamado.solicitanteNome();
        return nome + ". Recebemos seu chamado sobre " + categoria
                + " e ja estamos analisando. Vamos verificar os detalhes informados e retornar com a proxima orientacao neste atendimento.";
    }

    private List<String> proximasAcoes(String categoria, PrioridadeChamadoSuporte prioridade) {
        List<String> acoes = new ArrayList<>();
        acoes.add("Confirmar dados do ambiente, horario do erro e usuario afetado.");
        if (prioridade == PrioridadeChamadoSuporte.CRITICA) {
            acoes.add("Acionar responsavel tecnico antes de responder com prazo final.");
        }
        if ("assinatura".equals(categoria)) {
            acoes.add("Verificar plano, status da assinatura e historico de cobrancas.");
        }
        if ("acesso".equals(categoria)) {
            acoes.add("Validar e-mail de login, perfil de acesso e tenant vinculado.");
        }
        return acoes;
    }
}
