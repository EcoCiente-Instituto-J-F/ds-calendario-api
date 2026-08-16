package br.com.ecociente.calendario.entrypoint.mapper;

import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.entrypoint.dto.request.ListarAgendamentoRequestDto;

public class AgendamentoFiltroMapper {

  public AgendamentoFiltro toDomain(ListarAgendamentoRequestDto dto){
    if ( dto == null) {
      return new AgendamentoFiltro(null,null,null,null,null,null);
      
    }
    return new AgendamentoFiltro(
      dto.statusType(),
      dto.dataInicio(),
      dto.dataFim(),
      dto.condominioId(),
      dto.cooperativaId(),
      dto.possuiRecorrencia()
    );
  }
  
}
