package br.com.ecociente.calendario.core.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.gateway.AgendamentoConsultaGateway;
import br.com.ecociente.calendario.core.usecase.ConsultaAgendamentoPorPerfil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConsultaCooperativa implements ConsultaAgendamentoPorPerfil{

  private final AgendamentoConsultaGateway agendamentoConsultaGateway;

  @Override
  public Perfil perfilSuportado() {
    return Perfil.COOPERATIVA;
  }
  
  @Override
  public Page<AgendamentoColeta> listar(Integer usuarioId, AgendamentoFiltro filtro, Pageable pageable ){
    return agendamentoConsultaGateway.buscarPorCooperativa(usuarioId, filtro, pageable);
  }

  @Override
  public Optional<AgendamentoColeta> buscarProximo(Integer usuarioId, AgendamentoFiltro filtro){
    return agendamentoConsultaGateway.buscarProximoAgendamentoPorCooperativa(usuarioId, filtro);
  }
}
