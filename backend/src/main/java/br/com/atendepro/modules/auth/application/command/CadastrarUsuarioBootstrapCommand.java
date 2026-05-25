package br.com.atendepro.modules.auth.application.command;

import java.util.Set;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

public record CadastrarUsuarioBootstrapCommand(
        String nome,
        EmailUsuario email,
        String senhaEmTexto,
        Set<PerfilAcesso> perfis
) {

    public CadastrarUsuarioBootstrapCommand {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (email == null) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
        validarSenhaForte(senhaEmTexto);
        perfis = Set.copyOf(perfis);
        if (perfis.isEmpty()) {
            throw new IllegalArgumentException("ao menos um perfil e obrigatorio");
        }
    }

    private static void validarSenhaForte(String senhaEmTexto) {
        if (senhaEmTexto == null || senhaEmTexto.length() < 12) {
            throw new IllegalArgumentException("senha bootstrap deve ter ao menos 12 caracteres");
        }
        boolean temLetraMaiuscula = senhaEmTexto.chars().anyMatch(Character::isUpperCase);
        boolean temLetraMinuscula = senhaEmTexto.chars().anyMatch(Character::isLowerCase);
        boolean temNumero = senhaEmTexto.chars().anyMatch(Character::isDigit);
        boolean temSimbolo = senhaEmTexto.chars().anyMatch(caractere -> !Character.isLetterOrDigit(caractere));
        if (!temLetraMaiuscula || !temLetraMinuscula || !temNumero || !temSimbolo) {
            throw new IllegalArgumentException("senha bootstrap deve combinar letras, numero e simbolo");
        }
    }
}
