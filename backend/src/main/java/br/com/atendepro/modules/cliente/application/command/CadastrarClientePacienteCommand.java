package br.com.atendepro.modules.cliente.application.command;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.TipoCliente;

public record CadastrarClientePacienteCommand(
        UUID empresaId,
        String nome,
        TipoCliente tipo,
        AreaCliente area,
        String documento,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String observacoes
) {
}
