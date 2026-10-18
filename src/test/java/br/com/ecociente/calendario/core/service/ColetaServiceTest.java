package br.com.ecociente.calendario.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.ecociente.calendario.core.domain.Coleta;
import br.com.ecociente.calendario.core.domain.NovaColeta;
import br.com.ecociente.calendario.core.domain.StatusType;
import br.com.ecociente.calendario.core.exception.*;
import br.com.ecociente.calendario.core.gateway.ColetaGateway;

class ColetaServiceTest {

  private ColetaGateway gateway;
  private ColetaService service;

  @BeforeEach
  void configurar() {
    gateway = mock(ColetaGateway.class);
    service = new ColetaService(gateway);
  }

  @ParameterizedTest
  @CsvSource({
      "10,true,false,SINDICO",
      "20,false,true,COOPERATIVA"
  })
  void deveCriarPelaParteRepresentada(
      int usuario,
      boolean sindico,
      boolean cooperativa,
      String origem) {

    prepararCriacao(usuario, sindico, cooperativa);

    NovaColeta entrada = entrada();
    Coleta salva = coleta(StatusType.AGENDADO, usuario, origem);

    when(gateway.criar(usuario, origem, entrada))
        .thenReturn(salva);

    assertSame(salva, service.criar(usuario, entrada));

    verify(gateway).criar(usuario, origem, entrada);
  }

  @ParameterizedTest
  @CsvSource({
      "false,false",
      "true,true"
  })
  void naoDeveCriarSemRepresentacaoInequivoca(
      boolean sindico,
      boolean cooperativa) {

    prepararCriacao(10, sindico, cooperativa);

    assertThrows(
        OperacaoColetaNaoPermitidaException.class,
        () -> service.criar(10, entrada()));

    verificarQueNaoCriou();
  }

  @Test
  void naoDeveCriarComUsuarioInativo() {
    assertThrows(
        OperacaoColetaNaoPermitidaException.class,
        () -> service.criar(10, entrada()));

    verificarQueNaoCriou();
  }

  @Test
  void naoDeveCriarSemUsuario() {
    assertThrows(
        OperacaoColetaNaoPermitidaException.class,
        () -> service.criar(null, entrada()));

    verifyNoInteractions(gateway);
  }

  @Test
  void naoDeveCriarComCondominioInexistente() {
    when(gateway.usuarioAtivo(10)).thenReturn(true);

    assertThrows(
        CondominioNaoEncontradoException.class,
        () -> service.criar(10, entrada()));

    verificarQueNaoCriou();
  }

  @Test
  void naoDeveCriarComCooperativaInexistente() {
    when(gateway.usuarioAtivo(10)).thenReturn(true);
    when(gateway.condominioAtivo(1)).thenReturn(true);

    assertThrows(
        CooperativaNaoEncontradaException.class,
        () -> service.criar(10, entrada()));

    verificarQueNaoCriou();
  }

  @Test
  void naoDeveCriarComInicioNoPassado() {
    when(gateway.usuarioAtivo(10)).thenReturn(true);

    var entrada = new NovaColeta(
        1, 1, OffsetDateTime.now().minusDays(1), null);

    assertThrows(
        AgendamentoInvalidoException.class,
        () -> service.criar(10, entrada));

    verificarQueNaoCriou();
  }

  @Test
  void naoDeveCriarComFimAnteriorAoInicio() {
    when(gateway.usuarioAtivo(10)).thenReturn(true);

    var inicio = OffsetDateTime.now().plusDays(2);
    var entrada = new NovaColeta(
        1, 1, inicio, inicio.minusHours(1));

    assertThrows(
        AgendamentoInvalidoException.class,
        () -> service.criar(10, entrada));

    verificarQueNaoCriou();
  }

  @Test
  void deveAceitarFimOmitido() {
    prepararCriacao(10, true, false);

    var entrada = new NovaColeta(
        1, 1, OffsetDateTime.now().plusDays(2), null);

    service.criar(10, entrada);

    verify(gateway).criar(10, "SINDICO", entrada);
  }

  @ParameterizedTest
  @CsvSource({
      "10,20,SINDICO,false,true",
      "20,10,COOPERATIVA,true,false"
  })
  void destinatarioDeveResponder(
      int solicitante,
      int destinatario,
      String origem,
      boolean sindico,
      boolean cooperativa) {

    prepararAtualizacao(
        destinatario,
        coleta(StatusType.AGENDADO, solicitante, origem),
        sindico,
        cooperativa);

    service.atualizarStatus(
        destinatario, 100, StatusType.CONFIRMADO);

    verify(gateway).atualizarStatus(
        100, StatusType.CONFIRMADO, destinatario);
  }

  @ParameterizedTest
  @ValueSource(strings = {"CONFIRMADO", "RECUSADO", "CANCELADO"})
  void solicitanteNaoDeveResponderOuCancelar(String status) {
    prepararAtualizacao(
        10,
        coleta(StatusType.AGENDADO, 10, "SINDICO"),
        true,
        false);

    assertThrows(
        OperacaoColetaNaoPermitidaException.class,
        () -> service.atualizarStatus(
            10, 100, StatusType.valueOf(status)));

    verificarQueNaoAtualizou();
  }

  @Test
  void usuarioSemVinculoNaoDeveAlterar() {
    prepararAtualizacao(
        30,
        coleta(StatusType.AGENDADO, 10, "SINDICO"),
        false,
        false);

    assertThrows(
        OperacaoColetaNaoPermitidaException.class,
        () -> service.atualizarStatus(
            30, 100, StatusType.CONFIRMADO));

    verificarQueNaoAtualizou();
  }

  @Test
  void outroRepresentanteDoLadoSolicitanteNaoDeveResponder() {
    prepararAtualizacao(
        40,
        coleta(StatusType.AGENDADO, 10, "SINDICO"),
        true,
        false);

    assertThrows(
        OperacaoColetaNaoPermitidaException.class,
        () -> service.atualizarStatus(
            40, 100, StatusType.CONFIRMADO));

    verificarQueNaoAtualizou();
  }

  @Test
  void sindicoNaoDeveMarcarComoRealizado() {
    prepararAtualizacao(
        10,
        coleta(StatusType.CONFIRMADO, 20, "COOPERATIVA"),
        true,
        false);

    assertThrows(
        OperacaoColetaNaoPermitidaException.class,
        () -> service.atualizarStatus(
            10, 100, StatusType.REALIZADO));

    verificarQueNaoAtualizou();
  }

  @Test
  void cooperativaSolicitanteDeveConcluirPreservandoResposta() {
    var base = coleta(
        StatusType.CONFIRMADO, 20, "COOPERATIVA");

    var confirmada = new Coleta(
        base.id(),
        base.condominioId(),
        base.cooperativaId(),
        base.status(),
        base.dataInicio(),
        base.dataFim(),
        false,
        20,
        "COOPERATIVA",
        10,
        OffsetDateTime.now().minusMinutes(1));

    prepararAtualizacao(20, confirmada, false, true);

    service.atualizarStatus(20, 100, StatusType.REALIZADO);

    verify(gateway).atualizarStatus(
        100, StatusType.REALIZADO, 10);
  }

  @ParameterizedTest
  @MethodSource("transicoes")
  void deveValidarTodasAsTransicoes(
      StatusType atual,
      StatusType novo,
      boolean permitida) {

    prepararAtualizacao(
        20,
        coleta(atual, 10, "SINDICO"),
        false,
        true);

    if (permitida) {
      assertDoesNotThrow(
          () -> service.atualizarStatus(20, 100, novo));

      verify(gateway).atualizarStatus(
          eq(100), eq(novo), any());
    } else {
      assertThrows(
          AgendamentoInvalidoException.class,
          () -> service.atualizarStatus(20, 100, novo));

      verificarQueNaoAtualizou();
    }
  }

  static Stream<Arguments> transicoes() {
    return Stream.of(StatusType.values())
        .flatMap(atual -> Stream.of(StatusType.values())
            .map(novo -> {
              boolean permitida =
                  atual == StatusType.AGENDADO
                      && (novo == StatusType.CONFIRMADO
                          || novo == StatusType.RECUSADO
                          || novo == StatusType.CANCELADO)
                  || atual == StatusType.CONFIRMADO
                      && (novo == StatusType.REALIZADO
                          || novo == StatusType.CANCELADO);

              return Arguments.of(atual, novo, permitida);
            }));
  }

  @Test
  void naoDeveResponderSemOrigem() {
    prepararAtualizacao(
        20,
        coleta(StatusType.AGENDADO, null, null),
        false,
        true);

    assertThrows(
        AgendamentoInvalidoException.class,
        () -> service.atualizarStatus(
            20, 100, StatusType.CONFIRMADO));

    verificarQueNaoAtualizou();
  }

  @Test
  void naoDeveAtualizarAgendamentoInexistente() {
    when(gateway.usuarioAtivo(20)).thenReturn(true);
    when(gateway.buscarComBloqueio(100))
        .thenReturn(Optional.empty());

    assertThrows(
        AgendamentoNaoEncontradoException.class,
        () -> service.atualizarStatus(
            20, 100, StatusType.CONFIRMADO));

    verificarQueNaoAtualizou();
  }

  @Test
  void naoDeveAtualizarSemStatus() {
    when(gateway.usuarioAtivo(20)).thenReturn(true);

    assertThrows(
        AgendamentoInvalidoException.class,
        () -> service.atualizarStatus(20, 100, null));

    verificarQueNaoAtualizou();
  }

  private void prepararCriacao(
      int usuario,
      boolean sindico,
      boolean cooperativa) {

    when(gateway.usuarioAtivo(usuario)).thenReturn(true);
    when(gateway.condominioAtivo(1)).thenReturn(true);
    when(gateway.cooperativaAtiva(1)).thenReturn(true);
    when(gateway.sindicoResponsavel(usuario, 1))
        .thenReturn(sindico);
    when(gateway.responsavelCooperativa(usuario, 1))
        .thenReturn(cooperativa);
  }

  private void prepararAtualizacao(
      int usuario,
      Coleta coleta,
      boolean sindico,
      boolean cooperativa) {

    when(gateway.usuarioAtivo(usuario)).thenReturn(true);
    when(gateway.buscarComBloqueio(100))
        .thenReturn(Optional.of(coleta));
    when(gateway.sindicoResponsavel(usuario, 1))
        .thenReturn(sindico);
    when(gateway.responsavelCooperativa(usuario, 1))
        .thenReturn(cooperativa);
  }

  private NovaColeta entrada() {
    var inicio = OffsetDateTime.now().plusDays(2);
    return new NovaColeta(1, 1, inicio, inicio.plusHours(1));
  }

  private Coleta coleta(
      StatusType status,
      Integer solicitante,
      String origem) {

    var entrada = entrada();

    return new Coleta(
        100, 1, 1,
        status,
        entrada.dataInicio(),
        entrada.dataFim(),
        false,
        solicitante,
        origem,
        null,
        null);
  }

  private void verificarQueNaoCriou() {
    verify(gateway, never()).criar(
        anyInt(), anyString(), any(NovaColeta.class));
  }

  private void verificarQueNaoAtualizou() {
    verify(gateway, never()).atualizarStatus(
        anyInt(), any(StatusType.class), any());
  }
}