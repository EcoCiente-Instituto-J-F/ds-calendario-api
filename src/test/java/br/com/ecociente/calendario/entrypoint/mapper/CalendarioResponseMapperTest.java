package br.com.ecociente.calendario.entrypoint.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.StatusType;
import br.com.ecociente.calendario.entrypoint.dto.response.CalendarioResponseDto;

class CalendarioResponseMapperTest {

  private CalendarioResponseMapper mapper;

  private AgendamentoColeta domain;

  @BeforeEach
  void setUp() {
    mapper = new CalendarioResponseMapper();

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

  @Test
  @DisplayName("Deve mapear domain válido para DTO de resposta")
  void shouldMapDomainToResponseDto() {
    CalendarioResponseDto dto = mapper.toResponseDto(domain);

    assertNotNull(dto);
    assertEquals(1, dto.id());
    assertEquals(10, dto.condominioId());
    assertEquals(20, dto.cooperativaId());
    assertEquals(StatusType.AGENDADO, dto.statusAgendamento());
    assertEquals(LocalDateTime.of(2024, 1, 15, 10, 0), dto.dataInicio());
    assertEquals(false, dto.possuiRecorrencia());
  }

  @Test
  @DisplayName("Deve retornar null quando domain for null")
  void shouldReturnNullWhenDomainIsNull() {
    assertNull(mapper.toResponseDto(null));
  }
}