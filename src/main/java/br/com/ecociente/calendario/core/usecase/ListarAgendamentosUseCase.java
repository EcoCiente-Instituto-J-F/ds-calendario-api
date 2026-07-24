package br.com.ecociente.calendario.core.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.gateway.AgendamentoColetaGateway;

@Component
public class ListarAgendamentosUseCase {
  
  private final AgendamentoColetaGateway agendamentoColetaGateway;
  public ListarAgendamentosUseCase(AgendamentoColetaGateway agendamentoColetaGateway) {
    this.agendamentoColetaGateway = agendamentoColetaGateway;
  } 

  public List<AgendamentoColeta> execute() {

    return agendamentoColetaGateway.buscarTodos();
  }
}
