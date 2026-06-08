package br.com.atendepro.modules.beauty.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface BaixarProdutoEstoqueBeautyProPort {

    void baixarProdutoEstoqueBeauty(UUID empresaId, UUID produtoEstoqueId, BigDecimal quantidade, Instant atualizadoEm);
}
