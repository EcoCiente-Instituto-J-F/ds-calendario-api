package br.com.ecociente.calendario.dataprovider.gateway;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.gateway.AgendamentoColetaGateway;
import br.com.ecociente.calendario.core.mapper.AgendamentoColetaMapper;
import br.com.ecociente.calendario.dataprovider.entity.AgendamentoColetaEntity;
import br.com.ecociente.calendario.dataprovider.entity.StatusAgendamentoEntity;
import br.com.ecociente.calendario.dataprovider.repository.AgendamentoColetaRepository;
import br.com.ecociente.calendario.dataprovider.repository.StatusAgendamentoRepository;

@Component
public class AgendamentoColetaGatewayImpl implements AgendamentoColetaGateway {

  private final AgendamentoColetaRepository agendamentoColetaRepository;
  private final AgendamentoColetaMapper agendamentoColetaMapper;
  private final StatusAgendamentoRepository statusAgendamentoRepository;

  public AgendamentoColetaGatewayImpl(AgendamentoColetaRepository agendamentoColetaRepository, AgendamentoColetaMapper agendamentoColetaMapper, StatusAgendamentoRepository statusAgendamentoRepository) {
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
  public Page<AgendamentoColeta> buscarPorSindico(Integer usuarioId, Pageable pageable) {
    return agendamentoColetaRepository
            .buscarPorSindico(usuarioId, pageable)
            .map(agendamentoColetaMapper::toDomain);
}

  @Override
  public Page<AgendamentoColeta> buscarPorCooperativa(Integer usuarioId, Pageable pageable) {
    return agendamentoColetaRepository
            .buscarPorCooperativa(usuarioId, pageable)
            .map(agendamentoColetaMapper::toDomain);
}

  @Override
  public Optional<AgendamentoColeta> buscarProximoAgendamentoPorSindico(Integer usuarioId) {
    return agendamentoColetaRepository.buscarProximoAgendamentoPorSindico(usuarioId)
            .map(agendamentoColetaMapper::toDomain);
  }

  @Override
  public Optional<AgendamentoColeta> buscarProximoAgendamentoPorCooperativa(Integer usuarioId) {
    return agendamentoColetaRepository.buscarProximoAgendamentoPorCooperativa(usuarioId)
            .map(agendamentoColetaMapper::toDomain);
  }

  @Override
  public Optional<AgendamentoColeta> buscarPorId(Integer id) {
    return agendamentoColetaRepository.findById(id)
    .map(agendamentoColetaMapper::toDomain);
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
