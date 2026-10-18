package br.com.ecociente.calendario.entrypoint.dto.response;

import java.time.OffsetDateTime;

import br.com.ecociente.calendario.core.domain.StatusType;


public record ColetaResponseDto(
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