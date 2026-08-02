package br.com.ecociente.calendario.core.domain;

import java.time.LocalDateTime;

public record AgendamentoFiltro(
  StatusType status,
  LocalDateTime dataInicio,
  LocalDateTime dataFim,
  Integer condominioId,
  Integer cooperativaId,
  Boolean possuiRecorrencia
) {
}