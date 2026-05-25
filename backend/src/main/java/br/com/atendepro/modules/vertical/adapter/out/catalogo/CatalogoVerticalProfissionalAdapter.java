package br.com.atendepro.modules.vertical.adapter.out.catalogo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.atendepro.modules.vertical.application.port.out.CarregarCatalogoVerticaisProfissionaisPort;
import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;
import br.com.atendepro.modules.vertical.domain.model.StatusVerticalProfissional;
import br.com.atendepro.modules.vertical.domain.model.VerticalProfissional;

@Component
public class CatalogoVerticalProfissionalAdapter implements CarregarCatalogoVerticaisProfissionaisPort {

    private final Map<CodigoVerticalProfissional, VerticalProfissional> catalogo = Map.of(
            CodigoVerticalProfissional.NUTRI_PRO,
            new VerticalProfissional(
                    CodigoVerticalProfissional.NUTRI_PRO,
                    "Nutri Pro",
                    "R7",
                    StatusVerticalProfissional.OPERACIONAL_BASE,
                    "CRN",
                    "Base profissional para nutricionistas acompanharem pacientes com plano alimentar, diario, exames, suplementacao e documentos com CRN.",
                    List.of("Nutricionistas", "Clinicas de nutricao", "Consultorios", "Estudantes supervisionados"),
                    List.of(
                            "perfil nutricional do paciente",
                            "plano alimentar",
                            "refeicoes e horarios",
                            "diario alimentar",
                            "avaliacao antropometrica",
                            "gasto energetico",
                            "solicitacao de exames",
                            "prescricao de suplementacao",
                            "lista de compras",
                            "app do paciente futuro"
                    ),
                    List.of(
                            "Paciente nutricional",
                            "Plano alimentar",
                            "Refeicao do plano",
                            "Alimento",
                            "Suplemento ou formulacao",
                            "Avaliacao antropometrica",
                            "Solicitacao de exame",
                            "Prescricao nutricional"
                    ),
                    List.of(
                            "Plano alimentar imprimivel",
                            "Solicitacao de exames laboratoriais",
                            "Prescricao de suplementacao",
                            "Orientacoes nutricionais"
                    ),
                    List.of(
                            "clientes/pacientes",
                            "agenda",
                            "documentos profissionais",
                            "carimbo profissional",
                            "custos e precificacao",
                            "relatorios",
                            "app do paciente futuro"
                    ),
                    List.of(
                            "TASK-NUTRI-002 criar modulo Nutri Pro",
                            "TASK-NUTRI-004 criar plano alimentar por paciente",
                            "TASK-NUTRI-009 calcular energia e macronutrientes",
                            "TASK-NUTRI-010 criar PDF do plano alimentar com carimbo CRN"
                    )
            ),
            CodigoVerticalProfissional.BEAUTY_PRO,
            new VerticalProfissional(
                    CodigoVerticalProfissional.BEAUTY_PRO,
                    "Beauty Pro",
                    "R7",
                    StatusVerticalProfissional.OPERACIONAL_BASE,
                    null,
                    "Base profissional para estetica, beleza e saloes gerenciarem protocolos, fotos de evolucao, termos, pacotes e rotinas de atendimento.",
                    List.of("Clinicas de estetica", "Profissionais de beleza", "Saloes", "Espacos de beleza"),
                    List.of(
                            "protocolos de atendimento",
                            "registro de fotos de evolucao",
                            "termos de consentimento",
                            "pacotes de servicos",
                            "agenda por profissional",
                            "ficha de cliente",
                            "materiais e produtos usados",
                            "alertas de retorno",
                            "relatorios de recorrencia"
                    ),
                    List.of(
                            "Cliente beauty",
                            "Protocolo estetico",
                            "Sessao do protocolo",
                            "Foto de evolucao",
                            "Termo de consentimento",
                            "Pacote de servicos",
                            "Produto aplicado"
                    ),
                    List.of(
                            "Termo de consentimento",
                            "Ficha de procedimento",
                            "Orientacoes pos-atendimento",
                            "Contrato de pacote"
                    ),
                    List.of(
                            "clientes/pacientes",
                            "agenda",
                            "servicos/procedimentos",
                            "estoque",
                            "documentos profissionais",
                            "custos e precificacao",
                            "relatorios"
                    ),
                    List.of(
                            "criar protocolos por procedimento",
                            "criar fotos de evolucao com historico",
                            "criar pacotes e retornos",
                            "preparar experiencia de salao multiagenda"
                    )
            )
    );

    @Override
    public List<VerticalProfissional> listarVerticais() {
        return catalogo.values()
                .stream()
                .sorted(Comparator.comparing(VerticalProfissional::nome))
                .toList();
    }

    @Override
    public Optional<VerticalProfissional> carregarVertical(CodigoVerticalProfissional codigo) {
        return Optional.ofNullable(catalogo.get(codigo));
    }
}
