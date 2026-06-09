package br.com.atendepro.modules.nutri.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.ItemBancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.OrigemItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemBancoAlimentosNutriPro;

public interface BancoAlimentosNutriProPort {

    List<ItemBancoAlimentosNutriProResult> listarItens(
            UUID empresaId,
            String busca,
            TipoItemBancoAlimentosNutriPro tipoItem,
            OrigemItemBancoAlimentosNutriPro origem,
            Boolean ativo
    );

    Optional<ItemBancoAlimentosNutriProResult> carregarItem(UUID empresaId, UUID itemId);

    ItemBancoAlimentosNutriProResult salvarItem(NovoItemBancoAlimentosNutriPro novoItem);

    record NovoItemBancoAlimentosNutriPro(
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
            Instant agora
    ) {
    }
}
