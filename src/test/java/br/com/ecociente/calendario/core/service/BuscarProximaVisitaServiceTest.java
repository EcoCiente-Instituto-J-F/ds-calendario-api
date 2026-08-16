package br.com.ecociente.calendario.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.usecase.ConsultaAgendamentoPorPerfil;

@ExtendWith(MockitoExtension.class)
class BuscarProximaVisitaServiceTest {
  
  @Mock
  private ResolvedorConsultaAgendamento resolvedor;

  @InjectMocks
  private BuscarProximaVisitaService service;

  private AgendamentoFiltro filtro;
  private ConsultaAgendamentoPorPerfil estrategia;

  @BeforeEach
  void setUp() {
    filtro = new AgendamentoFiltro(null,null,null,null,null,null);
    estrategia = mock(ConsultaAgendamentoPorPerfil.class);
  }

  @Test
  @DisplayName("Deve resolver a estratégia correta e delegar a busca do próximo")
  void shouldResolveStrategyAndDelegateBuscaProximo(){
    AgendamentoColeta agendamentoColeta = AgendamentoColeta.builder().id(1).build();

    when(resolvedor.resolver(Perfil.SINDICO)).thenReturn(estrategia);
    when(estrategia.buscarProximo(1, filtro)).thenReturn(Optional.of(agendamentoColeta));

    Optional<AgendamentoColeta> resultado = service.executar(1, filtro, Perfil.SINDICO);

    assertEquals(Optional.of(agendamentoColeta), resultado);
    verify(resolvedor).resolver(Perfil.SINDICO);
    verify(estrategia).buscarProximo(1, filtro);
  }

  @Test
  @DisplayName("Deve retornar empty quando não houver próximo agendamento")
  void shouldReturnEmptyWhenNoNextAgendamento(){
    when(resolvedor.resolver(Perfil.SINDICO)).thenReturn(estrategia);
    when(estrategia.buscarProximo(1, filtro)).thenReturn(Optional.empty());

    Optional<AgendamentoColeta> resultado = service.executar(1, filtro, Perfil.SINDICO);

    assertEquals(Optional.empty(), resultado);
  }
}
