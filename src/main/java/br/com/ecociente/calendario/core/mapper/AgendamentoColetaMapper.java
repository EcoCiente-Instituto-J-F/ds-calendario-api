package br.com.ecociente.calendario.core.mapper;

import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.StatusType;
import br.com.ecociente.calendario.dataprovider.entity.AgendamentoColetaEntity;

@Component
public class AgendamentoColetaMapper {

  private AgendamentoColetaMapper() {
  }

  public AgendamentoColeta toDomain(AgendamentoColetaEntity entity) {

    if (entity == null) {
      return null;
    }

    return AgendamentoColeta.builder()
        .id(entity.getId())
        .condominioId(entity.getCondominioId())
        .cooperativaId(entity.getCooperativaId())
        .statusAgendamento(StatusType.valueOf(entity.getStatusAgendamento().getNomeStatus().toUpperCase()))
        .dataInicio(entity.getDataInicio())
        .dataFim(entity.getDataFim())
        .possuiRecorrencia(entity.getPossuiRecorrencia())
        .build();
  }

  public AgendamentoColetaEntity toEntity(AgendamentoColeta domain) {

    if (domain == null) {
      return null;
    }

    return AgendamentoColetaEntity.builder()
        .id(domain.getId())
        .condominioId(domain.getCondominioId())
        .cooperativaId(domain.getCooperativaId())
        .dataInicio(domain.getDataInicio())
        .dataFim(domain.getDataFim())
        .possuiRecorrencia(domain.getPossuiRecorrencia())
        .build();
  }

}
