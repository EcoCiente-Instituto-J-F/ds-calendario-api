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
class ConsultaSindicoTest {
  
  @Mock
  private AgendamentoConsultaGateway gateway;

  @InjectMocks
  private ConsultaSindico estrategia;

  private AgendamentoFiltro filtro;
  private Pageable pageable;

  @BeforeEach
  void setUp(){
    filtro = new AgendamentoFiltro(null,null,null,null,null,null);
    pageable = PageRequest.of(0,10);
  }

  @Test
  @DisplayName("Deve suportar o perfil SINDICO")
  void shouldSupportSindicoPerfil(){
    assertEquals(Perfil.SINDICO, estrategia.perfilSuportado());
  }

  @Test
  @DisplayName("Deve delegar para gateway.buscarPorSindico ao listar")
  void shouldDelegateToBuscarPorSindicoWhenListar(){
    Page<AgendamentoColeta> pagina = new PageImpl<>(List.of());
    when(gateway.buscarPorSindico(1, filtro, pageable)).thenReturn(pagina);

    Page<AgendamentoColeta> resultado = estrategia.listar(1, filtro, pageable);

    assertEquals(pagina, resultado);
    verify(gateway).buscarPorSindico(1, filtro, pageable);
  }

  @Test
  @DisplayName("Deve delegar para gateway.buscarProximoPorSindico ao buscar próximo")
  void shouldDelegateToBuscarProximoPorSindico(){
    AgendamentoColeta agendamentoColeta = AgendamentoColeta.builder().id(1).build();
    when(gateway.buscarProximoAgendamentoPorSindico(1, filtro)).thenReturn(Optional.of(agendamentoColeta));

    Optional<AgendamentoColeta> resultado = estrategia.buscarProximo(1, filtro);

    assertEquals(Optional.of(agendamentoColeta), resultado);
    verify(gateway).buscarProximoAgendamentoPorSindico(1, filtro);
  }
}
