package br.com.ecociente.calendario.core.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.StatusType;
import br.com.ecociente.calendario.dataprovider.entity.AgendamentoColetaEntity;
import br.com.ecociente.calendario.dataprovider.entity.StatusAgendamentoEntity;

class AgendamentoColetaMapperTest {

  private AgendamentoColetaMapper mapper;

  private AgendamentoColetaEntity entity;
  private AgendamentoColeta domain;
  private StatusAgendamentoEntity statusEntity;

  @BeforeEach
  void setUp() {
    mapper = new AgendamentoColetaMapper();

    statusEntity = new StatusAgendamentoEntity();
    statusEntity.setNomeStatus("AGENDADO");

    entity = AgendamentoColetaEntity.builder()
        .id(1)
        .condominioId(10)
        .cooperativaId(20)
        .dataInicio(LocalDateTime.of(2024, 1, 15, 10, 0))
        .dataFim(LocalDateTime.of(2024, 1, 15, 12, 0))
        .possuiRecorrencia(false)
        .build();
    entity.setStatusAgendamento(statusEntity);

    domain = AgendamentoColeta.builder()
        .id(1)
        .condominioId(10)
        .cooperativaId(20)
        .statusAgendamento(StatusType.AGENDADO)
        .dataInicio(LocalDateTime.of(2024, 1, 15, 10, 0))
        .dataFim(LocalDateTime.of(2024, 1, 15, 12, 0))
        .possuiRecorrencia(false)
        .build();
  }

  @Nested
  @DisplayName("toDomain")
  class ToDomain {

    @Test
    @DisplayName("Deve mapear entity válida para domain")
    void shouldMapEntityToDomain() {
      AgendamentoColeta resultado = mapper.toDomain(entity);

      assertNotNull(resultado);
      assertEquals(1, resultado.getId());
      assertEquals(10, resultado.getCondominioId());
      assertEquals(20, resultado.getCooperativaId());
      assertEquals(StatusType.AGENDADO, resultado.getStatusAgendamento());
      assertEquals(LocalDateTime.of(2024, 1, 15, 10, 0), resultado.getDataInicio());
      assertEquals(false, resultado.getPossuiRecorrencia());
    }

    @Test
    @DisplayName("Deve converter status em minúsculo para enum via toUpperCase")
    void shouldConvertLowercaseStatusToEnum() {
      statusEntity.setNomeStatus("confirmado");
      entity.setStatusAgendamento(statusEntity);

      AgendamentoColeta resultado = mapper.toDomain(entity);

      assertEquals(StatusType.CONFIRMADO, resultado.getStatusAgendamento());
    }

    @Test
    @DisplayName("Deve retornar null quando entity for null")
    void shouldReturnNullWhenEntityIsNull() {
      assertNull(mapper.toDomain(null));
    }
  }

  @Nested
  @DisplayName("toEntity")
  class ToEntity {

    @Test
    @DisplayName("Deve mapear domain válido para entity")
    void shouldMapDomainToEntity() {
      AgendamentoColetaEntity resultado = mapper.toEntity(domain);

      assertNotNull(resultado);
      assertEquals(1, resultado.getId());
      assertEquals(10, resultado.getCondominioId());
      assertEquals(20, resultado.getCooperativaId());
      assertEquals(LocalDateTime.of(2024, 1, 15, 10, 0), resultado.getDataInicio());
      assertEquals(false, resultado.getPossuiRecorrencia());
    }

    @Test
    @DisplayName("Deve retornar null quando domain for null")
    void shouldReturnNullWhenDomainIsNull() {
      assertNull(mapper.toEntity(null));
    }
  }
}