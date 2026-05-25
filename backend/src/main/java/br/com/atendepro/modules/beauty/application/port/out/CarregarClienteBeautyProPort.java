package br.com.atendepro.modules.beauty.application.port.out;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ClienteBeautyProntuarioResult;

public interface CarregarClienteBeautyProPort {
    Optional<ClienteBeautyProntuarioResult> carregarClienteBeautyPro(UUID empresaId, UUID clienteId, LocalDate hoje);
}
