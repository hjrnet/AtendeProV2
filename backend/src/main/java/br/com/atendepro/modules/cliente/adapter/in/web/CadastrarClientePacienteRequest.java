package br.com.atendepro.modules.cliente.adapter.in.web;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.cliente.application.command.CadastrarClientePacienteCommand;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarClientePacienteRequest(
        UUID empresaId,
        @NotBlank @Size(max = 160) String nome,
        @NotNull TipoCliente tipo,
        AreaCliente area,
        @Size(max = 30) String documento,
        @Email @Size(max = 160) String email,
        @Size(max = 40) String telefone,
        LocalDate dataNascimento,
        @Size(max = 1000) String observacoes
) {

    public CadastrarClientePacienteCommand paraCommand() {
        return new CadastrarClientePacienteCommand(
                empresaId,
                nome,
                tipo,
                area == null ? AreaCliente.GERAL : area,
                documento,
                email,
                telefone,
                dataNascimento,
                observacoes
        );
    }
}
