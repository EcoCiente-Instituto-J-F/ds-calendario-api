package br.com.ecociente.calendario.entrypoint.mapper;

import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.entrypoint.dto.response.CalendarioResponseDto;

@Component
public class CalendarioResponseMapper {

  public CalendarioResponseDto toResponseDto(AgendamentoColeta domain) {

    if (domain == null) {
      return null;
    }

    return new CalendarioResponseDto(
        domain.getId(),
        domain.getCondominioId(),
        domain.getCooperativaId(),
        domain.getDataInicio(),
        domain.getDataFim(),
        domain.getStatusAgendamento(),
        domain.getPossuiRecorrencia());

  }
}
