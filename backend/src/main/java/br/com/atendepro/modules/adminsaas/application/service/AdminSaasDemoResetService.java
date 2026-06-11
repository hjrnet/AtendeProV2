package br.com.atendepro.modules.adminsaas.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.command.ResetarDemoAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.command.RegistrarEventoAuditoriaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.port.in.ResetarDemoAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.out.RepopularDadosDemoPort;
import br.com.atendepro.modules.adminsaas.application.port.out.RegistrarEventoAuditoriaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.ResetDemoAdminSaasResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.domain.exception.ValidationException;

@Service
@Profile("local")
public class AdminSaasDemoResetService implements ResetarDemoAdminSaasUseCase {

    private static final String AMBIENTE_LOCAL = "local";

    private final PermissaoAcessoService permissaoAcessoService;
    private final RepopularDadosDemoPort repopularDadosDemoPort;
    private final RegistrarEventoAuditoriaAdminSaasPort registrarEventoAuditoriaAdminSaasPort;
    private final Clock clock;

    public AdminSaasDemoResetService(
            PermissaoAcessoService permissaoAcessoService,
            RepopularDadosDemoPort repopularDadosDemoPort,
            RegistrarEventoAuditoriaAdminSaasPort registrarEventoAuditoriaAdminSaasPort,
            Clock clock
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.repopularDadosDemoPort = repopularDadosDemoPort;
        this.registrarEventoAuditoriaAdminSaasPort = registrarEventoAuditoriaAdminSaasPort;
        this.clock = clock;
    }

    @Override
    public ResetDemoAdminSaasResult resetarDemo(ResetarDemoAdminSaasCommand command) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
        if (command == null || command.perfil() == null) {
            throw new ValidationException("Perfil demo e obrigatorio.");
        }

        boolean executado = command.confirmarReset();
        if (executado) {
            repopularDadosDemoPort.repopularDadosDemo();
        }
        registrarEventoAuditoriaAdminSaasPort.registrarEvento(new RegistrarEventoAuditoriaAdminSaasCommand(
                executado ? "DEMO_RESET_EXECUTADO" : "DEMO_RESET_PREPARADO",
                executado ? "ALTA" : "MEDIA",
                executado
                        ? "Reset demo executado pelo Admin SaaS local."
                        : "Reset demo preparado pelo Admin SaaS local.",
                empresaAtualId(),
                usuarioAtualId(),
                "DEMO",
                null,
                Map.of(
                        "perfil", command.perfil().name(),
                        "ambiente", AMBIENTE_LOCAL,
                        "confirmarReset", Boolean.toString(command.confirmarReset())
                )
        ));

        return new ResetDemoAdminSaasResult(
                command.perfil(),
                command.perfil().rotulo(),
                executado ? "RESET_EXECUTADO" : "RESET_PREPARADO",
                executado,
                AMBIENTE_LOCAL,
                command.perfil().etapas(),
                command.perfil().credenciais(),
                avisos(command.motivo()),
                Instant.now(clock)
        );
    }

    private UUID empresaAtualId() {
        return TenantContextHolder.contextoAtual()
                .map(contexto -> contexto.empresaId())
                .orElse(null);
    }

    private UUID usuarioAtualId() {
        return TenantContextHolder.contextoAtual()
                .map(contexto -> contexto.usuarioId())
                .orElse(null);
    }

    private List<String> avisos(String motivo) {
        String motivoTratado = motivo == null || motivo.isBlank()
                ? "Reset solicitado pelo Admin SaaS local."
                : motivo.trim();
        return List.of(
                "Operacao disponivel somente no profile local.",
                "Reset idempotente: recria/upserta massa demo conhecida sem apagar dados reais externos.",
                "Gateway, cobranca e webhooks reais nao sao acionados.",
                motivoTratado
        );
    }
}
