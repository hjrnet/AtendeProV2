package br.com.atendepro.modules.marketing.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import br.com.atendepro.shared.domain.exception.BusinessException;

public record LeadMarketing(
        UUID id,
        String nome,
        String email,
        String telefone,
        AreaInteresseLead areaInteresse,
        TamanhoOperacaoLead tamanhoOperacao,
        String origem,
        String mensagem,
        StatusLeadMarketing status,
        Instant criadoEm
) {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public LeadMarketing {
        if (id == null) {
            throw new BusinessException("LEAD_ID_OBRIGATORIO", "Identificador do lead e obrigatorio.");
        }
        nome = textoObrigatorio(nome, "LEAD_NOME_OBRIGATORIO", "Nome e obrigatorio.");
        email = normalizarEmail(email);
        telefone = textoOpcional(telefone);
        if (areaInteresse == null) {
            throw new BusinessException("LEAD_AREA_OBRIGATORIA", "Area de interesse e obrigatoria.");
        }
        if (tamanhoOperacao == null) {
            throw new BusinessException("LEAD_TAMANHO_OBRIGATORIO", "Tamanho da operacao e obrigatorio.");
        }
        origem = textoObrigatorio(origem, "LEAD_ORIGEM_OBRIGATORIA", "Origem do lead e obrigatoria.");
        mensagem = textoOpcional(mensagem);
        status = status == null ? StatusLeadMarketing.NOVO : status;
        criadoEm = criadoEm == null ? Instant.now() : criadoEm;
    }

    public static LeadMarketing registrar(
            String nome,
            String email,
            String telefone,
            AreaInteresseLead areaInteresse,
            TamanhoOperacaoLead tamanhoOperacao,
            String origem,
            String mensagem,
            Instant criadoEm
    ) {
        return new LeadMarketing(
                UUID.randomUUID(),
                nome,
                email,
                telefone,
                areaInteresse,
                tamanhoOperacao,
                origem,
                mensagem,
                StatusLeadMarketing.NOVO,
                criadoEm
        );
    }

    private static String textoObrigatorio(String valor, String codigo, String mensagem) {
        String normalizado = textoOpcional(valor);
        if (normalizado == null) {
            throw new BusinessException(codigo, mensagem);
        }
        return normalizado;
    }

    private static String textoOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }

    private static String normalizarEmail(String valor) {
        String emailNormalizado = textoObrigatorio(valor, "LEAD_EMAIL_OBRIGATORIO", "Email e obrigatorio.")
                .toLowerCase(Locale.ROOT);
        if (!FORMATO_EMAIL.matcher(emailNormalizado).matches()) {
            throw new BusinessException("LEAD_EMAIL_INVALIDO", "Email invalido.");
        }
        return emailNormalizado;
    }
}
