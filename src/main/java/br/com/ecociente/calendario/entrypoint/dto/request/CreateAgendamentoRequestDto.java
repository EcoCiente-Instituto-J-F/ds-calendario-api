package br.com.ecociente.calendario.entrypoint.dto.request;

import java.time.LocalDateTime;

import br.com.ecociente.calendario.core.domain.StatusType;

public record CreateAgendamentoRequestDto(
  Integer id,
  LocalDateTime dataInicio,
  LocalDateTime dataFim,
  StatusType status,
  Boolean possuiRecorrencia
) {
}