package br.com.atendepro.modules.documento.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.ModeloDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

public record ModeloDocumentoProfissionalResult(
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

    public static ModeloDocumentoProfissionalResult de(ModeloDocumentoProfissional modelo) {
        return new ModeloDocumentoProfissionalResult(
                modelo.id(),
                modelo.empresaId(),
                modelo.nome(),
                modelo.descricao(),
                modelo.tipo(),
                modelo.tituloPadrao(),
                modelo.conteudoPadrao(),
                modelo.global(),
                modelo.ativo(),
                modelo.criadoEm(),
                modelo.atualizadoEm()
        );
    }
}
