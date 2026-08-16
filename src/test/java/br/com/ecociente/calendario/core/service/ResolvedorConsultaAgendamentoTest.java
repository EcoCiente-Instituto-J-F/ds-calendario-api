package br.com.ecociente.calendario.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
import br.com.ecociente.calendario.core.usecase.ConsultaAgendamentoPorPerfil;



class ResolvedorConsultaAgendamentoTest {


  
  @Test
  @DisplayName("Deve retornar a estratégia correta quando o perfil for existente")
  void shouldReturnCorrectStrategyWhenPerfilIsSupported(){
    ConsultaAgendamentoPorPerfil sindico = mock(ConsultaAgendamentoPorPerfil.class);
    ConsultaAgendamentoPorPerfil cooperativa = mock(ConsultaAgendamentoPorPerfil.class);
    when(sindico.perfilSuportado()).thenReturn(Perfil.SINDICO);
    when(cooperativa.perfilSuportado()).thenReturn(Perfil.COOPERATIVA);

    ResolvedorConsultaAgendamento resolvedorConsultaAgendamento = new ResolvedorConsultaAgendamento(List.of(sindico,cooperativa));

    assertEquals(sindico, resolvedorConsultaAgendamento.resolver(Perfil.SINDICO));
    assertEquals(cooperativa, resolvedorConsultaAgendamento.resolver(Perfil.COOPERATIVA));
  }

  @Test
  @DisplayName("Deve lançar a exceção PerfilNaoAutorizadoException quando nenhuma perfil for suportado")
  void shouldReturnPerfilNaoAutorizadoWhenNoStrategySuppostsPerfil(){
    ResolvedorConsultaAgendamento resolvedor = new ResolvedorConsultaAgendamento(List.of());

    assertThrows(PerfilNaoAutorizadoException.class,() -> resolvedor.resolver(Perfil.SINDICO));

  }
}
