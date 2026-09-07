package br.com.ecociente.calendario.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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

@ExtendWith(MockitoExtension.class)
class ListarAgendamentosServiceTest {
  
  @Mock
  private ResolvedorConsultaAgendamento resolvedor;

  @InjectMocks
  private ListarAgendamentoService service;

  private AgendamentoFiltro filtro;
  private Pageable pageable;
  private ConsultaAgendamentoPorPerfil estrategia;

  @BeforeEach
  void setUp(){
    filtro = new AgendamentoFiltro(null,null,null,null,null,null);
    pageable = PageRequest.of(0, 10);
    estrategia = mock(ConsultaAgendamentoPorPerfil.class);
  }

  @Test
  @DisplayName("Deve resolver a estratégia e delegar a listagem")
  void shouldResolveStrategyAndDelegateListagem(){
    Page<AgendamentoColeta> paginaEsperada = new PageImpl<>(List.of());

    when(resolvedor.resolver(Perfil.SINDICO)).thenReturn(estrategia);
    when(estrategia.listar(1, filtro, pageable)).thenReturn(paginaEsperada);

    Page<AgendamentoColeta> resultado = service.executar(1, Perfil.SINDICO, filtro, pageable);

    assertEquals(paginaEsperada, resultado);
    verify(resolvedor).resolver(Perfil.SINDICO);
    verify(estrategia).listar(1, filtro, pageable);
  }
}
