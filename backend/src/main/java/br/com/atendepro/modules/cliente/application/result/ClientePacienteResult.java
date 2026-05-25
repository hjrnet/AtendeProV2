package br.com.atendepro.modules.cliente.application.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.modules.cliente.domain.model.TipoCliente;

public record ClientePacienteResult(
        UUID id,
        UUID empresaId,
        String nome,
        TipoCliente tipo,
        AreaCliente area,
        String documento,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String observacoes,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ClientePacienteResult de(ClientePaciente cliente) {
        return new ClientePacienteResult(
                cliente.id(),
                cliente.empresaId(),
                cliente.nome(),
                cliente.tipo(),
                cliente.area(),
                cliente.documento(),
                cliente.email(),
                cliente.telefone(),
                cliente.dataNascimento(),
                cliente.observacoes(),
                cliente.ativo(),
                cliente.criadoEm(),
                cliente.atualizadoEm()
        );
    }
}
