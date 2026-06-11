package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarLembreteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarExameAvancadoCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarMaterialEducativoCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarMetaCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarSubstituicaoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.EnviarMensagemCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ReorganizarRefeicoesPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.RevisarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.EvolucaoPacienteResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ExameAvancadoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.GrupoListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.IndicadorGerencialNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ItemListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.LembreteAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MaterialEducativoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MensagemAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MetaAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.PerfilCarteiraNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RelatorioGerencialNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RegistroDiarioResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.SubstituicaoAlimentarResult;

public final class ExperienciaPacienteNutriProWeb {

    private ExperienciaPacienteNutriProWeb() {
    }

    public record ListaComprasNutriProResponse(
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String objetivoPlano,
            List<GrupoListaComprasNutriProResponse> grupos,
            Instant geradoEm
    ) {
        public static ListaComprasNutriProResponse de(ListaComprasResult result) {
            return new ListaComprasNutriProResponse(
                    result.empresaId(),
                    result.pacienteId(),
                    result.planoId(),
                    result.objetivoPlano(),
                    result.grupos().stream().map(GrupoListaComprasNutriProResponse::de).toList(),
                    result.geradoEm()
            );
        }
    }

    public record GrupoListaComprasNutriProResponse(String categoria, List<ItemListaComprasNutriProResponse> itens) {
        public static GrupoListaComprasNutriProResponse de(GrupoListaComprasResult result) {
            return new GrupoListaComprasNutriProResponse(
                    result.categoria(),
                    result.itens().stream().map(ItemListaComprasNutriProResponse::de).toList()
            );
        }
    }

    public record ItemListaComprasNutriProResponse(
            String nome,
            String categoria,
            BigDecimal quantidade,
            String unidadeMedida,
            String refeicoes,
            String observacoes
    ) {
        public static ItemListaComprasNutriProResponse de(ItemListaComprasResult result) {
            return new ItemListaComprasNutriProResponse(
                    result.nome(),
                    result.categoria(),
                    result.quantidade(),
                    result.unidadeMedida(),
                    result.refeicoes(),
                    result.observacoes()
            );
        }
    }

    public record CriarRegistroDiarioNutriProRequest(String refeicaoNome, String texto, String evidenciaUrl) {
        public CriarRegistroDiarioCommand paraCommand(UUID empresaId, UUID pacienteId) {
            return new CriarRegistroDiarioCommand(empresaId, pacienteId, refeicaoNome, texto, evidenciaUrl);
        }
    }

    public record RevisarRegistroDiarioNutriProRequest(String parecerProfissional) {
        public RevisarRegistroDiarioCommand paraCommand(UUID empresaId, UUID pacienteId, UUID registroId) {
            return new RevisarRegistroDiarioCommand(empresaId, pacienteId, registroId, parecerProfissional);
        }
    }

    public record ReorganizarRefeicoesPlanoAlimentarNutriProRequest(List<UUID> refeicaoIds) {
        public ReorganizarRefeicoesPlanoAlimentarCommand paraCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
            return new ReorganizarRefeicoesPlanoAlimentarCommand(empresaId, pacienteId, planoId, refeicaoIds);
        }
    }

    public record CriarSubstituicaoAlimentarNutriProRequest(
            UUID refeicaoId,
            String alimentoOrigem,
            String alimentoSubstituto,
            String grupo,
            String objetivo,
            String restricaoAlimentar,
            BigDecimal quantidadeEquivalente,
            String unidadeMedida,
            String observacoes
    ) {
        public CriarSubstituicaoAlimentarCommand paraCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
            return new CriarSubstituicaoAlimentarCommand(
                    empresaId,
                    pacienteId,
                    planoId,
                    refeicaoId,
                    alimentoOrigem,
                    alimentoSubstituto,
                    grupo,
                    objetivo,
                    restricaoAlimentar,
                    quantidadeEquivalente,
                    unidadeMedida,
                    observacoes
            );
        }
    }

    public record SubstituicaoAlimentarNutriProResponse(
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
            BigDecimal quantidadeEquivalente,
            String unidadeMedida,
            String observacoes,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        public static SubstituicaoAlimentarNutriProResponse de(SubstituicaoAlimentarResult result) {
            return new SubstituicaoAlimentarNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.pacienteId(),
                    result.planoId(),
                    result.refeicaoId(),
                    result.alimentoOrigem(),
                    result.alimentoSubstituto(),
                    result.grupo(),
                    result.objetivo(),
                    result.restricaoAlimentar(),
                    result.quantidadeEquivalente(),
                    result.unidadeMedida(),
                    result.observacoes(),
                    result.criadoEm(),
                    result.atualizadoEm()
            );
        }
    }

    public record SubstituicoesAlimentaresNutriProResponse(List<SubstituicaoAlimentarNutriProResponse> itens) {
        public static SubstituicoesAlimentaresNutriProResponse de(List<SubstituicaoAlimentarResult> results) {
            return new SubstituicoesAlimentaresNutriProResponse(results.stream().map(SubstituicaoAlimentarNutriProResponse::de).toList());
        }
    }

    public record CriarMaterialEducativoNutriProRequest(
            String tipo,
            String titulo,
            String objetivo,
            String conteudo,
            String linkAnexo,
            String observacoes
    ) {
        public CriarMaterialEducativoCommand paraCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
            return new CriarMaterialEducativoCommand(empresaId, pacienteId, planoId, tipo, titulo, objetivo, conteudo, linkAnexo, observacoes);
        }
    }

    public record MaterialEducativoNutriProResponse(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String tipo,
            String titulo,
            String objetivo,
            String conteudo,
            String linkAnexo,
            String observacoes,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        public static MaterialEducativoNutriProResponse de(MaterialEducativoResult result) {
            return new MaterialEducativoNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.pacienteId(),
                    result.planoId(),
                    result.tipo(),
                    result.titulo(),
                    result.objetivo(),
                    result.conteudo(),
                    result.linkAnexo(),
                    result.observacoes(),
                    result.criadoEm(),
                    result.atualizadoEm()
            );
        }
    }

    public record MateriaisEducativosNutriProResponse(List<MaterialEducativoNutriProResponse> itens) {
        public static MateriaisEducativosNutriProResponse de(List<MaterialEducativoResult> results) {
            return new MateriaisEducativosNutriProResponse(results.stream().map(MaterialEducativoNutriProResponse::de).toList());
        }
    }

    public record CriarExameAvancadoNutriProRequest(
            String tipo,
            String nome,
            BigDecimal valor,
            String unidadeMedida,
            LocalDate dataExame,
            String status,
            String observacoes
    ) {
        public CriarExameAvancadoCommand paraCommand(UUID empresaId, UUID pacienteId) {
            return new CriarExameAvancadoCommand(empresaId, pacienteId, tipo, nome, valor, unidadeMedida, dataExame, status, observacoes);
        }
    }

    public record ExameAvancadoNutriProResponse(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String nome,
            BigDecimal valor,
            String unidadeMedida,
            LocalDate dataExame,
            String status,
            String observacoes,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        public static ExameAvancadoNutriProResponse de(ExameAvancadoResult result) {
            return new ExameAvancadoNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.pacienteId(),
                    result.tipo(),
                    result.nome(),
                    result.valor(),
                    result.unidadeMedida(),
                    result.dataExame(),
                    result.status(),
                    result.observacoes(),
                    result.criadoEm(),
                    result.atualizadoEm()
            );
        }
    }

    public record ExamesAvancadosNutriProResponse(List<ExameAvancadoNutriProResponse> itens) {
        public static ExamesAvancadosNutriProResponse de(List<ExameAvancadoResult> results) {
            return new ExamesAvancadosNutriProResponse(results.stream().map(ExameAvancadoNutriProResponse::de).toList());
        }
    }

    public record IndicadorGerencialNutriProResponse(String nome, BigDecimal valor, String unidade) {
        public static IndicadorGerencialNutriProResponse de(IndicadorGerencialNutriProResult result) {
            return new IndicadorGerencialNutriProResponse(result.nome(), result.valor(), result.unidade());
        }
    }

    public record PerfilCarteiraNutriProResponse(String segmento, long total) {
        public static PerfilCarteiraNutriProResponse de(PerfilCarteiraNutriProResult result) {
            return new PerfilCarteiraNutriProResponse(result.segmento(), result.total());
        }
    }

    public record RelatorioGerencialNutriProResponse(
            UUID empresaId,
            Instant geradoEm,
            List<IndicadorGerencialNutriProResponse> indicadores,
            List<PerfilCarteiraNutriProResponse> perfilCarteira
    ) {
        public static RelatorioGerencialNutriProResponse de(RelatorioGerencialNutriProResult result) {
            return new RelatorioGerencialNutriProResponse(
                    result.empresaId(),
                    result.geradoEm(),
                    result.indicadores().stream().map(IndicadorGerencialNutriProResponse::de).toList(),
                    result.perfilCarteira().stream().map(PerfilCarteiraNutriProResponse::de).toList()
            );
        }
    }

    public record RegistroDiarioNutriProResponse(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String refeicaoNome,
            String texto,
            String evidenciaUrl,
            String statusRevisao,
            String parecerProfissional,
            String criadoPor,
            Instant registradoEm,
            Instant atualizadoEm
    ) {
        public static RegistroDiarioNutriProResponse de(RegistroDiarioResult result) {
            return new RegistroDiarioNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.pacienteId(),
                    result.planoId(),
                    result.refeicaoNome(),
                    result.texto(),
                    result.evidenciaUrl(),
                    result.statusRevisao(),
                    result.parecerProfissional(),
                    result.criadoPor(),
                    result.registradoEm(),
                    result.atualizadoEm()
            );
        }
    }

    public record RegistrosDiarioNutriProResponse(List<RegistroDiarioNutriProResponse> itens) {
        public static RegistrosDiarioNutriProResponse de(List<RegistroDiarioResult> results) {
            return new RegistrosDiarioNutriProResponse(results.stream().map(RegistroDiarioNutriProResponse::de).toList());
        }
    }

    public record CriarMetaNutriProRequest(
            String tipo,
            String descricao,
            BigDecimal valorMeta,
            String unidade,
            LocalDate dataAlvo
    ) {
        public CriarMetaCommand paraCommand(UUID empresaId, UUID pacienteId) {
            return new CriarMetaCommand(empresaId, pacienteId, tipo, descricao, valorMeta, unidade, dataAlvo);
        }
    }

    public record MetaNutriProResponse(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String descricao,
            BigDecimal valorMeta,
            String unidade,
            LocalDate dataInicio,
            LocalDate dataAlvo,
            String status,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        public static MetaNutriProResponse de(MetaAcompanhamentoResult result) {
            return new MetaNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.pacienteId(),
                    result.tipo(),
                    result.descricao(),
                    result.valorMeta(),
                    result.unidade(),
                    result.dataInicio(),
                    result.dataAlvo(),
                    result.status(),
                    result.criadoEm(),
                    result.atualizadoEm()
            );
        }
    }

    public record MetasNutriProResponse(List<MetaNutriProResponse> itens) {
        public static MetasNutriProResponse de(List<MetaAcompanhamentoResult> results) {
            return new MetasNutriProResponse(results.stream().map(MetaNutriProResponse::de).toList());
        }
    }

    public record CriarLembreteNutriProRequest(String titulo, String descricao, String horario, String frequencia) {
        public CriarLembreteCommand paraCommand(UUID empresaId, UUID pacienteId) {
            return new CriarLembreteCommand(empresaId, pacienteId, titulo, descricao, horario, frequencia);
        }
    }

    public record LembreteNutriProResponse(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String titulo,
            String descricao,
            String horario,
            String frequencia,
            String status,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        public static LembreteNutriProResponse de(LembreteAcompanhamentoResult result) {
            return new LembreteNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.pacienteId(),
                    result.titulo(),
                    result.descricao(),
                    result.horario(),
                    result.frequencia(),
                    result.status(),
                    result.criadoEm(),
                    result.atualizadoEm()
            );
        }
    }

    public record LembretesNutriProResponse(List<LembreteNutriProResponse> itens) {
        public static LembretesNutriProResponse de(List<LembreteAcompanhamentoResult> results) {
            return new LembretesNutriProResponse(results.stream().map(LembreteNutriProResponse::de).toList());
        }
    }

    public record EnviarMensagemNutriProRequest(String remetenteTipo, String remetenteNome, String texto, String contexto) {
        public EnviarMensagemCommand paraCommand(UUID empresaId, UUID pacienteId) {
            return new EnviarMensagemCommand(empresaId, pacienteId, remetenteTipo, remetenteNome, texto, contexto);
        }
    }

    public record MensagemNutriProResponse(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String remetenteTipo,
            String remetenteNome,
            String texto,
            String contexto,
            boolean lidaPeloPaciente,
            boolean lidaPeloProfissional,
            Instant enviadaEm
    ) {
        public static MensagemNutriProResponse de(MensagemAcompanhamentoResult result) {
            return new MensagemNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.pacienteId(),
                    result.remetenteTipo(),
                    result.remetenteNome(),
                    result.texto(),
                    result.contexto(),
                    result.lidaPeloPaciente(),
                    result.lidaPeloProfissional(),
                    result.enviadaEm()
            );
        }
    }

    public record MensagensNutriProResponse(List<MensagemNutriProResponse> itens) {
        public static MensagensNutriProResponse de(List<MensagemAcompanhamentoResult> results) {
            return new MensagensNutriProResponse(results.stream().map(MensagemNutriProResponse::de).toList());
        }
    }

    public record EvolucaoNutriProResponse(String tipo, String titulo, String descricao, String status, Instant data) {
        public static EvolucaoNutriProResponse de(EvolucaoPacienteResult result) {
            return new EvolucaoNutriProResponse(
                    result.tipo(),
                    result.titulo(),
                    result.descricao(),
                    result.status(),
                    result.data()
            );
        }
    }

    public record EvolucoesNutriProResponse(List<EvolucaoNutriProResponse> itens) {
        public static EvolucoesNutriProResponse de(List<EvolucaoPacienteResult> results) {
            return new EvolucoesNutriProResponse(results.stream().map(EvolucaoNutriProResponse::de).toList());
        }
    }
}
