package br.com.ecociente.calendario.core.gateway;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;

public interface AgendamentoColetaGateway {
  AgendamentoColeta salvar (AgendamentoColeta agendamentoColeta);
  Page<AgendamentoColeta> buscarPorSindico(Integer usuarioId, Pageable pageable);
  Page<AgendamentoColeta> buscarPorCooperativa(Integer usuarioId, Pageable pageable);
  Optional<AgendamentoColeta> buscarProximoAgendamentoPorSindico(Integer usuarioId);
  Optional<AgendamentoColeta> buscarProximoAgendamentoPorCooperativa(Integer usuarioId);
  Optional<AgendamentoColeta> buscarPorId(Integer id);
  AgendamentoColeta atualizar(Integer id, AgendamentoColeta agendamentoColeta);
  void deletar(Integer id);
  
}
