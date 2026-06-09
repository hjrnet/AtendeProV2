package br.com.atendepro.modules.nutri.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.OrigemItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemBancoAlimentosNutriPro;

public final class BancoAlimentosNutriProResults {

    private BancoAlimentosNutriProResults() {
    }

    public record BancoAlimentosNutriProResult(
            UUID empresaId,
            MetricasBancoAlimentosNutriProResult metricas,
            List<ItemBancoAlimentosNutriProResult> itens,
            Instant atualizadoEm
    ) {
    }

    public record MetricasBancoAlimentosNutriProResult(
            int totalItens,
            int alimentos,
            int suplementos,
            int padrao,
            int personalizados
    ) {
    }

    public record ItemBancoAlimentosNutriProResult(
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
        public static ItemBancoAlimentosNutriProResult de(
                UUID id,
                UUID empresaId,
                TipoItemBancoAlimentosNutriPro tipoItem,
                OrigemItemBancoAlimentosNutriPro origem,
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
            return new ItemBancoAlimentosNutriProResult(
                    id,
                    empresaId,
                    tipoItem,
                    tipoItem.rotulo(),
                    origem,
                    origem.rotulo(),
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
                    observacoes,
                    ativo,
                    criadoEm,
                    atualizadoEm
            );
        }
    }
}
