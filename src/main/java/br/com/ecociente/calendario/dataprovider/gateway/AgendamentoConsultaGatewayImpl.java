package br.com.ecociente.calendario.dataprovider.gateway;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.gateway.AgendamentoConsultaGateway;
import br.com.ecociente.calendario.core.mapper.AgendamentoColetaMapper;

import br.com.ecociente.calendario.dataprovider.repository.AgendamentoColetaRepository;


@Component
public class AgendamentoConsultaGatewayImpl implements AgendamentoConsultaGateway {

  private final AgendamentoColetaRepository agendamentoColetaRepository;
  private final AgendamentoColetaMapper agendamentoColetaMapper;

  public AgendamentoConsultaGatewayImpl(AgendamentoColetaRepository agendamentoColetaRepository, AgendamentoColetaMapper agendamentoColetaMapper) {
    this.agendamentoColetaRepository = agendamentoColetaRepository;
    this.agendamentoColetaMapper = agendamentoColetaMapper;
  }

  
  @Override
  public Page<AgendamentoColeta> buscarPorSindico(Integer usuarioId, AgendamentoFiltro filtro, Pageable pageable) {
    return agendamentoColetaRepository
            .buscarPorSindico(
              usuarioId, 
              filtro.status() == null ? null : filtro.status().name(),
              filtro.dataInicio(),
              filtro.dataFim(),
              filtro.cooperativaId(),
              filtro.possuiRecorrencia(),
              pageable)
            .map(agendamentoColetaMapper::toDomain);
}

  @Override
  public Page<AgendamentoColeta> buscarPorCooperativa(Integer usuarioId, AgendamentoFiltro filtro, Pageable pageable) {
    return agendamentoColetaRepository
            .buscarPorCooperativa(
              usuarioId, 
              filtro.status() == null ? null : filtro.status().name(),
              filtro.dataInicio(),
              filtro.dataFim(),
              filtro.condominioId(),
              filtro.possuiRecorrencia(),
              pageable)
            .map(agendamentoColetaMapper::toDomain);
}

  @Override
  public Optional<AgendamentoColeta> buscarProximoAgendamentoPorSindico(Integer usuarioId, AgendamentoFiltro filtro) {
    return agendamentoColetaRepository.buscarProximoAgendamentoPorSindico(
      usuarioId,
      filtro.dataInicio(),
      filtro.dataFim(),
      filtro.possuiRecorrencia(),
      filtro.cooperativaId()
    )
    .map(agendamentoColetaMapper::toDomain);
  }

  @Override
  public Optional<AgendamentoColeta> buscarProximoAgendamentoPorCooperativa(Integer usuarioId, AgendamentoFiltro filtro) {
    return agendamentoColetaRepository.buscarProximoAgendamentoPorCooperativa(
      usuarioId,
      filtro.dataInicio(),
      filtro.dataFim(),
      filtro.possuiRecorrencia(),
      filtro.condominioId()
    )
    .map(agendamentoColetaMapper::toDomain);
  }

 
  
}
