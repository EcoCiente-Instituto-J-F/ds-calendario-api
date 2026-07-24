package br.com.ecociente.calendario.entrypoint.mapper;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.entrypoint.dto.response.CalendarioResponseDto;

public class CalendarioResponseMapper {

  private CalendarioResponseMapper() {
  }

  public CalendarioResponseDto toResponseDto(AgendamentoColeta domain) {

    if (domain == null) {
      return null;
    }

    return new CalendarioResponseDto(
        domain.getId(),
        domain.getDataInicio(),
        domain.getDataFim(),
        domain.getStatusAgendamento(),
        domain.getPossuiRecorrencia());

  }
}
