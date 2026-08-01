package br.com.ecociente.calendario.core.usecase;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;

@Component
public interface ListarAgendamentosUseCase {

  Page<AgendamentoColeta> executar();
}
