package br.com.atendepro.modules.vertical.application.result;

import java.util.List;

import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;
import br.com.atendepro.modules.vertical.domain.model.StatusVerticalProfissional;
import br.com.atendepro.modules.vertical.domain.model.VerticalProfissional;

public record VerticalProfissionalResult(
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

    public static VerticalProfissionalResult de(VerticalProfissional vertical) {
        return new VerticalProfissionalResult(
                vertical.codigo(),
                vertical.nome(),
                vertical.release(),
                vertical.status(),
                vertical.conselhoProfissional(),
                vertical.resumo(),
                vertical.publicosAtendidos(),
                vertical.capacidades(),
                vertical.entidades(),
                vertical.documentos(),
                vertical.integracoesNucleo(),
                vertical.proximasEvolucoes()
        );
    }
}
