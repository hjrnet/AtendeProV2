package br.com.atendepro.modules.adminsaas.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasObservacaoResult;

public interface ObservarEmpresaAdminSaasUseCase {

    Optional<EmpresaAdminSaasObservacaoResult> observarEmpresa(UUID empresaId);
}
