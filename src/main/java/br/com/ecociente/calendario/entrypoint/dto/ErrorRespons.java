package br.com.ecociente.calendario.entrypoint.dto;

import java.util.List;

public record ErrorRespons(int status, String codigoError, List<ValidationError> details) {
  
}
