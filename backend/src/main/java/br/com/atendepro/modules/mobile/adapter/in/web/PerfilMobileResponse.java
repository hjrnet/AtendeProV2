package br.com.atendepro.modules.mobile.adapter.in.web;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.mobile.application.result.ClienteVinculadoMobileResult;
import br.com.atendepro.modules.mobile.application.result.EmpresaMobileResult;
import br.com.atendepro.modules.mobile.application.result.PerfilMobileResult;

public record PerfilMobileResponse(
        UUID usuarioId,
        UUID empresaId,
        String nomeUsuario,
        String emailUsuario,
        Set<PerfilAcesso> perfis,
        Set<String> authorities,
        EmpresaResponse empresa,
        List<ClienteVinculadoResponse> clientesVinculados,
        boolean exigeVinculoCliente,
        String papelPrincipal
) {

    public static PerfilMobileResponse de(PerfilMobileResult result) {
        return new PerfilMobileResponse(
                result.usuarioId(),
                result.empresaId(),
                result.nomeUsuario(),
                result.emailUsuario(),
                result.perfis(),
                result.authorities(),
                EmpresaResponse.de(result.empresa()),
                result.clientesVinculados().stream().map(ClienteVinculadoResponse::de).toList(),
                result.exigeVinculoCliente(),
                result.papelPrincipal()
        );
    }

    public record EmpresaResponse(
            UUID id,
            String nomeFantasia,
            String razaoSocial,
            String email,
            String telefone,
            boolean ativa
    ) {

        static EmpresaResponse de(EmpresaMobileResult empresa) {
            return new EmpresaResponse(
                    empresa.id(),
                    empresa.nomeFantasia(),
                    empresa.razaoSocial(),
                    empresa.email(),
                    empresa.telefone(),
                    empresa.ativa()
            );
        }
    }

    public record ClienteVinculadoResponse(
            UUID id,
            UUID empresaId,
            String nome,
            String tipo,
            String area,
            String documento,
            String email,
            String telefone,
            LocalDate dataNascimento,
            String observacoes,
            boolean ativo,
            Instant criadoEm,
            Instant atualizadoEm
    ) {

        static ClienteVinculadoResponse de(ClienteVinculadoMobileResult cliente) {
            return new ClienteVinculadoResponse(
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
}
