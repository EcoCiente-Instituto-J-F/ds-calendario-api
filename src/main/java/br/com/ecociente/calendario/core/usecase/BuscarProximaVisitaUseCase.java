package br.com.ecociente.calendario.core.usecase;

import java.util.Optional;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;

public interface BuscarProximaVisitaUseCase {
  Optional<AgendamentoColeta> executar(Integer usuarioId, AgendamentoFiltro filtro, Perfil perfil);
}
