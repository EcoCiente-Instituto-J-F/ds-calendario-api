package br.com.ecociente.calendario.dataprovider.gateway;

import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.gateway.AgendamentoComandoGateway;
import br.com.ecociente.calendario.core.mapper.AgendamentoColetaMapper;
import br.com.ecociente.calendario.dataprovider.entity.AgendamentoColetaEntity;
import br.com.ecociente.calendario.dataprovider.entity.StatusAgendamentoEntity;
import br.com.ecociente.calendario.dataprovider.repository.AgendamentoColetaRepository;
import br.com.ecociente.calendario.dataprovider.repository.StatusAgendamentoRepository;

@Component
public class AgendamentoComandoGatewayImpl implements AgendamentoComandoGateway{

  private final AgendamentoColetaRepository agendamentoColetaRepository;
  private final AgendamentoColetaMapper agendamentoColetaMapper;
  private final StatusAgendamentoRepository statusAgendamentoRepository;

  public AgendamentoComandoGatewayImpl(AgendamentoColetaRepository agendamentoColetaRepository, AgendamentoColetaMapper agendamentoColetaMapper, StatusAgendamentoRepository statusAgendamentoRepository) {
    this.agendamentoColetaRepository = agendamentoColetaRepository;
    this.agendamentoColetaMapper = agendamentoColetaMapper;
    this.statusAgendamentoRepository = statusAgendamentoRepository;
  }

  @Override
  public AgendamentoColeta salvar(AgendamentoColeta agendamentoColeta) {
    StatusAgendamentoEntity status = statusAgendamentoRepository.findByNomeStatus(agendamentoColeta.getStatusAgendamento().name());
    AgendamentoColetaEntity entity = agendamentoColetaMapper.toEntity(agendamentoColeta);
    entity.setStatusAgendamento(status);
    AgendamentoColetaEntity savedEntity = agendamentoColetaRepository.save(entity);
    return agendamentoColetaMapper.toDomain(savedEntity);

  }

   @Override
  public AgendamentoColeta atualizar(Integer id, AgendamentoColeta agendamentoColeta) {
    AgendamentoColetaEntity entity = agendamentoColetaMapper.toEntity(agendamentoColeta);
    entity.setId(id);
    return agendamentoColetaMapper.toDomain(agendamentoColetaRepository.save(entity));
  }

  @Override
  public void deletar(Integer id) {
    agendamentoColetaRepository.deleteById(id);
  }
  
}
