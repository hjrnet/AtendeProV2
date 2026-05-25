package br.com.atendepro.modules.beauty.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.ProdutoUtilizadoBeautyPro;

public record ProdutoUtilizadoBeautyProResult(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        UUID sessaoId,
        UUID produtoEstoqueId,
        String nomeProduto,
        String lote,
        LocalDate validade,
        BigDecimal quantidade,
        String unidade,
        boolean alertaValidade,
        boolean alertaEstoqueBaixo,
        String statusRotulo,
        String observacoes,
        Instant criadoEm
) {
    public static ProdutoUtilizadoBeautyProResult de(ProdutoUtilizadoBeautyPro produto) {
        return new ProdutoUtilizadoBeautyProResult(
                produto.id(),
                produto.empresaId(),
                produto.clienteId(),
                produto.protocoloId(),
                produto.sessaoId(),
                produto.produtoEstoqueId(),
                produto.nomeProduto(),
                produto.lote(),
                produto.validade(),
                produto.quantidade(),
                produto.unidade(),
                produto.alertaValidade(),
                produto.alertaEstoqueBaixo(),
                statusRotulo(produto),
                produto.observacoes(),
                produto.criadoEm()
        );
    }

    private static String statusRotulo(ProdutoUtilizadoBeautyPro produto) {
        if (produto.alertaValidade() && produto.alertaEstoqueBaixo()) {
            return "Validade e estoque em alerta";
        }
        if (produto.alertaValidade()) {
            return "Validade em alerta";
        }
        if (produto.alertaEstoqueBaixo()) {
            return "Estoque baixo";
        }
        return "Rastreado";
    }
}
