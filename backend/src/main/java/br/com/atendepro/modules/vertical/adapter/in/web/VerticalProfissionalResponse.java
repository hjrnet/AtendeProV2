package br.com.atendepro.modules.vertical.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.vertical.application.result.VerticalProfissionalResult;
import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;
import br.com.atendepro.modules.vertical.domain.model.StatusVerticalProfissional;

public record VerticalProfissionalResponse(
        CodigoVerticalProfissional codigo,
        String nome,
        String release,
        StatusVerticalProfissional status,
        String conselhoProfissional,
        String resumo,
        List<String> publicosAtendidos,
        List<String> capacidades,
        List<String> entidades,
        List<String> documentos,
        List<String> integracoesNucleo,
        List<String> proximasEvolucoes
) {

    public static VerticalProfissionalResponse de(VerticalProfissionalResult result) {
        return new VerticalProfissionalResponse(
                result.codigo(),
                result.nome(),
                result.release(),
                result.status(),
                result.conselhoProfissional(),
                result.resumo(),
                result.publicosAtendidos(),
                result.capacidades(),
                result.entidades(),
                result.documentos(),
                result.integracoesNucleo(),
                result.proximasEvolucoes()
        );
    }
}
