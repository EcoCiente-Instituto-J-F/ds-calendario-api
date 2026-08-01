package br.com.ecociente.calendario.entrypoint.dto.response;

import java.time.LocalDateTime;

import br.com.ecociente.calendario.core.domain.StatusType;

public record CalendarioResponseDto(
  Integer id,
  LocalDateTime dataInicio,
  LocalDateTime dataFim,
  StatusType statusAgendamento,
  Boolean possuiRecorrencia
) {
}
