package br.com.atendepro.modules.empresa.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.empresa.application.command.CadastrarEmpresaCommand;
import br.com.atendepro.modules.empresa.application.port.in.BuscarEmpresaUseCase;
import br.com.atendepro.modules.empresa.application.port.in.CadastrarEmpresaUseCase;
import br.com.atendepro.modules.empresa.application.port.in.ListarEmpresasUseCase;
import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorDocumentoPort;
import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorIdPort;
import br.com.atendepro.modules.empresa.application.port.out.ListarEmpresasPort;
import br.com.atendepro.modules.empresa.application.port.out.SalvarEmpresaPort;
import br.com.atendepro.modules.empresa.application.result.EmpresaResult;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class EmpresaService implements CadastrarEmpresaUseCase, BuscarEmpresaUseCase, ListarEmpresasUseCase {

    private final CarregarEmpresaPorDocumentoPort carregarEmpresaPorDocumentoPort;
    private final CarregarEmpresaPorIdPort carregarEmpresaPorIdPort;
    private final ListarEmpresasPort listarEmpresasPort;
    private final SalvarEmpresaPort salvarEmpresaPort;
    private final Clock clock;

    public EmpresaService(
            CarregarEmpresaPorDocumentoPort carregarEmpresaPorDocumentoPort,
            CarregarEmpresaPorIdPort carregarEmpresaPorIdPort,
            ListarEmpresasPort listarEmpresasPort,
            SalvarEmpresaPort salvarEmpresaPort,
            Clock clock
    ) {
        this.carregarEmpresaPorDocumentoPort = carregarEmpresaPorDocumentoPort;
        this.carregarEmpresaPorIdPort = carregarEmpresaPorIdPort;
        this.listarEmpresasPort = listarEmpresasPort;
        this.salvarEmpresaPort = salvarEmpresaPort;
        this.clock = clock;
    }

    @Override
    public EmpresaResult cadastrarEmpresa(CadastrarEmpresaCommand command) {
        carregarEmpresaPorDocumentoPort.carregarEmpresaPorDocumento(command.documento())
                .ifPresent(empresa -> {
                    throw new BusinessException("EMPRESA_DOCUMENTO_JA_CADASTRADO", "Documento de empresa ja cadastrado.");
                });

        EmpresaTenant empresa = new EmpresaTenant(
                UUID.randomUUID(),
                command.nomeFantasia(),
                command.razaoSocial(),
                command.documento(),
                command.email(),
                command.telefone(),
                true,
                Instant.now(clock)
        );
        salvarEmpresaPort.salvarEmpresa(empresa);
        return EmpresaResult.de(empresa);
    }

    @Override
    public Optional<EmpresaResult> buscarEmpresaPorId(UUID empresaId) {
        return carregarEmpresaPorIdPort.carregarEmpresaPorId(empresaId)
                .map(EmpresaResult::de);
    }

    @Override
    public ResultadoPaginado<EmpresaResult> listarEmpresas(Paginacao paginacao) {
        ResultadoPaginado<EmpresaTenant> empresas = listarEmpresasPort.listarEmpresas(paginacao);
        return new ResultadoPaginado<>(
                empresas.itens().stream().map(EmpresaResult::de).toList(),
                empresas.totalItens(),
                empresas.pagina(),
                empresas.tamanho()
        );
    }
}
