package br.com.ecociente.calendario.core.gateway;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;

public interface AgendamentoColetaGateway {
  AgendamentoColeta salvar (AgendamentoColeta agendamentoColeta);
  Page<AgendamentoColeta> buscarPorSindico(Integer usuarioId, AgendamentoFiltro filtro, Pageable pageable);
  Page<AgendamentoColeta> buscarPorCooperativa(Integer usuarioId, AgendamentoFiltro filtro, Pageable pageable);
  Optional<AgendamentoColeta> buscarProximoAgendamentoPorSindico(Integer usuarioId,AgendamentoFiltro filtro);
  Optional<AgendamentoColeta> buscarProximoAgendamentoPorCooperativa(Integer usuarioId,AgendamentoFiltro filtro);
  AgendamentoColeta atualizar(Integer id, AgendamentoColeta agendamentoColeta);
  void deletar(Integer id);
  
}
