package br.com.ecociente.calendario.entrypoint.exception;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.ecociente.calendario.core.exception.OperacaoColetaNaoPermitidaException;
import br.com.ecociente.calendario.entrypoint.controller.ColetaController;
import br.com.ecociente.calendario.entrypoint.dto.ErrorResponse;
import br.com.ecociente.calendario.entrypoint.dto.ValidationError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ColetaController.class)
public class ColetaExceptionHandler {

  @ExceptionHandler(OperacaoColetaNaoPermitidaException.class)
  public ResponseEntity<ErrorResponse> handleNaoPermitida(
      OperacaoColetaNaoPermitidaException exception) {

    var resposta = new ErrorResponse(
        HttpStatus.FORBIDDEN.value(),
        "OPERACAO_COLETA_NAO_PERMITIDA",
        List.of(
            new ValidationError(
                null,
                exception.getMessage())));

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(resposta);
  }
}