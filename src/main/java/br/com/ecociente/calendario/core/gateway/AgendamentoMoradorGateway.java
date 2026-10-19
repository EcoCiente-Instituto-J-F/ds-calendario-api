package br.com.ecociente.calendario.core.gateway;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;

public interface AgendamentoMoradorGateway {

  Page<AgendamentoColeta> listar(
      Integer usuarioId,
      AgendamentoFiltro filtro,
      Pageable pageable);

  Optional<AgendamentoColeta> buscarProximo(
      Integer usuarioId,
      AgendamentoFiltro filtro);
}