package br.com.atendepro.modules.cliente.adapter.in.web;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.cliente.application.result.ClientePacienteResult;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.TipoCliente;

public record ClientePacienteResponse(
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

    public static ClientePacienteResponse de(ClientePacienteResult result) {
        return new ClientePacienteResponse(
                result.id(),
                result.empresaId(),
                result.nome(),
                result.tipo(),
                result.area(),
                result.documento(),
                result.email(),
                result.telefone(),
                result.dataNascimento(),
                result.observacoes(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
