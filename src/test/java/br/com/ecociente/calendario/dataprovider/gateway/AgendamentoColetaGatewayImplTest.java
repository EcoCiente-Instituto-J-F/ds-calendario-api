package br.com.ecociente.calendario.dataprovider.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import br.com.ecociente.calendario.core.domain.StatusType;
import br.com.ecociente.calendario.core.mapper.AgendamentoColetaMapper;
import br.com.ecociente.calendario.dataprovider.entity.AgendamentoColetaEntity;
import br.com.ecociente.calendario.dataprovider.entity.StatusAgendamentoEntity;
import br.com.ecociente.calendario.dataprovider.repository.AgendamentoColetaRepository;
import br.com.ecociente.calendario.dataprovider.repository.StatusAgendamentoRepository;

@ExtendWith(MockitoExtension.class)
public class AgendamentoColetaGatewayImplTest {

  @Mock
  private AgendamentoColetaRepository agendamentoColetaRepository;

  @Mock
  private AgendamentoColetaMapper agendamentoColetaMapper;

  @Mock
  private StatusAgendamentoRepository statusAgendamentoRepository;

  @InjectMocks
  private AgendamentoColetaGatewayImpl agendamentoColetaGateway;

  private AgendamentoColetaEntity entity;
  private AgendamentoColeta domain;
  private StatusAgendamentoEntity statusAgendamentoEntity;

  @BeforeEach
  void setUp() {
    statusAgendamentoEntity = new StatusAgendamentoEntity();
    statusAgendamentoEntity.setNomeStatus("AGENDADO");

    entity = AgendamentoColetaEntity
    .builder()
    .id(1)
    .condominioId(10)
    .cooperativaId(20)
    .dataInicio(LocalDateTime.of(2024, 1, 15, 10, 0))
    .possuiRecorrencia(false)
    .build();

    entity.setStatusAgendamento(statusAgendamentoEntity);

    domain = AgendamentoColeta
    .builder()
    .id(1)
    .condominioId(10)
    .cooperativaId(20)
    .statusAgendamento(StatusType.AGENDADO)
    .dataInicio(LocalDateTime.of(2024, 1, 15, 10, 0))
    .possuiRecorrencia(false)
    .build();
  }

  @Nested
  @DisplayName("BuscarPorSindico")
  class BuscarPorSindico{

    @Test
    @DisplayName("Deve chamar o repository e mapear o resultado")
    void shouldDelegateToRepositoryAndMapResult() {
      AgendamentoFiltro filtro = new AgendamentoFiltro(null,null,null,null,null,null);
      Pageable pageable = PageRequest.of(0, 10);

      when(agendamentoColetaRepository.buscarPorSindico(any(),any(),any(),any(),any(),any(),any()))
        .thenReturn(new PageImpl<>(List.of(entity)));
      when(agendamentoColetaMapper.toDomain(entity)).thenReturn(domain);
      
      Page<AgendamentoColeta> resultado = agendamentoColetaGateway.buscarPorSindico(1, filtro, pageable);

      assertEquals(1, resultado.getTotalElements());
      assertEquals(1, resultado.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Deve passar status como string quando filtro tiver status")
    void shouldPassStatusAsStringWhenFilterHasStatus() {
      AgendamentoFiltro filtro = new AgendamentoFiltro( StatusType.AGENDADO,null,null,null,null,null);
      Pageable pageable = PageRequest.of(0,10);

      when(agendamentoColetaRepository.buscarPorSindico(eq(1),eq("AGENDADO"),any(),any(),any(),any(),any()))
        .thenReturn(new PageImpl<>(List.of()));
      
      agendamentoColetaGateway.buscarPorSindico(1, filtro, pageable);

      verify(agendamentoColetaRepository).buscarPorSindico(eq(1), eq("AGENDADO"),any(),any(),any(),any(),any());
    }

    @Test
    @DisplayName("Deve passa status como null quando filtro não tiver status")
    void shouldPassStatusAsNullWhenFilterHasNoStatus() {
      AgendamentoFiltro filtro = new AgendamentoFiltro(null,null,null,null,null,null);
      Pageable pageable = PageRequest.of(0,10);

      when(agendamentoColetaRepository.buscarPorSindico(any(),any(),any(),any(),any(),any(),any()))
        .thenReturn(new PageImpl<>(List.of()));
      
      agendamentoColetaGateway.buscarPorSindico(1, filtro, pageable);

      verify(agendamentoColetaRepository).buscarPorSindico(eq(1),eq(null),any(),any(),any(),any(),any());
    }
  }

  @Nested
  @DisplayName("buscarPorCooperativa")
  class BuscarPorCooperativa{

    @Test
    @DisplayName("Deve chamar o repository e mapear o resultado")
    void shouldDelegateToRepositoryAndMapResult() {
      AgendamentoFiltro filtro = new AgendamentoFiltro(null,null,null,null,null,null);
      Pageable pageable = PageRequest.of(0,10);

      when(agendamentoColetaRepository.buscarPorCooperativa(any(),any(),any(),any(),any(),any(),any()))
        .thenReturn(new PageImpl<>(List.of(entity)));
      when(agendamentoColetaMapper.toDomain(entity)).thenReturn(domain);
      
      Page<AgendamentoColeta> resultado = agendamentoColetaGateway.buscarPorCooperativa(2, filtro, pageable);

      assertEquals(1, resultado.getTotalElements());
      assertEquals(1, resultado.getContent().get(0).getId());
    }
  }

  @Nested
  @DisplayName("buscarProximoAgendamentoPorSindico")
  class BuscarProximoPorSindico{

    @Test
    @DisplayName("Deve chamar o repository e mapear o reseultado quando encontrado")
    void shouldDelegateAndMapResultWhenFound() {
      AgendamentoFiltro filtro = new AgendamentoFiltro(null,null,null,null,null,null);

      when(agendamentoColetaRepository.buscarProximoAgendamentoPorSindico(any(),any(),any(),any(),any()))
        .thenReturn(Optional.of(entity));
      when(agendamentoColetaMapper.toDomain(entity)).thenReturn(domain);

      Optional<AgendamentoColeta> resultado = agendamentoColetaGateway.buscarProximoAgendamentoPorSindico(1, filtro);

      assertTrue(resultado.isPresent());
      assertEquals(1, resultado.get().getId());
    }

    @Test
    @DisplayName("Deve retornar optional.empty quando não encontrado")
    void shouldReturnEmptyWhenNotFound() {
        AgendamentoFiltro filtro = new AgendamentoFiltro(null,null,null,null,null,null);

      when(agendamentoColetaRepository.buscarProximoAgendamentoPorSindico(any(),any(),any(),any(),any()))
        .thenReturn(Optional.empty());
      
      Optional<AgendamentoColeta> resultado = agendamentoColetaGateway.buscarProximoAgendamentoPorSindico(1, filtro);
      
      assertTrue(resultado.isEmpty());  
    }
  }
}
