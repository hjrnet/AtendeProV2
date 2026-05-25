package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;

public record ClientesBeautyProResponse(List<ClienteBeautyResumoResponse> itens) {

    public static ClientesBeautyProResponse de(List<ClienteBeautyResumoResult> results) {
        return new ClientesBeautyProResponse(results.stream().map(ClienteBeautyResumoResponse::de).toList());
    }

    public record ClienteBeautyResumoResponse(
            UUID id,
            String nome,
            String telefone,
            String observacoes,
            boolean ativo,
            Instant atualizadoEm
    ) {
        public static ClienteBeautyResumoResponse de(ClienteBeautyResumoResult result) {
            return new ClienteBeautyResumoResponse(
                    result.id(),
                    result.nome(),
                    result.telefone(),
                    result.observacoes(),
                    result.ativo(),
                    result.atualizadoEm()
            );
        }
    }
}
