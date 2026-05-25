package br.com.atendepro.modules.empresa.domain.model;

public record DocumentoEmpresa(String valor) {

    public DocumentoEmpresa {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("documento da empresa e obrigatorio");
        }
        valor = valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
        if (valor.length() < 8 || valor.length() > 20) {
            throw new IllegalArgumentException("documento da empresa deve ter entre 8 e 20 caracteres");
        }
    }

    public static DocumentoEmpresa de(String valor) {
        return new DocumentoEmpresa(valor);
    }
}
