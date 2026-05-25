package br.com.atendepro.modules.plano.domain.model;

import java.util.Arrays;

public enum ModuloPlano {
    ADMIN_SAAS("admin-saas"),
    TENANT_EMPRESA("tenant-empresa"),
    USUARIOS_PERMISSOES("usuarios-permissoes"),
    DASHBOARD("dashboard"),
    CLIENTES("clientes"),
    AGENDA("agenda"),
    PROCEDIMENTOS("procedimentos"),
    CUSTOS("custos"),
    PRECIFICACAO("precificacao"),
    ESTOQUE("estoque"),
    EQUIPAMENTOS("equipamentos"),
    DOCUMENTOS("documentos"),
    SUBLOCACAO("sublocacao"),
    SUPORTE("suporte"),
    NUTRI_PRO("nutri-pro"),
    BEAUTY_PRO("beauty-pro"),
    BIOMED_PRO("biomed-pro"),
    FISIO_PRO("fisio-pro"),
    SPACES("spaces");

    private final String codigo;

    ModuloPlano(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }

    public static ModuloPlano deCodigo(String codigo) {
        String valorNormalizado = codigo.trim().toLowerCase().replace("_", "-");
        return Arrays.stream(values())
                .filter(modulo -> modulo.codigo.equals(valorNormalizado)
                        || modulo.name().equalsIgnoreCase(codigo.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("modulo de plano invalido: " + codigo));
    }
}
