package br.com.ecociente.calendario.core.domain;

import java.time.OffsetDateTime;

public record Coleta(
    Integer id,
    Integer condominioId,
    Integer cooperativaId,
    StatusType status,
    OffsetDateTime dataInicio,
    OffsetDateTime dataFim,
    boolean possuiRecorrencia,
    Integer solicitadoPorUsuarioId,
    String origemSolicitacao,
    Integer respondidoPorUsuarioId,
    OffsetDateTime respondidoEm
) {
}