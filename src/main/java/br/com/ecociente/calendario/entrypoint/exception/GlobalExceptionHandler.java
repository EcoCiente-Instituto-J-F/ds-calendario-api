package br.com.ecociente.calendario.entrypoint.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.ecociente.calendario.entrypoint.dto.ValidationError;
import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
import br.com.ecociente.calendario.entrypoint.dto.ErrorRespons;

@ControllerAdvice
public class GlobalExceptionHandler {

  private ErrorRespons createErrorResponse(HttpStatus status, String codigoError, List<ValidationError> details) {
    return new ErrorRespons(status.value(), codigoError, details);
  }

  @ExceptionHandler(PerfilNaoAutorizadoException.class)
  public ResponseEntity<ErrorRespons> handlePerfilNaoAutorizadoException(PerfilNaoAutorizadoException ex, WebRequest request) {
    List<ValidationError> details = new ArrayList<>();
    details.add(new ValidationError("perfil", ex.getMessage()));
    var response = createErrorResponse(HttpStatus.FORBIDDEN, "PERFIL_NAO_AUTORIZADO", details);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }
}
