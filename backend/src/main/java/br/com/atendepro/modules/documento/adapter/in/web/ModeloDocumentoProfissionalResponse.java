package br.com.atendepro.modules.documento.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.ModeloDocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

public record ModeloDocumentoProfissionalResponse(
        UUID id,
        UUID empresaId,
        String nome,
        String descricao,
        TipoDocumentoProfissional tipo,
        String tituloPadrao,
        String conteudoPadrao,
        boolean global,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ModeloDocumentoProfissionalResponse de(ModeloDocumentoProfissionalResult result) {
        return new ModeloDocumentoProfissionalResponse(
                result.id(),
                result.empresaId(),
                result.nome(),
                result.descricao(),
                result.tipo(),
                result.tituloPadrao(),
                result.conteudoPadrao(),
                result.global(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
