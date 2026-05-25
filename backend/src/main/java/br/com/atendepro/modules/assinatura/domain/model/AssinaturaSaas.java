package br.com.atendepro.modules.assinatura.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AssinaturaSaas(
        UUID id,
        UUID empresaId,
        UUID planoId,
        AssinaturaStatus status,
        Instant iniciadoEm,
        Instant canceladoEm,
        Instant bloqueadoEm,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public AssinaturaSaas {
        if (id == null) {
            throw new IllegalArgumentException("id da assinatura e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da assinatura e obrigatoria");
        }
        if (planoId == null) {
            throw new IllegalArgumentException("plano da assinatura e obrigatorio");
        }
        if (status == null) {
            throw new IllegalArgumentException("status da assinatura e obrigatorio");
        }
        if (iniciadoEm == null || criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas da assinatura sao obrigatorias");
        }
        if (status == AssinaturaStatus.CANCELADA && canceladoEm == null) {
            throw new IllegalArgumentException("assinatura cancelada exige data de cancelamento");
        }
        if (status == AssinaturaStatus.BLOQUEADA && bloqueadoEm == null) {
            throw new IllegalArgumentException("assinatura bloqueada exige data de bloqueio");
        }
    }

    public static AssinaturaSaas criar(UUID empresaId, UUID planoId, Instant agora) {
        return new AssinaturaSaas(
                UUID.randomUUID(),
                empresaId,
                planoId,
                AssinaturaStatus.ATIVA,
                agora,
                null,
                null,
                agora,
                agora
        );
    }

    public AssinaturaSaas alterarPlano(UUID novoPlanoId, Instant agora) {
        validarAtivaOuBloqueada("alterar plano");
        return new AssinaturaSaas(id, empresaId, novoPlanoId, status, iniciadoEm, canceladoEm, bloqueadoEm, criadoEm, agora);
    }

    public AssinaturaSaas cancelar(Instant agora) {
        validarAtivaOuBloqueada("cancelar");
        return new AssinaturaSaas(id, empresaId, planoId, AssinaturaStatus.CANCELADA, iniciadoEm, agora, null, criadoEm, agora);
    }

    public AssinaturaSaas bloquear(Instant agora) {
        if (status != AssinaturaStatus.ATIVA) {
            throw new IllegalArgumentException("somente assinatura ativa pode ser bloqueada");
        }
        return new AssinaturaSaas(id, empresaId, planoId, AssinaturaStatus.BLOQUEADA, iniciadoEm, null, agora, criadoEm, agora);
    }

    public AssinaturaSaas desbloquear(Instant agora) {
        if (status != AssinaturaStatus.BLOQUEADA) {
            throw new IllegalArgumentException("somente assinatura bloqueada pode ser desbloqueada");
        }
        return new AssinaturaSaas(id, empresaId, planoId, AssinaturaStatus.ATIVA, iniciadoEm, null, null, criadoEm, agora);
    }

    private void validarAtivaOuBloqueada(String acao) {
        if (status == AssinaturaStatus.CANCELADA) {
            throw new IllegalArgumentException("assinatura cancelada nao pode " + acao);
        }
    }
}
