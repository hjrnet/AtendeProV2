package br.com.atendepro.modules.vertical.domain.model;

import java.util.List;

import br.com.atendepro.shared.domain.exception.ValidationException;

public record VerticalProfissional(
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

    public VerticalProfissional {
        if (codigo == null) {
            throw new ValidationException("Codigo da vertical profissional e obrigatorio.");
        }
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Nome da vertical profissional e obrigatorio.");
        }
        if (release == null || release.isBlank()) {
            throw new ValidationException("Release da vertical profissional e obrigatoria.");
        }
        if (status == null) {
            throw new ValidationException("Status da vertical profissional e obrigatorio.");
        }
        if (resumo == null || resumo.isBlank()) {
            throw new ValidationException("Resumo da vertical profissional e obrigatorio.");
        }
        publicosAtendidos = List.copyOf(publicosAtendidos == null ? List.of() : publicosAtendidos);
        capacidades = List.copyOf(capacidades == null ? List.of() : capacidades);
        entidades = List.copyOf(entidades == null ? List.of() : entidades);
        documentos = List.copyOf(documentos == null ? List.of() : documentos);
        integracoesNucleo = List.copyOf(integracoesNucleo == null ? List.of() : integracoesNucleo);
        proximasEvolucoes = List.copyOf(proximasEvolucoes == null ? List.of() : proximasEvolucoes);
    }
}
