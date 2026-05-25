package br.com.atendepro.modules.documento.application.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.documento.application.port.in.ValidarDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.out.CarregarDocumentoProfissionalPorCodigoValidacaoPort;
import br.com.atendepro.modules.documento.application.result.ValidacaoDocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;

@Service
@Profile("!test")
public class ValidacaoDocumentoProfissionalService implements ValidarDocumentoProfissionalUseCase {

    private final CarregarDocumentoProfissionalPorCodigoValidacaoPort carregarDocumentoProfissionalPorCodigoValidacaoPort;

    public ValidacaoDocumentoProfissionalService(
            CarregarDocumentoProfissionalPorCodigoValidacaoPort carregarDocumentoProfissionalPorCodigoValidacaoPort
    ) {
        this.carregarDocumentoProfissionalPorCodigoValidacaoPort = carregarDocumentoProfissionalPorCodigoValidacaoPort;
    }

    @Override
    public ValidacaoDocumentoProfissionalResult validarDocumento(String codigoValidacao) {
        if (codigoValidacao == null || codigoValidacao.isBlank()) {
            return ValidacaoDocumentoProfissionalResult.invalida(codigoValidacao);
        }
        return carregarDocumentoProfissionalPorCodigoValidacaoPort.carregarDocumentoPorCodigoValidacao(codigoValidacao.trim())
                .filter(this::podeValidarPublicamente)
                .map(ValidacaoDocumentoProfissionalResult::valida)
                .orElseGet(() -> ValidacaoDocumentoProfissionalResult.invalida(codigoValidacao.trim()));
    }

    private boolean podeValidarPublicamente(DocumentoProfissional documento) {
        return documento.ativo()
                && documento.validacaoPublicaAtiva()
                && documento.status() != StatusDocumentoProfissional.CANCELADO;
    }
}
