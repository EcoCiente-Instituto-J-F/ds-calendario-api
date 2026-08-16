package br.com.ecociente.calendario.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.gateway.AgendamentoConsultaGateway;

@ExtendWith(MockitoExtension.class)
class ConsultaCooperativaTest {
  
  @Mock
  private AgendamentoConsultaGateway gateway;

  @InjectMocks
  private ConsultaCooperativa estrategia;

  private AgendamentoFiltro filtro;
  private Pageable pageable;

  @BeforeEach
  void setUp(){
    filtro = new AgendamentoFiltro(null,null,null,null,null,null);
    pageable = PageRequest.of(0, 10);
  }

  @Test
  @DisplayName("Deve suportar o perfil COOPERATIVA")
  void shouldSupportCooperativaPerfil(){
    assertEquals(Perfil.COOPERATIVA, estrategia.perfilSuportado());
  }

  @Test
  @DisplayName("Deve delegar para gateway.buscarPorCooperativa ao listar")
  void shouldDelegateToBuscarPorCooperativaWhenListar(){
    Page<AgendamentoColeta> pagina = new PageImpl<>(List.of());
    when(gateway.buscarPorCooperativa(2, filtro, pageable)).thenReturn(pagina);
    Page<AgendamentoColeta> resultado = estrategia.listar(2, filtro, pageable);
    assertEquals(pagina, resultado);
    verify(gateway).buscarPorCooperativa(2, filtro, pageable);
  }

  @Test
  @DisplayName("Deve delegar para gateway.buscaProximoPorCooperativa ao buscar próximo")
  void shouldDelegateToBuscarProximoPorCooperativa(){
    AgendamentoColeta agendamentoColeta = AgendamentoColeta.builder().id(2).build();
    when(gateway.buscarProximoAgendamentoPorCooperativa(2, filtro)).thenReturn(Optional.of(agendamentoColeta));

    Optional<AgendamentoColeta> resultado = estrategia.buscarProximo(2, filtro);

    assertEquals(Optional.of(agendamentoColeta), resultado);
    verify(gateway).buscarProximoAgendamentoPorCooperativa(2, filtro);
  }
}
