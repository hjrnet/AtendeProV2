package br.com.atendepro.modules.documento.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.cliente.application.port.out.CarregarClientePacientePorIdPort;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.modules.documento.application.command.CriarDocumentoPorModeloCommand;
import br.com.atendepro.modules.documento.application.port.in.CriarDocumentoProfissionalPorModeloUseCase;
import br.com.atendepro.modules.documento.application.port.in.DetalharModeloDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarModelosDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.out.CarregarModeloDocumentoProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.ListarModelosDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.port.out.SalvarDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;
import br.com.atendepro.modules.documento.application.result.ModeloDocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.ModeloDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class ModeloDocumentoProfissionalService implements
        ListarModelosDocumentoProfissionalUseCase,
        DetalharModeloDocumentoProfissionalUseCase,
        CriarDocumentoProfissionalPorModeloUseCase {

    private final ListarModelosDocumentoProfissionalPort listarModelosDocumentoProfissionalPort;
    private final CarregarModeloDocumentoProfissionalPorIdPort carregarModeloDocumentoProfissionalPorIdPort;
    private final SalvarDocumentoProfissionalPort salvarDocumentoProfissionalPort;
    private final CarregarClientePacientePorIdPort carregarClientePacientePorIdPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public ModeloDocumentoProfissionalService(
            ListarModelosDocumentoProfissionalPort listarModelosDocumentoProfissionalPort,
            CarregarModeloDocumentoProfissionalPorIdPort carregarModeloDocumentoProfissionalPorIdPort,
            SalvarDocumentoProfissionalPort salvarDocumentoProfissionalPort,
            CarregarClientePacientePorIdPort carregarClientePacientePorIdPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.listarModelosDocumentoProfissionalPort = listarModelosDocumentoProfissionalPort;
        this.carregarModeloDocumentoProfissionalPorIdPort = carregarModeloDocumentoProfissionalPorIdPort;
        this.salvarDocumentoProfissionalPort = salvarDocumentoProfissionalPort;
        this.carregarClientePacientePorIdPort = carregarClientePacientePorIdPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public ResultadoPaginado<ModeloDocumentoProfissionalResult> listarModelos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        ResultadoPaginado<ModeloDocumentoProfissional> modelos = listarModelosDocumentoProfissionalPort.listarModelos(
                empresaResolvida,
                paginacao,
                busca,
                tipo,
                ativo
        );
        return new ResultadoPaginado<>(
                modelos.itens().stream().map(ModeloDocumentoProfissionalResult::de).toList(),
                modelos.totalItens(),
                modelos.pagina(),
                modelos.tamanho()
        );
    }

    @Override
    public Optional<ModeloDocumentoProfissionalResult> detalharModelo(UUID modeloId, UUID empresaId) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        return carregarModeloDocumentoProfissionalPorIdPort.carregarModeloPorId(modeloId)
                .filter(modelo -> {
                    validarAcessoModelo(modelo, empresaResolvida);
                    return true;
                })
                .map(ModeloDocumentoProfissionalResult::de);
    }

    @Override
    public DocumentoProfissionalResult criarDocumentoPorModelo(CriarDocumentoPorModeloCommand command) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(command.empresaId());
        validarClientePaciente(command.clientePacienteId(), empresaResolvida);

        ModeloDocumentoProfissional modelo = carregarModeloDocumentoProfissionalPorIdPort
                .carregarModeloPorId(command.modeloId())
                .orElseThrow(() -> new BusinessException(
                        "MODELO_DOCUMENTO_NAO_ENCONTRADO",
                        "Modelo de documento profissional nao encontrado."
                ));
        validarAcessoModelo(modelo, empresaResolvida);
        if (!modelo.ativo()) {
            throw new BusinessException(
                    "MODELO_DOCUMENTO_INATIVO",
                    "Modelo de documento profissional esta inativo."
            );
        }

        DocumentoProfissional documento = DocumentoProfissional.criar(
                empresaResolvida,
                command.clientePacienteId(),
                command.profissionalId(),
                command.profissionalNome(),
                tituloDocumento(command.titulo(), modelo),
                modelo.tipo(),
                conteudoDocumento(modelo, command.conteudoComplementar()),
                command.status() == null ? StatusDocumentoProfissional.RASCUNHO : command.status(),
                Instant.now(clock)
        );
        salvarDocumentoProfissionalPort.salvarDocumento(documento);
        return DocumentoProfissionalResult.de(documento);
    }

    private void validarAcessoModelo(ModeloDocumentoProfissional modelo, UUID empresaId) {
        if (!modelo.pertenceAEmpresa(empresaId)) {
            throw new BusinessException(
                    "MODELO_DOCUMENTO_EMPRESA_DIVERGENTE",
                    "Modelo de documento profissional nao pertence a empresa informada."
            );
        }
        if (modelo.empresaId() != null) {
            tenantAccessService.validarAcessoEmpresa(modelo.empresaId());
        }
    }

    private void validarClientePaciente(UUID clientePacienteId, UUID empresaId) {
        if (clientePacienteId == null) {
            return;
        }
        ClientePaciente cliente = carregarClientePacientePorIdPort.carregarClientePacientePorId(clientePacienteId)
                .orElseThrow(() -> new BusinessException(
                        "DOCUMENTO_CLIENTE_NAO_ENCONTRADO",
                        "Cliente ou paciente do documento nao encontrado."
                ));
        tenantAccessService.validarAcessoEmpresa(cliente.empresaId());
        if (!cliente.empresaId().equals(empresaId)) {
            throw new BusinessException(
                    "DOCUMENTO_CLIENTE_EMPRESA_DIVERGENTE",
                    "Cliente ou paciente nao pertence a empresa do documento."
            );
        }
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
            throw new BusinessException(
                    "DOCUMENTO_EMPRESA_OBRIGATORIA",
                    "Empresa e obrigatoria para operar documentos."
            );
        }
        return empresaIdSolicitada;
    }

    private String tituloDocumento(String titulo, ModeloDocumentoProfissional modelo) {
        if (titulo == null || titulo.isBlank()) {
            return modelo.tituloPadrao();
        }
        return titulo.trim();
    }

    private String conteudoDocumento(ModeloDocumentoProfissional modelo, String conteudoComplementar) {
        if (conteudoComplementar == null || conteudoComplementar.isBlank()) {
            return modelo.conteudoPadrao();
        }
        return modelo.conteudoPadrao() + "\n\nComplemento:\n" + conteudoComplementar.trim();
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_DOCUMENTOS);
    }
}
