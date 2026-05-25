package br.com.atendepro.modules.beauty.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record FichaEsteticaBeautyPro(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        ObjetivoEsteticoBeautyPro objetivo,
        String queixaPrincipal,
        String historicoEstetico,
        String alergias,
        String medicamentos,
        boolean gestante,
        boolean lactante,
        boolean sensibilidadePele,
        boolean usaAcidos,
        boolean exposicaoSolarIntensa,
        String procedimentosRecentes,
        String contraindicacoes,
        String observacoes,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public FichaEsteticaBeautyPro {
        Objects.requireNonNull(id, "id e obrigatorio");
        Objects.requireNonNull(empresaId, "empresaId e obrigatorio");
        Objects.requireNonNull(clienteId, "clienteId e obrigatorio");
        Objects.requireNonNull(objetivo, "objetivo e obrigatorio");
        queixaPrincipal = exigirTexto(queixaPrincipal, "queixaPrincipal");
        historicoEstetico = limpar(historicoEstetico);
        alergias = limpar(alergias);
        medicamentos = limpar(medicamentos);
        procedimentosRecentes = limpar(procedimentosRecentes);
        contraindicacoes = limpar(contraindicacoes);
        observacoes = limpar(observacoes);
        Objects.requireNonNull(criadoEm, "criadoEm e obrigatorio");
        Objects.requireNonNull(atualizadoEm, "atualizadoEm e obrigatorio");
    }

    public static FichaEsteticaBeautyPro criar(
            UUID empresaId,
            UUID clienteId,
            ObjetivoEsteticoBeautyPro objetivo,
            String queixaPrincipal,
            String historicoEstetico,
            String alergias,
            String medicamentos,
            boolean gestante,
            boolean lactante,
            boolean sensibilidadePele,
            boolean usaAcidos,
            boolean exposicaoSolarIntensa,
            String procedimentosRecentes,
            String contraindicacoes,
            String observacoes,
            Instant agora
    ) {
        return new FichaEsteticaBeautyPro(
                UUID.randomUUID(),
                empresaId,
                clienteId,
                objetivo,
                queixaPrincipal,
                historicoEstetico,
                alergias,
                medicamentos,
                gestante,
                lactante,
                sensibilidadePele,
                usaAcidos,
                exposicaoSolarIntensa,
                procedimentosRecentes,
                contraindicacoes,
                observacoes,
                agora,
                agora
        );
    }

    public FichaEsteticaBeautyPro atualizar(
            ObjetivoEsteticoBeautyPro objetivo,
            String queixaPrincipal,
            String historicoEstetico,
            String alergias,
            String medicamentos,
            boolean gestante,
            boolean lactante,
            boolean sensibilidadePele,
            boolean usaAcidos,
            boolean exposicaoSolarIntensa,
            String procedimentosRecentes,
            String contraindicacoes,
            String observacoes,
            Instant agora
    ) {
        return new FichaEsteticaBeautyPro(
                id,
                empresaId,
                clienteId,
                objetivo,
                queixaPrincipal,
                historicoEstetico,
                alergias,
                medicamentos,
                gestante,
                lactante,
                sensibilidadePele,
                usaAcidos,
                exposicaoSolarIntensa,
                procedimentosRecentes,
                contraindicacoes,
                observacoes,
                criadoEm,
                agora
        );
    }

    public boolean possuiAlertaContraindicacao() {
        return !alertasContraindicacoes().isEmpty();
    }

    public String alertaContraindicacoes() {
        List<String> alertas = alertasContraindicacoes();
        if (alertas.isEmpty()) {
            return "Sem contraindicações ou alertas informados nesta ficha.";
        }
        return String.join(" ", alertas);
    }

    private List<String> alertasContraindicacoes() {
        List<String> alertas = new ArrayList<>();
        if (gestante) {
            alertas.add("Cliente gestante: validar segurança do procedimento antes de executar.");
        }
        if (lactante) {
            alertas.add("Cliente lactante: registrar orientação profissional antes do protocolo.");
        }
        if (sensibilidadePele) {
            alertas.add("Pele sensível: usar protocolo conservador e registrar teste de sensibilidade quando aplicável.");
        }
        if (usaAcidos) {
            alertas.add("Uso recente de ácidos: avaliar intervalo seguro antes de peelings ou procedimentos agressivos.");
        }
        if (exposicaoSolarIntensa) {
            alertas.add("Exposição solar intensa: reforçar fotoproteção e avaliar adiamento de procedimentos fotossensíveis.");
        }
        if (temTexto(alergias)) {
            alertas.add("Alergias informadas: " + alergias + ".");
        }
        if (temTexto(medicamentos)) {
            alertas.add("Medicamentos informados: " + medicamentos + ".");
        }
        if (temTexto(contraindicacoes)) {
            alertas.add("Contraindicações registradas: " + contraindicacoes + ".");
        }
        return alertas;
    }

    private static String exigirTexto(String texto, String campo) {
        String valor = limpar(texto);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor;
    }

    private static String limpar(String texto) {
        if (texto == null) {
            return null;
        }
        String valor = texto.trim();
        return valor.isEmpty() ? null : valor;
    }

    private static boolean temTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
