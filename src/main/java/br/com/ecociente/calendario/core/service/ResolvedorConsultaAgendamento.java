package br.com.ecociente.calendario.core.service;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
import lombok.RequiredArgsConstructor;

@Component
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
