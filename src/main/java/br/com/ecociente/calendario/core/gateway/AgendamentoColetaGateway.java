package br.com.ecociente.calendario.core.gateway;

import java.util.List;
import java.util.Optional;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;

public interface AgendamentoColetaGateway {
  AgendamentoColeta salvar (AgendamentoColeta agendamentoColeta);
  List<AgendamentoColeta> buscarTodos();
  Optional<AgendamentoColeta> buscarPorId(Integer id);
  AgendamentoColeta atualizar(Integer id, AgendamentoColeta agendamentoColeta);
  void deletar(Integer id);
  
}
