package br.com.atendepro.modules.documento.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.CarimboProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;

public record CarimboProfissionalResponse(
        UUID id,
        UUID empresaId,
        UUID profissionalId,
        String profissionalNome,
        ConselhoProfissional conselho,
        String uf,
        String numeroRegistro,
        String assinaturaTexto,
        String clinicaNome,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static CarimboProfissionalResponse de(CarimboProfissionalResult result) {
        return new CarimboProfissionalResponse(
                result.id(),
                result.empresaId(),
                result.profissionalId(),
                result.profissionalNome(),
                result.conselho(),
                result.uf(),
                result.numeroRegistro(),
                result.assinaturaTexto(),
                result.clinicaNome(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
