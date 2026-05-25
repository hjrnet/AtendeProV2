package br.com.atendepro.modules.beauty.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ProdutoUtilizadoBeautyProResult;

public record ProdutoUtilizadoBeautyProResponse(
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
    public static ProdutoUtilizadoBeautyProResponse de(ProdutoUtilizadoBeautyProResult result) {
        return new ProdutoUtilizadoBeautyProResponse(
                result.id(),
                result.empresaId(),
                result.clienteId(),
                result.protocoloId(),
                result.sessaoId(),
                result.produtoEstoqueId(),
                result.nomeProduto(),
                result.lote(),
                result.validade(),
                result.quantidade(),
                result.unidade(),
                result.alertaValidade(),
                result.alertaEstoqueBaixo(),
                result.statusRotulo(),
                result.observacoes(),
                result.criadoEm()
        );
    }
}
