package br.com.atendepro.modules.agenda.domain.model;

import java.time.Instant;
import java.util.UUID;

public record CompromissoAgenda(
        UUID id,
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        String profissionalNome,
        String sala,
        TipoCompromisso tipo,
        AgendaStatus status,
        Instant inicio,
        Instant fim,
        String observacoes,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public CompromissoAgenda {
        if (id == null) {
            throw new IllegalArgumentException("id do compromisso e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do compromisso e obrigatoria");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do compromisso e obrigatorio");
        }
        if (status == null) {
            throw new IllegalArgumentException("status do compromisso e obrigatorio");
        }
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("periodo do compromisso e obrigatorio");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("fim do compromisso deve ser posterior ao inicio");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do compromisso sao obrigatorias");
        }
        profissionalNome = textoOpcional(profissionalNome);
        sala = textoOpcional(sala);
        observacoes = textoOpcional(observacoes);
        if (profissionalId == null && profissionalNome == null && sala == null) {
            throw new IllegalArgumentException("profissional ou sala sao obrigatorios para agenda");
        }
    }

    public static CompromissoAgenda agendar(
            UUID empresaId,
            UUID clientePacienteId,
            UUID profissionalId,
            String profissionalNome,
            String sala,
            TipoCompromisso tipo,
            Instant inicio,
            Instant fim,
            String observacoes,
            Instant agora
    ) {
        return new CompromissoAgenda(
                UUID.randomUUID(),
                empresaId,
                clientePacienteId,
                profissionalId,
                profissionalNome,
                sala,
                tipo,
                AgendaStatus.AGENDADO,
                inicio,
                fim,
                observacoes,
                agora,
                agora
        );
    }

    private static String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
