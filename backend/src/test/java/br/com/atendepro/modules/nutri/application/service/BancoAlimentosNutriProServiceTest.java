package br.com.atendepro.modules.nutri.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.CadastrarItemBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.ConsultarBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.out.BancoAlimentosNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.BancoAlimentosNutriProPort.NovoItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.ItemBancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.OrigemItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemBancoAlimentosNutriPro;
import br.com.atendepro.shared.domain.exception.BusinessException;

class BancoAlimentosNutriProServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-09T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void deveConsultarBancoComMetricasDeItensPadraoEPersonalizados() {
        BancoAlimentosNutriProService service = service(new BancoAlimentosNutriProPortFake(List.of(
                item(TipoItemBancoAlimentosNutriPro.ALIMENTO, OrigemItemBancoAlimentosNutriPro.PADRAO),
                item(TipoItemBancoAlimentosNutriPro.SUPLEMENTO, OrigemItemBancoAlimentosNutriPro.PADRAO),
                item(TipoItemBancoAlimentosNutriPro.ALIMENTO, OrigemItemBancoAlimentosNutriPro.PERSONALIZADO)
        )));

        var result = service.consultarBancoAlimentos(new ConsultarBancoAlimentosNutriProCommand(
                EMPRESA_ID,
                "banana",
                null,
                null,
                true
        ));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.metricas().totalItens()).isEqualTo(3);
        assertThat(result.metricas().alimentos()).isEqualTo(2);
        assertThat(result.metricas().suplementos()).isEqualTo(1);
        assertThat(result.metricas().padrao()).isEqualTo(2);
        assertThat(result.metricas().personalizados()).isEqualTo(1);
    }

    @Test
    void deveCadastrarItemPersonalizadoComComposicaoNutricional() {
        AtomicReference<NovoItemBancoAlimentosNutriPro> itemSalvo = new AtomicReference<>();
        BancoAlimentosNutriProService service = service(new BancoAlimentosNutriProPortFake(List.of(), itemSalvo));

        var result = service.cadastrarItem(commandCadastro(OrigemItemBancoAlimentosNutriPro.PERSONALIZADO));

        assertThat(result.nome()).isEqualTo("Pao proteico artesanal");
        assertThat(result.origem()).isEqualTo(OrigemItemBancoAlimentosNutriPro.PERSONALIZADO);
        assertThat(result.tipoItem()).isEqualTo(TipoItemBancoAlimentosNutriPro.ALIMENTO);
        assertThat(itemSalvo.get().empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(itemSalvo.get().fibrasBase()).isEqualByComparingTo("4.00");
        assertThat(itemSalvo.get().fonteDados()).isEqualTo("Rotulo validado em consulta");
    }

    @Test
    void deveBloquearCadastroDeItemPadraoPeloProfissional() {
        BancoAlimentosNutriProService service = service(new BancoAlimentosNutriProPortFake(List.of()));

        assertThatThrownBy(() -> service.cadastrarItem(commandCadastro(OrigemItemBancoAlimentosNutriPro.PADRAO)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Itens padrao sao mantidos pelo catalogo do AtendePro.");
    }

    private BancoAlimentosNutriProService service(BancoAlimentosNutriProPort port) {
        return new BancoAlimentosNutriProService(
                port,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarItemBancoAlimentosNutriProCommand commandCadastro(OrigemItemBancoAlimentosNutriPro origem) {
        return new CadastrarItemBancoAlimentosNutriProCommand(
                EMPRESA_ID,
                TipoItemBancoAlimentosNutriPro.ALIMENTO,
                origem,
                "Pao proteico artesanal",
                "Panificados",
                "Lanche proteico",
                "g",
                new BigDecimal("100.00"),
                new BigDecimal("210.00"),
                new BigDecimal("15.00"),
                new BigDecimal("22.00"),
                new BigDecimal("6.00"),
                new BigDecimal("4.00"),
                new BigDecimal("320.00"),
                "Rotulo validado em consulta",
                "Manipulacao local",
                "Usar em lanches planejados.",
                "Item cadastrado pela nutricionista."
        );
    }

    private ItemBancoAlimentosNutriProResult item(
            TipoItemBancoAlimentosNutriPro tipo,
            OrigemItemBancoAlimentosNutriPro origem
    ) {
        return ItemBancoAlimentosNutriProResult.de(
                UUID.randomUUID(),
                origem == OrigemItemBancoAlimentosNutriPro.PADRAO ? null : EMPRESA_ID,
                tipo,
                origem,
                tipo == TipoItemBancoAlimentosNutriPro.ALIMENTO ? "Banana prata" : "Whey protein",
                tipo == TipoItemBancoAlimentosNutriPro.ALIMENTO ? "Frutas" : "Suplementos",
                "Catalogo",
                "g",
                new BigDecimal("100.00"),
                new BigDecimal("90.00"),
                new BigDecimal("1.00"),
                new BigDecimal("20.00"),
                new BigDecimal("1.00"),
                new BigDecimal("2.00"),
                BigDecimal.ZERO,
                "Teste",
                null,
                null,
                null,
                true,
                Instant.now(CLOCK),
                Instant.now(CLOCK)
        );
    }

    private final class BancoAlimentosNutriProPortFake implements BancoAlimentosNutriProPort {

        private final List<ItemBancoAlimentosNutriProResult> itens;
        private final AtomicReference<NovoItemBancoAlimentosNutriPro> itemSalvo;

        private BancoAlimentosNutriProPortFake(List<ItemBancoAlimentosNutriProResult> itens) {
            this(itens, new AtomicReference<>());
        }

        private BancoAlimentosNutriProPortFake(
                List<ItemBancoAlimentosNutriProResult> itens,
                AtomicReference<NovoItemBancoAlimentosNutriPro> itemSalvo
        ) {
            this.itens = itens;
            this.itemSalvo = itemSalvo;
        }

        @Override
        public List<ItemBancoAlimentosNutriProResult> listarItens(
                UUID empresaId,
                String busca,
                TipoItemBancoAlimentosNutriPro tipoItem,
                OrigemItemBancoAlimentosNutriPro origem,
                Boolean ativo
        ) {
            assertThat(empresaId).isEqualTo(EMPRESA_ID);
            return itens;
        }

        @Override
        public Optional<ItemBancoAlimentosNutriProResult> carregarItem(UUID empresaId, UUID itemId) {
            return Optional.empty();
        }

        @Override
        public ItemBancoAlimentosNutriProResult salvarItem(NovoItemBancoAlimentosNutriPro novoItem) {
            itemSalvo.set(novoItem);
            return ItemBancoAlimentosNutriProResult.de(
                    novoItem.id(),
                    novoItem.empresaId(),
                    novoItem.tipoItem(),
                    novoItem.origem(),
                    novoItem.nome(),
                    novoItem.grupo(),
                    novoItem.categoriaClinica(),
                    novoItem.unidadeMedida(),
                    novoItem.quantidadeBase(),
                    novoItem.energiaKcalBase(),
                    novoItem.proteinasBase(),
                    novoItem.carboidratosBase(),
                    novoItem.lipidiosBase(),
                    novoItem.fibrasBase(),
                    novoItem.sodioMgBase(),
                    novoItem.fonteDados(),
                    novoItem.marcaFabricante(),
                    novoItem.orientacaoUso(),
                    novoItem.observacoes(),
                    true,
                    novoItem.agora(),
                    novoItem.agora()
            );
        }
    }
}
