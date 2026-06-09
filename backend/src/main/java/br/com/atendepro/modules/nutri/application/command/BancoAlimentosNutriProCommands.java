package br.com.atendepro.modules.nutri.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.OrigemItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemBancoAlimentosNutriPro;

public final class BancoAlimentosNutriProCommands {

    private BancoAlimentosNutriProCommands() {
    }

    public record ConsultarBancoAlimentosNutriProCommand(
            UUID empresaId,
            String busca,
            TipoItemBancoAlimentosNutriPro tipoItem,
            OrigemItemBancoAlimentosNutriPro origem,
            Boolean ativo
    ) {
    }

    public record CadastrarItemBancoAlimentosNutriProCommand(
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
            String observacoes
    ) {
    }
}
