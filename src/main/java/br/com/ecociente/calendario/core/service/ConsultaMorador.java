package br.com.ecociente.calendario.core.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.gateway.AgendamentoMoradorGateway;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultaMorador implements ConsultaAgendamentoPorPerfil {

  private final AgendamentoMoradorGateway gateway;

  @Override
  public Perfil perfilSuportado() {
    return Perfil.MORADOR;
  }

  @Override
  public Page<AgendamentoColeta> listar(
      Integer usuarioId,
      AgendamentoFiltro filtro,
      Pageable pageable) {

    return gateway.listar(usuarioId, filtro, pageable);
  }

  @Override
  public Optional<AgendamentoColeta> buscarProximo(
      Integer usuarioId,
      AgendamentoFiltro filtro) {

    return gateway.buscarProximo(usuarioId, filtro);
  }
}