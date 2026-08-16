// package br.com.ecociente.calendario.core.service;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;

// import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
// import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
// import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
// import br.com.ecociente.calendario.core.gateway.AgendamentoColetaGateway;

// @ExtendWith(MockitoExtension.class)
// public class AgendamentoBuscasServiceTest {

//   @Mock
//   private AgendamentoColetaGateway agendamentoColetaGateway;

//   @InjectMocks
//   private AgendamentoBuscasService agendamentoBuscasService;

//   private AgendamentoFiltro filtro;
//   private Pageable pageable;
  
//   @BeforeEach
//   void setUp() {
//     filtro = new AgendamentoFiltro(null,null,null,null,null,null);
//     pageable = PageRequest.of(0, 10);
//   }

//   @Nested
//   @DisplayName("ListarAgendamentos")
//   class ListarAgendamentos{

//     @Test
//     @DisplayName("Deve chamar o método buscarPorSindico quando o perfil for SINDICO")
//     void shouldDelegateToBuscarPorSindicoWhenPerfilIsSindico(){
//       Page<AgendamentoColeta> paginaEsperada = new PageImpl<>(List.of());
//       when(agendamentoColetaGateway.buscarPorSindico(eq(1), eq(filtro), eq(pageable)))
//         .thenReturn(paginaEsperada);

//       Page<AgendamentoColeta> resultado = agendamentoBuscasService.executar(1,"SINDICO", filtro, pageable);

//       assertEquals(paginaEsperada, resultado);
//       verify(agendamentoColetaGateway).buscarPorSindico(1, filtro, pageable);
//       verify(agendamentoColetaGateway,never()).buscarPorCooperativa(any(),any(),any());
//     }

//     @Test
//     @DisplayName("Deve chamar o método buscarPorCooperativa quando o perfil for COOPERATIVA")
//     void shouldDelegateToBuscarPorCooperativaWhenPerfilIsCooperativa(){
//       Page<AgendamentoColeta> paginaEsperada = new PageImpl<>(List.of());
//       when(agendamentoColetaGateway.buscarPorCooperativa(eq(2), eq(filtro), eq(pageable)))
//         .thenReturn(paginaEsperada);

//       Page<AgendamentoColeta> resultado = agendamentoBuscasService.executar(2,"COOPERATIVA", filtro, pageable);

//       assertEquals(paginaEsperada, resultado);
//       verify(agendamentoColetaGateway).buscarPorCooperativa(2, filtro, pageable);
//       verify(agendamentoColetaGateway,never()).buscarPorSindico(any(),any(),any());
//     }

//     @Test
//     @DisplayName("Deve funcionar com perfil em minúsculo (case-insensitive)")
//     void shouldWorkWithLowerCasePerfil() {
//       Page<AgendamentoColeta> paginaEsperada = new PageImpl<>(List.of());
//       when(agendamentoColetaGateway.buscarPorSindico(eq(1), eq(filtro),eq(pageable)))
//         .thenReturn(paginaEsperada);
      
//       agendamentoBuscasService.executar(1, "sindico" ,filtro, pageable);
      
//       verify(agendamentoColetaGateway).buscarPorSindico(1, filtro, pageable);
//     }

//     @Test
//     @DisplayName("Deve lançar PerfilNaoAutorizadoException quando perfil for inválido")
//     void shouldThrowPerfilNaoAutorizadoExceptionWhenPerfilIsInvalid(){

//       assertThrows(PerfilNaoAutorizadoException.class,
//         () -> agendamentoBuscasService.executar(1,"ADMIN", filtro, pageable));
      
//       verify(agendamentoColetaGateway, never()).buscarPorSindico(any(), any(), any());
//       verify(agendamentoColetaGateway, never()).buscarPorCooperativa(any(), any(), any());  
//     }
//   }

//   @Nested
//   @DisplayName("buscarProximoAgendamento")
//   class BuscarProximoAgendamento{

//     @Test
//     @DisplayName("Deve chamar o método buscarProximoAgendamentoPorSindico quando o perfil for SINDICO")
//     void shouldDelegarToBuscarProximoPorSindicoWhenPerfilIsSindico() {
//       AgendamentoColeta agendamentoColeta = AgendamentoColeta.builder().id(1).build();
//       when(agendamentoColetaGateway.buscarProximoAgendamentoPorSindico(eq(1), eq(filtro)))
//         .thenReturn(Optional.of(agendamentoColeta));

//       Optional<AgendamentoColeta> resultado = agendamentoBuscasService.executar(1, filtro, "SINDICO");

//       assertEquals(Optional.of(agendamentoColeta),resultado);
//       verify(agendamentoColetaGateway).buscarProximoAgendamentoPorSindico(1, filtro);
//     }
    
//     @Test
//     @DisplayName("Deve chamar o método buscarProximoAgendamentoPorCooperativa quando o perfil for COOPERATIVA")
//     void shouldDelegarToBuscarProximoPorCooperativaWhenPerfilIsCooperativa() {
//       AgendamentoColeta agendamentoColeta = AgendamentoColeta.builder().id(2).build();
//       when(agendamentoColetaGateway.buscarProximoAgendamentoPorCooperativa(eq(2), eq(filtro)))
//         .thenReturn(Optional.of(agendamentoColeta));

//       Optional<AgendamentoColeta> resultado = agendamentoBuscasService.executar(2, filtro, "COOPERATIVA");

//       assertEquals(Optional.of(agendamentoColeta),resultado);
//       verify(agendamentoColetaGateway).buscarProximoAgendamentoPorCooperativa(2, filtro);
//     }

//     @Test
//     @DisplayName("Deve retornar Optional.empty quando não houver próximo agendamento")
//     void shouldReturnEmptyWhenNoNextAgendamento() {
//       when(agendamentoColetaGateway.buscarProximoAgendamentoPorSindico(any(), any()))
//         .thenReturn(Optional.empty());

//       Optional<AgendamentoColeta> resultado = agendamentoBuscasService.executar(1,filtro,"SINDICO");
//       assertEquals(Optional.empty(), resultado);
//     }

//     @Test
//     @DisplayName("Deve lançar PerfilNaoAutorizadoException quando perfil for inválido")
//     void shouldThrowPerfilNaoAutorizadoExceptionWhenPerfilIsInvalid() {
//       assertThrows(PerfilNaoAutorizadoException.class, 
//         () -> agendamentoBuscasService.executar(1, filtro, "ADMIN"));

//       verify(agendamentoColetaGateway,never()).buscarProximoAgendamentoPorSindico(any(), any());
//       verify(agendamentoColetaGateway,never()).buscarProximoAgendamentoPorCooperativa(any(), any());  
//     }
//   }

// }
