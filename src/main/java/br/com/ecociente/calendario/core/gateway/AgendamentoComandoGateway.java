package br.com.ecociente.calendario.core.gateway;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;

public interface AgendamentoComandoGateway {
  AgendamentoColeta salvar (AgendamentoColeta agendamentoColeta);
  AgendamentoColeta atualizar(Integer id, AgendamentoColeta agendamentoColeta);
  void deletar(Integer id);
  
}
