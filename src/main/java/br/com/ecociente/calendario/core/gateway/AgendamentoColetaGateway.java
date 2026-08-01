package br.com.ecociente.calendario.core.gateway;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;

public interface AgendamentoColetaGateway {
  AgendamentoColeta salvar (AgendamentoColeta agendamentoColeta);
  Page<AgendamentoColeta> buscarTodos(Pageable pageable);
  Optional<AgendamentoColeta> buscarPorId(Integer id);
  AgendamentoColeta atualizar(Integer id, AgendamentoColeta agendamentoColeta);
  void deletar(Integer id);
  
}
