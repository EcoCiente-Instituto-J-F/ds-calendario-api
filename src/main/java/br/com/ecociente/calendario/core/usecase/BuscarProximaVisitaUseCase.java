package br.com.ecociente.calendario.core.usecase;

import java.util.Optional;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;

public interface BuscarProximaVisitaUseCase {
  Optional<AgendamentoColeta> executar(Integer usuarioId, AgendamentoFiltro filtro, String perfil);
}
