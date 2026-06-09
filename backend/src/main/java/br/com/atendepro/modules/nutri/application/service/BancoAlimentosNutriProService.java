package br.com.atendepro.modules.nutri.application.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.CadastrarItemBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.ConsultarBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.BancoAlimentosNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.out.BancoAlimentosNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.BancoAlimentosNutriProPort.NovoItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.BancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.ItemBancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.MetricasBancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.OrigemItemBancoAlimentosNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemBancoAlimentosNutriPro;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class BancoAlimentosNutriProService implements BancoAlimentosNutriProUseCase {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final BancoAlimentosNutriProPort bancoAlimentosNutriProPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public BancoAlimentosNutriProService(
            BancoAlimentosNutriProPort bancoAlimentosNutriProPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.bancoAlimentosNutriProPort = bancoAlimentosNutriProPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public BancoAlimentosNutriProResult consultarBancoAlimentos(ConsultarBancoAlimentosNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command == null ? null : command.empresaId());
        List<ItemBancoAlimentosNutriProResult> itens = bancoAlimentosNutriProPort.listarItens(
                empresaId,
                command == null ? null : command.busca(),
                command == null ? null : command.tipoItem(),
                command == null ? null : command.origem(),
                command == null ? Boolean.TRUE : command.ativo()
        );
        return new BancoAlimentosNutriProResult(
                empresaId,
                calcularMetricas(itens),
                itens,
                Instant.now(clock)
        );
    }

    @Override
    public ItemBancoAlimentosNutriProResult cadastrarItem(CadastrarItemBancoAlimentosNutriProCommand command) {
        validarPermissao();
        validarCadastro(command);
        UUID empresaId = resolverEmpresaId(command.empresaId());
        OrigemItemBancoAlimentosNutriPro origem = command.origem() == null
                ? OrigemItemBancoAlimentosNutriPro.PERSONALIZADO
                : command.origem();
        if (origem == OrigemItemBancoAlimentosNutriPro.PADRAO) {
            throw new BusinessException("NUTRI_BANCO_PADRAO_RESTRITO", "Itens padrao sao mantidos pelo catalogo do AtendePro.");
        }

        return bancoAlimentosNutriProPort.salvarItem(new NovoItemBancoAlimentosNutriPro(
                UUID.randomUUID(),
                empresaId,
                command.tipoItem(),
                OrigemItemBancoAlimentosNutriPro.PERSONALIZADO,
                textoObrigatorio(command.nome()),
                textoOpcional(command.grupo()),
                textoOpcional(command.categoriaClinica()),
                textoObrigatorio(command.unidadeMedida()),
                positivo(command.quantidadeBase(), "Quantidade base"),
                naoNegativo(command.energiaKcalBase(), "Energia"),
                naoNegativo(command.proteinasBase(), "Proteinas"),
                naoNegativo(command.carboidratosBase(), "Carboidratos"),
                naoNegativo(command.lipidiosBase(), "Lipidios"),
                naoNegativoOuZero(command.fibrasBase()),
                naoNegativoOuZero(command.sodioMgBase()),
                textoOpcional(command.fonteDados()),
                textoOpcional(command.marcaFabricante()),
                textoOpcional(command.orientacaoUso()),
                textoOpcional(command.observacoes()),
                Instant.now(clock)
        ));
    }

    private MetricasBancoAlimentosNutriProResult calcularMetricas(List<ItemBancoAlimentosNutriProResult> itens) {
        int alimentos = (int) itens.stream().filter(item -> item.tipoItem() == TipoItemBancoAlimentosNutriPro.ALIMENTO).count();
        int suplementos = (int) itens.stream().filter(item -> item.tipoItem() == TipoItemBancoAlimentosNutriPro.SUPLEMENTO).count();
        int padrao = (int) itens.stream().filter(item -> item.origem() == OrigemItemBancoAlimentosNutriPro.PADRAO).count();
        int personalizados = (int) itens.stream().filter(item -> item.origem() == OrigemItemBancoAlimentosNutriPro.PERSONALIZADO).count();
        return new MetricasBancoAlimentosNutriProResult(itens.size(), alimentos, suplementos, padrao, personalizados);
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException("NUTRI_BANCO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar o banco de alimentos.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_CLIENTES);
    }

    private void validarCadastro(CadastrarItemBancoAlimentosNutriProCommand command) {
        if (command == null || command.tipoItem() == null) {
            throw new BusinessException("NUTRI_BANCO_TIPO_OBRIGATORIO", "Tipo de item e obrigatorio no banco Nutri.");
        }
        textoObrigatorio(command.nome());
        textoObrigatorio(command.unidadeMedida());
        positivo(command.quantidadeBase(), "Quantidade base");
        naoNegativo(command.energiaKcalBase(), "Energia");
        naoNegativo(command.proteinasBase(), "Proteinas");
        naoNegativo(command.carboidratosBase(), "Carboidratos");
        naoNegativo(command.lipidiosBase(), "Lipidios");
    }

    private String textoObrigatorio(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("NUTRI_BANCO_CAMPO_OBRIGATORIO", "Campos obrigatorios do banco Nutri nao foram informados.");
        }
        return valor.trim();
    }

    private String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private BigDecimal positivo(BigDecimal valor, String campo) {
        BigDecimal validado = naoNegativo(valor, campo);
        if (validado.compareTo(ZERO) <= 0) {
            throw new BusinessException("NUTRI_BANCO_VALOR_INVALIDO", campo + " deve ser maior que zero.");
        }
        return validado;
    }

    private BigDecimal naoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(ZERO) < 0) {
            throw new BusinessException("NUTRI_BANCO_VALOR_INVALIDO", campo + " deve ser maior ou igual a zero.");
        }
        return valor;
    }

    private BigDecimal naoNegativoOuZero(BigDecimal valor) {
        if (valor == null) {
            return ZERO;
        }
        if (valor.compareTo(ZERO) < 0) {
            throw new BusinessException("NUTRI_BANCO_VALOR_INVALIDO", "Composicao nutricional deve ser maior ou igual a zero.");
        }
        return valor;
    }
}
