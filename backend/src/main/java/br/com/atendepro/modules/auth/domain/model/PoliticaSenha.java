package br.com.atendepro.modules.auth.domain.model;

public final class PoliticaSenha {

    private PoliticaSenha() {
    }

    public static void validarSenhaForte(String senhaEmTexto, String nomeCampo) {
        if (senhaEmTexto == null || senhaEmTexto.length() < 12) {
            throw new IllegalArgumentException(nomeCampo + " deve ter ao menos 12 caracteres");
        }
        boolean temLetraMaiuscula = senhaEmTexto.chars().anyMatch(Character::isUpperCase);
        boolean temLetraMinuscula = senhaEmTexto.chars().anyMatch(Character::isLowerCase);
        boolean temNumero = senhaEmTexto.chars().anyMatch(Character::isDigit);
        boolean temSimbolo = senhaEmTexto.chars().anyMatch(caractere -> !Character.isLetterOrDigit(caractere));
        if (!temLetraMaiuscula || !temLetraMinuscula || !temNumero || !temSimbolo) {
            throw new IllegalArgumentException(nomeCampo + " deve combinar letras, numero e simbolo");
        }
    }
}
