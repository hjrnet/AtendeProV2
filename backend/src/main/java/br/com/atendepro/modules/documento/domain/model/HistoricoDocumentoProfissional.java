package br.com.atendepro.modules.documento.domain.model;

import java.time.Instant;
import java.util.UUID;

public record HistoricoDocumentoProfissional(
        UUID id,
        UUID documentoId,
        UUID empresaId,
        int versaoAnterior,
        int versaoNova,
        AcaoHistoricoDocumentoProfissional acao,
        String tituloAnterior,
        String conteudoAnterior,
        StatusDocumentoProfissional statusAnterior,
        String tituloNovo,
        String conteudoNovo,
        StatusDocumentoProfissional statusNovo,
        String motivo,
        UUID usuarioId,
        Instant criadoEm
) {

    public HistoricoDocumentoProfissional {
        if (id == null) {
            throw new IllegalArgumentException("id do historico do documento profissional e obrigatorio");
        }
        if (documentoId == null) {
            throw new IllegalArgumentException("documento do historico profissional e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do historico profissional e obrigatoria");
        }
        if (versaoAnterior < 1 || versaoNova < 1) {
            throw new IllegalArgumentException("versoes do historico profissional devem ser positivas");
        }
        if (acao == null) {
            throw new IllegalArgumentException("acao do historico do documento profissional e obrigatoria");
        }
        if (tituloAnterior == null || tituloAnterior.isBlank()) {
            throw new IllegalArgumentException("titulo anterior do historico profissional e obrigatorio");
        }
        if (conteudoAnterior == null || conteudoAnterior.isBlank()) {
            throw new IllegalArgumentException("conteudo anterior do historico profissional e obrigatorio");
        }
        if (statusAnterior == null || statusNovo == null) {
            throw new IllegalArgumentException("status do historico profissional e obrigatorio");
        }
        if (tituloNovo == null || tituloNovo.isBlank()) {
            throw new IllegalArgumentException("titulo novo do historico profissional e obrigatorio");
        }
        if (conteudoNovo == null || conteudoNovo.isBlank()) {
            throw new IllegalArgumentException("conteudo novo do historico profissional e obrigatorio");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("motivo do historico profissional e obrigatorio");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("data do historico profissional e obrigatoria");
        }
        tituloAnterior = tituloAnterior.trim();
        conteudoAnterior = conteudoAnterior.trim();
        tituloNovo = tituloNovo.trim();
        conteudoNovo = conteudoNovo.trim();
        motivo = motivo.trim();
    }

    public static HistoricoDocumentoProfissional registrarSubstituicao(
            DocumentoProfissional anterior,
            DocumentoProfissional novo,
            String motivo,
            UUID usuarioId,
            Instant agora
    ) {
        return registrar(anterior, novo, AcaoHistoricoDocumentoProfissional.SUBSTITUICAO, motivo, usuarioId, agora);
    }

    public static HistoricoDocumentoProfissional registrarCancelamento(
            DocumentoProfissional anterior,
            DocumentoProfissional novo,
            String motivo,
            UUID usuarioId,
            Instant agora
    ) {
        return registrar(anterior, novo, AcaoHistoricoDocumentoProfissional.CANCELAMENTO, motivo, usuarioId, agora);
    }

    private static HistoricoDocumentoProfissional registrar(
            DocumentoProfissional anterior,
            DocumentoProfissional novo,
            AcaoHistoricoDocumentoProfissional acao,
            String motivo,
            UUID usuarioId,
            Instant agora
    ) {
        if (anterior == null || novo == null) {
            throw new IllegalArgumentException("documentos do historico profissional sao obrigatorios");
        }
        return new HistoricoDocumentoProfissional(
                UUID.randomUUID(),
                anterior.id(),
                anterior.empresaId(),
                anterior.versao(),
                novo.versao(),
                acao,
                anterior.titulo(),
                anterior.conteudo(),
                anterior.status(),
                novo.titulo(),
                novo.conteudo(),
                novo.status(),
                motivo,
                usuarioId,
                agora
        );
    }
}
