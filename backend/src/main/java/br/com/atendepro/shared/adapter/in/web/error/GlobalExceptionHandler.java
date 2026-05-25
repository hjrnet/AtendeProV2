package br.com.atendepro.shared.adapter.in.web.error;

import java.time.Instant;
import java.util.List;

import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.empresa.domain.exception.AcessoTenantNegadoException;
import br.com.atendepro.shared.domain.exception.BusinessException;
import br.com.atendepro.shared.domain.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErroApiResponse> tratarBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(criarErro(exception.codigo(), exception.getMessage(), request.getRequestURI(), List.of()));
    }

    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErroApiResponse> tratarAutenticacaoException(
            AutenticacaoException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(criarErro(exception.codigo(), exception.getMessage(), request.getRequestURI(), List.of()));
    }

    @ExceptionHandler(AcessoTenantNegadoException.class)
    public ResponseEntity<ErroApiResponse> tratarAcessoTenantNegadoException(
            AcessoTenantNegadoException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(criarErro(exception.codigo(), exception.getMessage(), request.getRequestURI(), List.of()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErroApiResponse> tratarValidationException(
            ValidationException exception,
            HttpServletRequest request
    ) {
        List<CampoErroResponse> campos = exception.campos().stream()
                .map(campo -> new CampoErroResponse(campo.campo(), campo.mensagem()))
                .toList();

        return ResponseEntity.badRequest()
                .body(criarErro("VALIDACAO", exception.getMessage(), request.getRequestURI(), campos));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApiResponse> tratarMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<CampoErroResponse> campos = exception.getBindingResult().getFieldErrors().stream()
                .map(this::mapearCampo)
                .toList();

        return ResponseEntity.badRequest()
                .body(criarErro("VALIDACAO", "Dados invalidos.", request.getRequestURI(), campos));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroApiResponse> tratarConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<CampoErroResponse> campos = exception.getConstraintViolations().stream()
                .map(violacao -> new CampoErroResponse(violacao.getPropertyPath().toString(), violacao.getMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(criarErro("VALIDACAO", "Dados invalidos.", request.getRequestURI(), campos));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroApiResponse> tratarException(Exception exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(criarErro("ERRO_INTERNO", "Erro interno inesperado.", request.getRequestURI(), List.of()));
    }

    private CampoErroResponse mapearCampo(FieldError fieldError) {
        return new CampoErroResponse(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private ErroApiResponse criarErro(String codigo, String mensagem, String path, List<CampoErroResponse> campos) {
        return new ErroApiResponse(codigo, mensagem, path, Instant.now(), campos);
    }
}
