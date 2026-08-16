package br.com.ecociente.calendario.entrypoint.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.StatusType;

@Component
public class AgendamentoFiltroMapper {

  public AgendamentoFiltro toDomain(
    StatusType statusType,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    Integer condominioId,
    Integer cooperativaId,
    Boolean possuiRecorrencia
  ){
    return new AgendamentoFiltro(
      statusType,
      dataInicio,
      dataFim,
      condominioId,
      cooperativaId,
      possuiRecorrencia
    );
  }
  
}
