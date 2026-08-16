package br.com.ecociente.calendario.core.service;

import java.util.List;

import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
import br.com.ecociente.calendario.core.usecase.ConsultaAgendamentoPorPerfil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResolvedorConsultaAgendamento {
  
  private final List<ConsultaAgendamentoPorPerfil> perfils;

  public ConsultaAgendamentoPorPerfil resolver(Perfil perfil){
    return perfils.stream()
      .filter(e -> e.perfilSuportado() == perfil)
      .findFirst()
      .orElseThrow(() -> new PerfilNaoAutorizadoException(perfil.name()));
  }
}
