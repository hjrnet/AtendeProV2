package br.com.atendepro.modules.documento.domain.model;

public enum ConselhoProfissional {
    CRN,
    CRBM,
    CREFITO,
    CRP,
    CREFONO,
    CRF,
    CRO,
    CRM,
    COREN,
    OUTRO;

    public static ConselhoProfissional deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("conselho profissional e obrigatorio");
        }
        return ConselhoProfissional.valueOf(codigo.trim().toUpperCase());
    }
}
