package br.com.atendepro.modules.spaces.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.spaces.application.command.SimularParceiroSpacesCommand;
import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.StatusSimulacaoParceiroSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;

class SimulacaoParceiroSpacesServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b79ef2fd-bd18-4788-a565-62fd50901982");

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveSimularLucroDoParceiroComPacoteDaEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PacoteSublocacaoSpaces pacote = pacote();
        var service = new SimulacaoParceiroSpacesService(
                id -> Optional.of(pacote),
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        var result = service.simularParceiro(command(null, pacote.id()));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.lucroEstimadoParceiro()).isEqualByComparingTo("8700.00");
        assertThat(result.status()).isEqualTo(StatusSimulacaoParceiroSpaces.SAUDAVEL);
    }

    @Test
    void naoDeveSimularSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        var service = new SimulacaoParceiroSpacesService(
                id -> Optional.of(pacote()),
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        assertThatThrownBy(() -> service.simularParceiro(command(null, UUID.randomUUID())))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private SimularParceiroSpacesCommand command(UUID empresaId, UUID pacoteId) {
        return new SimularParceiroSpacesCommand(
                empresaId,
                pacoteId,
                40,
                80,
                new BigDecimal("180.00"),
                new BigDecimal("2500.00")
        );
    }

    private PacoteSublocacaoSpaces pacote() {
        return PacoteSublocacaoSpaces.cadastrar(
                EMPRESA_ID,
                null,
                "Hora avulsa",
                TipoPacoteSublocacaoSpaces.HORA,
                "Uso avulso",
                new BigDecimal("1.00"),
                new BigDecimal("80.00"),
                BigDecimal.ZERO,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }
}
