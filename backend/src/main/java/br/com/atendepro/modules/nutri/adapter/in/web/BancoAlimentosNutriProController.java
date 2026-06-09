package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.CadastrarItemBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.ConsultarBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.BancoAlimentosNutriProUseCase;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.BancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.ItemBancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.MetricasBancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.OrigemItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemBancoAlimentosNutriPro;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/nutri-pro/banco-alimentos")
@Profile("!test")
public class BancoAlimentosNutriProController {

    private final BancoAlimentosNutriProUseCase bancoAlimentosNutriProUseCase;

    public BancoAlimentosNutriProController(BancoAlimentosNutriProUseCase bancoAlimentosNutriProUseCase) {
        this.bancoAlimentosNutriProUseCase = bancoAlimentosNutriProUseCase;
    }

    @GetMapping
    public ResponseEntity<BancoAlimentosNutriProResponse> consultarBanco(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoItemBancoAlimentosNutriPro tipoItem,
            @RequestParam(required = false) OrigemItemBancoAlimentosNutriPro origem,
            @RequestParam(required = false, defaultValue = "true") Boolean ativo
    ) {
        return ResponseEntity.ok(BancoAlimentosNutriProResponse.de(
                bancoAlimentosNutriProUseCase.consultarBancoAlimentos(
                        new ConsultarBancoAlimentosNutriProCommand(empresaId, busca, tipoItem, origem, ativo)
                )
        ));
    }

    @PostMapping
    public ResponseEntity<ItemBancoAlimentosNutriProResponse> cadastrarItem(
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody CadastrarItemBancoAlimentosNutriProRequest request
    ) {
        ItemBancoAlimentosNutriProResponse response = ItemBancoAlimentosNutriProResponse.de(
                bancoAlimentosNutriProUseCase.cadastrarItem(request.paraCommand(empresaId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/banco-alimentos/" + response.id())).body(response);
    }

    public record CadastrarItemBancoAlimentosNutriProRequest(
            @NotNull TipoItemBancoAlimentosNutriPro tipoItem,
            OrigemItemBancoAlimentosNutriPro origem,
            @NotBlank String nome,
            String grupo,
            String categoriaClinica,
            @NotBlank String unidadeMedida,
            @Positive BigDecimal quantidadeBase,
            @PositiveOrZero BigDecimal energiaKcalBase,
            @PositiveOrZero BigDecimal proteinasBase,
            @PositiveOrZero BigDecimal carboidratosBase,
            @PositiveOrZero BigDecimal lipidiosBase,
            @PositiveOrZero BigDecimal fibrasBase,
            @PositiveOrZero BigDecimal sodioMgBase,
            String fonteDados,
            String marcaFabricante,
            String orientacaoUso,
            String observacoes
    ) {
        CadastrarItemBancoAlimentosNutriProCommand paraCommand(UUID empresaId) {
            return new CadastrarItemBancoAlimentosNutriProCommand(
                    empresaId,
                    tipoItem,
                    origem,
                    nome,
                    grupo,
                    categoriaClinica,
                    unidadeMedida,
                    quantidadeBase,
                    energiaKcalBase,
                    proteinasBase,
                    carboidratosBase,
                    lipidiosBase,
                    fibrasBase,
                    sodioMgBase,
                    fonteDados,
                    marcaFabricante,
                    orientacaoUso,
                    observacoes
            );
        }
    }

    public record BancoAlimentosNutriProResponse(
            UUID empresaId,
            MetricasBancoAlimentosNutriProResponse metricas,
            List<ItemBancoAlimentosNutriProResponse> itens,
            Instant atualizadoEm
    ) {
        static BancoAlimentosNutriProResponse de(BancoAlimentosNutriProResult result) {
            return new BancoAlimentosNutriProResponse(
                    result.empresaId(),
                    MetricasBancoAlimentosNutriProResponse.de(result.metricas()),
                    result.itens().stream().map(ItemBancoAlimentosNutriProResponse::de).toList(),
                    result.atualizadoEm()
            );
        }
    }

    public record MetricasBancoAlimentosNutriProResponse(
            int totalItens,
            int alimentos,
            int suplementos,
            int padrao,
            int personalizados
    ) {
        static MetricasBancoAlimentosNutriProResponse de(MetricasBancoAlimentosNutriProResult metricas) {
            return new MetricasBancoAlimentosNutriProResponse(
                    metricas.totalItens(),
                    metricas.alimentos(),
                    metricas.suplementos(),
                    metricas.padrao(),
                    metricas.personalizados()
            );
        }
    }

    public record ItemBancoAlimentosNutriProResponse(
            UUID id,
            UUID empresaId,
            TipoItemBancoAlimentosNutriPro tipoItem,
            String tipoItemRotulo,
            OrigemItemBancoAlimentosNutriPro origem,
            String origemRotulo,
            String nome,
            String grupo,
            String categoriaClinica,
            String unidadeMedida,
            BigDecimal quantidadeBase,
            BigDecimal energiaKcalBase,
            BigDecimal proteinasBase,
            BigDecimal carboidratosBase,
            BigDecimal lipidiosBase,
            BigDecimal fibrasBase,
            BigDecimal sodioMgBase,
            String fonteDados,
            String marcaFabricante,
            String orientacaoUso,
            String observacoes,
            boolean ativo,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        static ItemBancoAlimentosNutriProResponse de(ItemBancoAlimentosNutriProResult item) {
            return new ItemBancoAlimentosNutriProResponse(
                    item.id(),
                    item.empresaId(),
                    item.tipoItem(),
                    item.tipoItemRotulo(),
                    item.origem(),
                    item.origemRotulo(),
                    item.nome(),
                    item.grupo(),
                    item.categoriaClinica(),
                    item.unidadeMedida(),
                    item.quantidadeBase(),
                    item.energiaKcalBase(),
                    item.proteinasBase(),
                    item.carboidratosBase(),
                    item.lipidiosBase(),
                    item.fibrasBase(),
                    item.sodioMgBase(),
                    item.fonteDados(),
                    item.marcaFabricante(),
                    item.orientacaoUso(),
                    item.observacoes(),
                    item.ativo(),
                    item.criadoEm(),
                    item.atualizadoEm()
            );
        }
    }
}
