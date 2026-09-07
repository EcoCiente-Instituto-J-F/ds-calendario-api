package br.com.ecociente.calendario.entrypoint.dto.request;

import java.time.LocalDateTime;

import br.com.ecociente.calendario.core.domain.StatusType;

public record ListarAgendamentoRequestDto(
  StatusType statusType, 
  LocalDateTime dataInicio,
  LocalDateTime dataFim, 
  Integer condominioId,
  Integer cooperativaId, 
  Boolean possuiRecorrencia) {
  
}
