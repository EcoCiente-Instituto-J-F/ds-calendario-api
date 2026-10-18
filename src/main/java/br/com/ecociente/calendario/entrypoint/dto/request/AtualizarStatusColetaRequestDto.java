package br.com.ecociente.calendario.entrypoint.dto.request;

import br.com.ecociente.calendario.core.domain.StatusType;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusColetaRequestDto(

    @NotNull(message = "Status é obrigatório")
    StatusType status

) {
}