package br.com.ecociente.calendario.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.gateway.AgendamentoMoradorGateway;

class ConsultaMoradorTest {

  private AgendamentoMoradorGateway gateway;
  private ConsultaMorador consulta;

  @BeforeEach
  void configurar() {
    gateway = mock(AgendamentoMoradorGateway.class);
    consulta = new ConsultaMorador(gateway);
  }

  @Test
  void deveSuportarPerfilMorador() {
    assertEquals(Perfil.MORADOR, consulta.perfilSuportado());
  }

  @Test
  void deveSerSelecionadaPeloResolvedor() {
    var resolvedor = new ResolvedorConsultaAgendamento(
        List.of(consulta));

    assertSame(
        consulta,
        resolvedor.resolver(Perfil.MORADOR));
  }

  @Test
  void deveEncaminharUsuarioFiltrosEPaginacao() {
    var filtro = filtro();
    var pageable = PageRequest.of(1, 10);
    Page<AgendamentoColeta> pagina = Page.empty(pageable);

    when(gateway.listar(70, filtro, pageable))
        .thenReturn(pagina);

    assertSame(
        pagina,
        consulta.listar(70, filtro, pageable));

    verify(gateway).listar(70, filtro, pageable);
  }

  @Test
  void deveBuscarProximoAgendamento() {
    var filtro = filtro();
    var agendamento = AgendamentoColeta.builder()
        .id(100)
        .condominioId(1)
        .build();

    when(gateway.buscarProximo(70, filtro))
        .thenReturn(Optional.of(agendamento));

    assertSame(
        agendamento,
        consulta.buscarProximo(70, filtro).orElseThrow());
  }

  @Test
  void devePreservarAusenciaDeProximoAgendamento() {
    var filtro = filtro();

    when(gateway.buscarProximo(70, filtro))
        .thenReturn(Optional.empty());

    assertTrue(consulta.buscarProximo(70, filtro).isEmpty());
  }

  @Test
  void deveConverterPerfilDoJwt() {
    assertEquals(Perfil.MORADOR, Perfil.tipoPerfil("MORADOR"));
    assertEquals(Perfil.MORADOR, Perfil.tipoPerfil(" morador "));
  }

  private AgendamentoFiltro filtro() {
    return new AgendamentoFiltro(
        null, null, null, null, null, null);
  }
}