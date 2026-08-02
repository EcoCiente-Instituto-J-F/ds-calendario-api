package br.com.ecociente.calendario.core.usecase;

import java.util.Optional;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;

public interface BuscarProximaVisitaUseCase {
  Optional<AgendamentoColeta> executar(Integer usuarioId, String perfil);
}
