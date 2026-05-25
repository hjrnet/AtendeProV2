package br.com.atendepro.modules.assinatura.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;

public interface CarregarAssinaturaAtivaPorEmpresaPort {

    Optional<AssinaturaSaas> carregarAssinaturaAtivaPorEmpresa(UUID empresaId);
}
