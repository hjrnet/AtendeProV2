package br.com.atendepro.modules.nutri.application.port.out;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.EvolucaoPacienteResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ExameAvancadoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.LembreteAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MaterialEducativoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MensagemAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MetaAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RelatorioGerencialNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RegistroDiarioResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.SubstituicaoAlimentarResult;
import br.com.atendepro.modules.nutri.domain.model.PlanoAlimentarNutriPro;

public interface ExperienciaPacienteNutriProPort {

    Optional<PlanoAlimentarNutriPro> publicarPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId);

    Optional<PlanoAlimentarNutriPro> carregarPlanoPublicado(UUID empresaId, UUID pacienteId);

    Optional<ListaComprasResult> consultarListaCompras(UUID empresaId, UUID pacienteId, Clock clock);

    default List<SubstituicaoAlimentarResult> listarSubstituicoesAlimentares(UUID empresaId, UUID pacienteId, UUID planoId) {
        return List.of();
    }

    default SubstituicaoAlimentarResult criarSubstituicaoAlimentar(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            UUID refeicaoId,
            String alimentoOrigem,
            String alimentoSubstituto,
            String grupo,
            String objetivo,
            String restricaoAlimentar,
            java.math.BigDecimal quantidadeEquivalente,
            String unidadeMedida,
            String observacoes
    ) {
        throw new UnsupportedOperationException("Substituicoes alimentares nao implementadas neste adapter.");
    }

    default List<MaterialEducativoResult> listarMateriaisEducativos(UUID empresaId, UUID pacienteId, UUID planoId) {
        return List.of();
    }

    default MaterialEducativoResult criarMaterialEducativo(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String tipo,
            String titulo,
            String objetivo,
            String conteudo,
            String linkAnexo,
            String observacoes
    ) {
        throw new UnsupportedOperationException("Materiais educativos nao implementados neste adapter.");
    }

    default List<ExameAvancadoResult> listarExamesAvancados(UUID empresaId, UUID pacienteId) {
        return List.of();
    }

    default ExameAvancadoResult criarExameAvancado(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String nome,
            java.math.BigDecimal valor,
            String unidadeMedida,
            java.time.LocalDate dataExame,
            String status,
            String observacoes
    ) {
        throw new UnsupportedOperationException("Exames avancados nao implementados neste adapter.");
    }

    default RelatorioGerencialNutriProResult consultarRelatorioGerencial(UUID empresaId, Clock clock) {
        return new RelatorioGerencialNutriProResult(empresaId, java.time.Instant.now(clock), List.of(), List.of());
    }

    List<RegistroDiarioResult> listarDiarioAlimentar(UUID empresaId, UUID pacienteId);

    RegistroDiarioResult criarRegistroDiario(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String refeicaoNome,
            String texto,
            String evidenciaUrl,
            String criadoPor,
            Clock clock
    );

    Optional<RegistroDiarioResult> revisarRegistroDiario(UUID empresaId, UUID pacienteId, UUID registroId, String parecerProfissional);

    List<MetaAcompanhamentoResult> listarMetas(UUID empresaId, UUID pacienteId);

    MetaAcompanhamentoResult criarMeta(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String descricao,
            BigDecimal valorMeta,
            String unidade,
            LocalDate dataAlvo,
            Clock clock
    );

    List<LembreteAcompanhamentoResult> listarLembretes(UUID empresaId, UUID pacienteId);

    LembreteAcompanhamentoResult criarLembrete(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String titulo,
            String descricao,
            String horario,
            String frequencia,
            Clock clock
    );

    List<MensagemAcompanhamentoResult> listarMensagens(UUID empresaId, UUID pacienteId);

    MensagemAcompanhamentoResult enviarMensagem(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String remetenteTipo,
            String remetenteNome,
            String texto,
            String contexto,
            Clock clock
    );

    void marcarMensagensLidas(UUID empresaId, UUID pacienteId, String leitor);

    List<EvolucaoPacienteResult> listarEvolucao(UUID empresaId, UUID pacienteId);

    default Optional<PlanoAlimentarNutriPro> arquivarPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId) {
        return Optional.empty();
    }

    default void reorganizarRefeicoesPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId, List<UUID> refeicaoIds) {
    }

    default Optional<PlanoAlimentarNutriPro> substituirPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId) {
        return publicarPlanoAlimentar(empresaId, pacienteId, planoId);
    }
}
