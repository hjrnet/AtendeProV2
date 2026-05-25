package br.com.atendepro.modules.documento.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;

public record CarimboProfissionalResult(
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

    public static CarimboProfissionalResult de(CarimboProfissional carimbo) {
        return new CarimboProfissionalResult(
                carimbo.id(),
                carimbo.empresaId(),
                carimbo.profissionalId(),
                carimbo.profissionalNome(),
                carimbo.conselho(),
                carimbo.uf(),
                carimbo.numeroRegistro(),
                carimbo.assinaturaTexto(),
                carimbo.clinicaNome(),
                carimbo.ativo(),
                carimbo.criadoEm(),
                carimbo.atualizadoEm()
        );
    }
}
