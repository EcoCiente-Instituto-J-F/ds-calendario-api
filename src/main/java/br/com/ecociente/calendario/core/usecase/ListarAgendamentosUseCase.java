package br.com.ecociente.calendario.core.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;

public interface ListarAgendamentosUseCase {

  Page<AgendamentoColeta> executar(
    Integer usuarioId,
    String perfil,
    AgendamentoFiltro filtro,
    Pageable pageable
  );
}
