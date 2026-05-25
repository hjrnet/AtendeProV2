package br.com.atendepro.modules.auth.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class JwtChaveAssinaturaFactory {

    private JwtChaveAssinaturaFactory() {
    }

    public static SecretKey criarChaveAssinatura(String segredoEmTexto) {
        try {
            byte[] segredo = MessageDigest.getInstance("SHA-256")
                    .digest(segredoEmTexto.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(segredo, "HmacSHA256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("algoritmo de assinatura jwt indisponivel", exception);
        }
    }
}
