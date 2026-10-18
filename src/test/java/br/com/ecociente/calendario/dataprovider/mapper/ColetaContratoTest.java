package br.com.ecociente.calendario.dataprovider.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import br.com.ecociente.calendario.core.domain.Coleta;
import br.com.ecociente.calendario.core.domain.StatusType;
import br.com.ecociente.calendario.entrypoint.dto.request.AtualizarStatusColetaRequestDto;
import br.com.ecociente.calendario.entrypoint.dto.request.CriarColetaRequestDto;
import br.com.ecociente.calendario.entrypoint.mapper.ColetaMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ColetaContratoTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  private final ColetaMapper mapper = new ColetaMapper();

  @BeforeAll
  static void configurar() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void encerrar() {
    factory.close();
  }

  @Test
  void deveValidarEConverterCriacao() {
    var inicio = OffsetDateTime.now().plusDays(2);
    var fim = inicio.plusHours(1);
    var request = new CriarColetaRequestDto(1, 2, inicio, fim);

    assertTrue(validator.validate(request).isEmpty());

    var domain = mapper.toDomain(request);

    assertEquals(1, domain.condominioId());
    assertEquals(2, domain.cooperativaId());
    assertEquals(inicio, domain.dataInicio());
    assertEquals(fim, domain.dataFim());
  }

  @Test
  void deveAceitarFimNuloOuIgualAoInicio() {
    var inicio = OffsetDateTime.now().plusDays(2);

    assertTrue(validator.validate(
        new CriarColetaRequestDto(1, 2, inicio, null)).isEmpty());

    assertTrue(validator.validate(
        new CriarColetaRequestDto(1, 2, inicio, inicio)).isEmpty());
  }

  @Test
  void deveRejeitarDadosObrigatoriosAusentes() {
    var erros = validator.validate(
        new CriarColetaRequestDto(null, null, null, null));

    var campos = erros.stream()
        .map(erro -> erro.getPropertyPath().toString())
        .toList();

    assertTrue(campos.contains("condominioId"));
    assertTrue(campos.contains("cooperativaId"));
    assertTrue(campos.contains("dataInicio"));
  }

  @Test
  void deveRejeitarIdsNaoPositivos() {
    assertFalse(validator.validate(
        new CriarColetaRequestDto(
            0, -1, OffsetDateTime.now().plusDays(2), null))
        .isEmpty());
  }

  @Test
  void deveRejeitarInicioPassado() {
    assertFalse(validator.validate(
        new CriarColetaRequestDto(
            1, 2, OffsetDateTime.now().minusDays(1), null))
        .isEmpty());
  }

  @Test
  void deveRejeitarPeriodoInvertido() {
    var inicio = OffsetDateTime.now().plusDays(2);

    assertFalse(validator.validate(
        new CriarColetaRequestDto(
            1, 2, inicio, inicio.minusHours(1)))
        .isEmpty());
  }

  @Test
  void deveExigirStatus() {
    assertFalse(validator.validate(
        new AtualizarStatusColetaRequestDto(null))
        .isEmpty());
  }

  @Test
  void deveConverterTodosOsCamposDaResposta() {
    var inicio = OffsetDateTime.now().plusDays(2);
    var respostaEm = OffsetDateTime.now();

    var coleta = new Coleta(
        100, 1, 2, StatusType.CONFIRMADO,
        inicio, inicio.plusHours(1), false,
        10, "SINDICO", 20, respostaEm);

    var resposta = mapper.toResponse(coleta);

    assertEquals(coleta.id(), resposta.id());
    assertEquals(coleta.condominioId(), resposta.condominioId());
    assertEquals(coleta.cooperativaId(), resposta.cooperativaId());
    assertEquals(coleta.status(), resposta.status());
    assertEquals(coleta.dataInicio(), resposta.dataInicio());
    assertEquals(coleta.dataFim(), resposta.dataFim());
    assertEquals(coleta.possuiRecorrencia(), resposta.possuiRecorrencia());
    assertEquals(
        coleta.solicitadoPorUsuarioId(),
        resposta.solicitadoPorUsuarioId());
    assertEquals(coleta.origemSolicitacao(), resposta.origemSolicitacao());
    assertEquals(
        coleta.respondidoPorUsuarioId(),
        resposta.respondidoPorUsuarioId());
    assertEquals(coleta.respondidoEm(), resposta.respondidoEm());
  }

  @ParameterizedTest
  @CsvSource({
      "1,AGENDADO",
      "14,AGENDADO",
      "2,CONFIRMADO",
      "12,CONFIRMADO",
      "3,RECUSADO",
      "15,RECUSADO",
      "4,REALIZADO",
      "13,REALIZADO",
      "5,CANCELADO",
      "11,CANCELADO"
  })
  void deveReconhecerIdsDoBanco(int id, StatusType esperado) {
    assertEquals(esperado, StatusType.porId(id));
  }

  @Test
  void deveRejeitarStatusDesconhecidoDoBanco() {
    assertThrows(
        IllegalArgumentException.class,
        () -> StatusType.porId(999));
  }
}