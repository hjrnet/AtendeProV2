package br.com.atendepro.modules.growth.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.growth.domain.model.EtapaLeadGrowth;
import br.com.atendepro.modules.growth.domain.model.PerfilDemoGrowth;

public final class GrowthResults {

    private GrowthResults() {
    }

    public record LeadGrowthResult(
            UUID id,
            UUID empresaId,
            String nome,
            String email,
            String telefone,
            AreaCliente vertical,
            String origem,
            EtapaLeadGrowth etapa,
            BigDecimal potencialMensal,
            UUID clientePacienteId,
            UUID compromissoAgendaId,
            String observacoes,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
    }

    public record ClientePosVendaGrowthResult(
            UUID id,
            String nome,
            AreaCliente vertical,
            String email,
            String telefone,
            Instant ultimaConsultaEm,
            Instant proximaConsultaEm,
            Instant ultimoContatoEm,
            int faltasRecentes,
            Integer ultimaNotaNps,
            Instant atualizadoEm
    ) {
    }

    public record SugestaoPosVendaIAResult(
            UUID clienteId,
            String clienteNome,
            AreaCliente vertical,
            String tipo,
            String prioridade,
            String motivo,
            LocalDate retornoRecomendadoEm,
            String mensagemSugerida,
            String oportunidadePacote
    ) {
    }

    public record IndicadorVerticalGrowthResult(
            AreaCliente vertical,
            long clientesAtivos,
            long agendaProximos30Dias,
            BigDecimal faturamentoPrevisto,
            BigDecimal ticketMedio,
            BigDecimal margemMediaPercentual,
            BigDecimal recorrenciaPercentual,
            long clientesComRecompra,
            String leituraExecutiva
    ) {
    }

    public record ApresentacaoDemoGrowthResult(
            UUID id,
            PerfilDemoGrowth perfil,
            String titulo,
            String roteiro,
            String metricasChave,
            String chamadaParaAcao,
            Instant atualizadoEm
    ) {
    }

    public record PainelGrowthResult(
            UUID empresaId,
            List<LeadGrowthResult> leads,
            List<SugestaoPosVendaIAResult> sugestoesPosVenda,
            List<IndicadorVerticalGrowthResult> indicadores,
            List<ApresentacaoDemoGrowthResult> apresentacoesDemo,
            Instant atualizadoEm
    ) {
    }
}
