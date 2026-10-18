package br.com.ecociente.calendario.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import org.testcontainers.containers.PostgreSQLContainer;

import br.com.ecociente.calendario.config.security.JwtService;
import br.com.ecociente.calendario.config.security.SecurityConfig;
import br.com.ecociente.calendario.core.domain.StatusType;
import br.com.ecociente.calendario.core.exception.AgendamentoInvalidoException;
import br.com.ecociente.calendario.core.service.ColetaService;
import br.com.ecociente.calendario.dataprovider.gateway.ColetaGatewayImpl;
import br.com.ecociente.calendario.dataprovider.mapper.ColetaPersistenciaMapper;
import br.com.ecociente.calendario.entrypoint.controller.ColetaController;
import br.com.ecociente.calendario.entrypoint.exception.ColetaExceptionHandler;
import br.com.ecociente.calendario.entrypoint.exception.GlobalExceptionHandler;
import br.com.ecociente.calendario.entrypoint.mapper.ColetaMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringJUnitConfig(ColetaIntegrationTest.Config.class)
@WebAppConfiguration
@TestPropertySource(properties = {
    "app.security.jwt.secret=0123456789abcdef0123456789abcdef"
})
@Execution(ExecutionMode.SAME_THREAD)
class ColetaIntegrationTest {

  private static final String SECRET =
      "0123456789abcdef0123456789abcdef";

  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine");

  static {
    POSTGRES.start();
  }

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private JdbcTemplate jdbc;

  @Autowired
  private ColetaService service;

  private MockMvc mvc;

  @BeforeEach
  void preparar() throws Exception {
    String schema = recurso("sql/ecociente-schema.sql");
    String dados = recurso("sql/coletas-dados-test.sql");

    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {

      statement.execute("DROP SCHEMA public CASCADE");
      statement.execute("CREATE SCHEMA public");
      statement.execute(schema);
      statement.execute(dados);
    }

    Integer campos = jdbc.queryForObject("""
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'tb_agendamentos_coletas'
          AND column_name IN (
            'solicitado_por_usuario_id',
            'origem_solicitacao',
            'respondido_por_usuario_id',
            'respondido_em'
          )
        """, Integer.class);

    assertEquals(
        4, campos,
        "Atualize o schema de teste com os quatro campos novos");

    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @ParameterizedTest
  @CsvSource({
      "10,SINDICO",
      "20,COOPERATIVA"
  })
  void deveCriarComIdentidadeDoJwt(
      int usuario,
      String origem) throws Exception {

    int id = criar(usuario);

    var dados = jdbc.queryForMap("""
        SELECT solicitado_por_usuario_id,
               origem_solicitacao,
               status_agendamento_id,
               possui_recorrencia,
               respondido_por_usuario_id,
               respondido_em
        FROM tb_agendamentos_coletas
        WHERE id_agendamento_coleta = ?
        """, id);

    assertEquals(usuario, dados.get("solicitado_por_usuario_id"));
    assertEquals(origem, dados.get("origem_solicitacao"));
    assertEquals(1, dados.get("status_agendamento_id"));
    assertEquals(false, dados.get("possui_recorrencia"));
    assertNull(dados.get("respondido_por_usuario_id"));
    assertNull(dados.get("respondido_em"));

    assertEquals(0L, jdbc.queryForObject("""
        SELECT COUNT(*)
        FROM tb_rel_recorrencias_agendamentos
        WHERE agendamento_coleta_id = ?
        """, Long.class, id));
  }

  @ParameterizedTest
  @CsvSource({
      "10,20,CONFIRMADO,2",
      "20,10,CONFIRMADO,2",
      "10,20,RECUSADO,3",
      "20,10,RECUSADO,3",
      "10,20,CANCELADO,5",
      "20,10,CANCELADO,5"
  })
  void destinatarioDeveResponder(
      int solicitante,
      int destinatario,
      String status,
      int statusId) throws Exception {

    int id = criar(solicitante);

    alterar(id, destinatario, status, 200);

    assertEquals(statusId, statusBanco(id));
    assertEquals(destinatario, respondidoPor(id));
    assertNotNull(respondidoEm(id));
  }

  @ParameterizedTest
  @CsvSource({
      "10,CONFIRMADO",
      "10,RECUSADO",
      "10,CANCELADO",
      "20,CONFIRMADO",
      "20,RECUSADO",
      "20,CANCELADO"
  })
  void solicitanteNaoDeveResponder(
      int solicitante,
      String novoStatus) throws Exception {

    int id = criar(solicitante);

    alterar(id, solicitante, novoStatus, 403);

    assertEquals(1, statusBanco(id));
    assertNull(respondidoPor(id));
  }

  @ParameterizedTest
  @ValueSource(ints = {30, 40, 50})
  void terceiroNaoDeveCriarNemResponder(int usuario)
      throws Exception {

    mvc.perform(post("/api/v1/agendamentos")
            .header("Authorization", bearer(usuario))
            .contentType(MediaType.APPLICATION_JSON)
            .content(criacaoJson()))
        .andExpect(status().isForbidden());

    assertEquals(0L, quantidadeColetas());

    int id = criar(10);

    alterar(id, usuario, "CONFIRMADO", 403);

    assertEquals(1, statusBanco(id));
  }

  @ParameterizedTest
  @CsvSource({
      "10,20",
      "20,10"
  })
  void cooperativaDeveConcluirAposConfirmacao(
      int solicitante,
      int destinatario) throws Exception {

    int id = criar(solicitante);

    alterar(id, 20, "REALIZADO", 400);
    alterar(id, destinatario, "CONFIRMADO", 200);

    OffsetDateTime respostaAntes = respondidoEm(id);

    alterar(id, 10, "REALIZADO", 403);
    alterar(id, 20, "REALIZADO", 200);

    assertEquals(4, statusBanco(id));
    assertEquals(destinatario, respondidoPor(id));
    assertEquals(respostaAntes, respondidoEm(id));
  }

  @ParameterizedTest
  @CsvSource({
      "10,20",
      "20,10"
  })
  void somenteDestinatarioDeveCancelarConfirmada(
      int solicitante,
      int destinatario) throws Exception {

    int id = criar(solicitante);

    alterar(id, destinatario, "CONFIRMADO", 200);
    alterar(id, solicitante, "CANCELADO", 403);
    alterar(id, destinatario, "CANCELADO", 200);

    assertEquals(5, statusBanco(id));
  }

  @ParameterizedTest
  @ValueSource(strings = {"RECUSADO", "CANCELADO"})
  void naoDeveReabrirStatusFinal(String novoStatus)
      throws Exception {

    int id = criar(10);

    alterar(id, 20, novoStatus, 200);
    alterar(id, 20, "CONFIRMADO", 400);
  }

  @Test
  void naoDeveCancelarRealizada() throws Exception {
    int id = criar(10);

    alterar(id, 20, "CONFIRMADO", 200);
    alterar(id, 20, "REALIZADO", 200);
    alterar(id, 20, "CANCELADO", 400);

    assertEquals(4, statusBanco(id));
  }

  @Test
  void devePreservarDadosDaSolicitacaoAoResponder()
      throws Exception {

    int id = criar(10);

    var antes = dadosSolicitacao(id);

    alterar(id, 20, "CONFIRMADO", 200);

    assertEquals(antes, dadosSolicitacao(id));
  }

  @Test
  void deveRetornar404ParaAgendamentoInexistente()
      throws Exception {

    alterar(999999, 20, "CONFIRMADO", 404);
  }

  @Test
  void usuarioInativoNaoDeveCriar() throws Exception {
    jdbc.update("""
        UPDATE tb_usuarios
        SET ativo = FALSE
        WHERE id_usuario = 10
        """);

    mvc.perform(post("/api/v1/agendamentos")
            .header("Authorization", bearer(10))
            .contentType(MediaType.APPLICATION_JSON)
            .content(criacaoJson()))
        .andExpect(status().isForbidden());

    assertEquals(0L, quantidadeColetas());
  }

  @Test
  void condominioInativoNaoDeveReceberSolicitacao()
      throws Exception {

    jdbc.update("""
        UPDATE tb_condominios
        SET ativo = FALSE
        WHERE id_condominio = 1
        """);

    mvc.perform(post("/api/v1/agendamentos")
            .header("Authorization", bearer(20))
            .contentType(MediaType.APPLICATION_JSON)
            .content(criacaoJson()))
        .andExpect(status().isNotFound());

    assertEquals(0L, quantidadeColetas());
  }

  @Test
  void deveValidarCorpoDaCriacao() throws Exception {
    mvc.perform(post("/api/v1/agendamentos")
            .header("Authorization", bearer(10))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "condominioId": 0,
                  "cooperativaId": -1,
                  "dataInicio": "2020-01-01T10:00:00-03:00"
                }
                """))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.codigoError").value("ERRO_VALIDACAO"));

    assertEquals(0L, quantidadeColetas());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "{}",
      "{\"status\":null}",
      "{\"status\":\"INEXISTENTE\"}",
      "{"
  })
  void deveRejeitarCorpoDeStatusInvalido(String corpo)
      throws Exception {

    int id = criar(10);

    mvc.perform(patch("/api/v1/agendamentos/{id}/status", id)
            .header("Authorization", bearer(20))
            .contentType(MediaType.APPLICATION_JSON)
            .content(corpo))
        .andExpect(status().isBadRequest());

    assertEquals(1, statusBanco(id));
  }

  @Test
  void deveExigirTokenNosDoisEndpoints() throws Exception {
    mvc.perform(post("/api/v1/agendamentos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(criacaoJson()))
        .andExpect(status().isUnauthorized());

    int id = criar(10);

    mvc.perform(patch("/api/v1/agendamentos/{id}/status", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"CONFIRMADO\"}"))
        .andExpect(status().isUnauthorized());

    assertEquals(1, statusBanco(id));
  }

  @Test
  void deveRejeitarTokenExpirado() throws Exception {
    String token = token(
        10, "SINDICO", SECRET,
        Instant.now().minusSeconds(60));

    criarComTokenInvalido(token);
  }

  @Test
  void deveRejeitarOutraAssinatura() throws Exception {
    String token = token(
        10, "SINDICO",
        "abcdef0123456789abcdef0123456789ab",
        Instant.now().plusSeconds(600));

    criarComTokenInvalido(token);
  }

  @Test
  void deveRejeitarPerfilForaDosPermitidos() throws Exception {
    String token = token(
        30, "MORADOR", SECRET,
        Instant.now().plusSeconds(600));

    mvc.perform(post("/api/v1/agendamentos")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(criacaoJson()))
        .andExpect(status().isForbidden());
  }

  @Test
  void respostasConcorrentesNaoDevemSobrescreverDecisao()
      throws Exception {

    int id = criar(10);

    CountDownLatch prontas = new CountDownLatch(2);
    CountDownLatch iniciar = new CountDownLatch(1);
    ExecutorService executor = Executors.newFixedThreadPool(2);

    try {
      Future<Boolean> confirmar = executor.submit(
          () -> responderConcorrente(
              id, StatusType.CONFIRMADO, prontas, iniciar));

      Future<Boolean> recusar = executor.submit(
          () -> responderConcorrente(
              id, StatusType.RECUSADO, prontas, iniciar));

      assertTrue(prontas.await(10, TimeUnit.SECONDS));
      iniciar.countDown();

      boolean confirmou = confirmar.get(20, TimeUnit.SECONDS);
      boolean recusou = recusar.get(20, TimeUnit.SECONDS);

      assertNotEquals(
          confirmou, recusou,
          "Apenas uma das respostas pode ser aplicada");

      assertTrue(List.of(2, 3).contains(statusBanco(id)));
      assertEquals(20, respondidoPor(id));

    } finally {
      iniciar.countDown();
      executor.shutdownNow();
    }
  }

  private boolean responderConcorrente(
      int id,
      StatusType novoStatus,
      CountDownLatch prontas,
      CountDownLatch iniciar) throws Exception {

    prontas.countDown();

    if (!iniciar.await(10, TimeUnit.SECONDS)) {
      throw new IllegalStateException("Tempo de início excedido");
    }

    try {
      service.atualizarStatus(20, id, novoStatus);
      return true;
    } catch (AgendamentoInvalidoException exception) {
      return false;
    }
  }

  private void criarComTokenInvalido(String token)
      throws Exception {

    mvc.perform(post("/api/v1/agendamentos")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(criacaoJson()))
        .andExpect(status().isUnauthorized());

    assertEquals(0L, quantidadeColetas());
  }

  private int criar(int usuario) throws Exception {
    mvc.perform(post("/api/v1/agendamentos")
            .header("Authorization", bearer(usuario))
            .contentType(MediaType.APPLICATION_JSON)
            .content(criacaoJson()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("AGENDADO"))
        .andExpect(jsonPath("$.possuiRecorrencia").value(false));

    return jdbc.queryForObject("""
        SELECT MAX(id_agendamento_coleta)
        FROM tb_agendamentos_coletas
        """, Integer.class);
  }

  private void alterar(
      int id,
      int usuario,
      String novoStatus,
      int esperado) throws Exception {

    mvc.perform(patch("/api/v1/agendamentos/{id}/status", id)
            .header("Authorization", bearer(usuario))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"" + novoStatus + "\"}"))
        .andExpect(status().is(esperado));
  }

  private String criacaoJson() {
    var inicio = OffsetDateTime.now().plusDays(2);

    return """
        {
          "condominioId": 1,
          "cooperativaId": 1,
          "dataInicio": "%s",
          "dataFim": "%s"
        }
        """.formatted(inicio, inicio.plusHours(1));
  }

  private java.util.Map<String, Object> dadosSolicitacao(int id) {
    return jdbc.queryForMap("""
        SELECT condominio_id, cooperativa_id,
               data_inicio, data_fim,
               possui_recorrencia,
               solicitado_por_usuario_id, origem_solicitacao
        FROM tb_agendamentos_coletas
        WHERE id_agendamento_coleta = ?
        """, id);
  }

  private Integer statusBanco(int id) {
    return jdbc.queryForObject("""
        SELECT status_agendamento_id
        FROM tb_agendamentos_coletas
        WHERE id_agendamento_coleta = ?
        """, Integer.class, id);
  }

  private Integer respondidoPor(int id) {
    return jdbc.queryForObject("""
        SELECT respondido_por_usuario_id
        FROM tb_agendamentos_coletas
        WHERE id_agendamento_coleta = ?
        """, Integer.class, id);
  }

  private OffsetDateTime respondidoEm(int id) {
    return jdbc.queryForObject("""
        SELECT respondido_em
        FROM tb_agendamentos_coletas
        WHERE id_agendamento_coleta = ?
        """,
        (rs, rowNum) ->
            rs.getObject("respondido_em", OffsetDateTime.class),
        id);
  }

  private long quantidadeColetas() {
    return jdbc.queryForObject(
        "SELECT COUNT(*) FROM tb_agendamentos_coletas",
        Long.class);
  }

  private String bearer(int usuario) {
    String perfil = usuario == 20 || usuario == 50
        ? "COOPERATIVA"
        : "SINDICO";

    return "Bearer " + token(
        usuario, perfil, SECRET,
        Instant.now().plusSeconds(600));
  }

  private String token(
      int usuario,
      String perfil,
      String segredo,
      Instant expiracao) {

    return Jwts.builder()
        .subject("usuario-" + usuario + "@teste.com")
        .claim("usuarioId", usuario)
        .claim("perfil", perfil)
        .issuedAt(Date.from(Instant.now().minusSeconds(120)))
        .expiration(Date.from(expiracao))
        .signWith(Keys.hmacShaKeyFor(
            segredo.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  private String recurso(String caminho) throws Exception {
    try (var stream = new ClassPathResource(caminho)
        .getInputStream()) {

      return new String(
          stream.readAllBytes(),
          StandardCharsets.UTF_8);
    }
  }

  @Configuration
  @EnableWebMvc
  @EnableTransactionManagement
  @Import({
      SecurityConfig.class,
      JwtService.class,
      ColetaService.class,
      ColetaGatewayImpl.class,
      ColetaPersistenciaMapper.class,
      ColetaController.class,
      ColetaMapper.class,
      ColetaExceptionHandler.class,
      GlobalExceptionHandler.class
  })
  static class Config {

    @Bean
    DataSource dataSource() {
      return new DriverManagerDataSource(
          POSTGRES.getJdbcUrl(),
          POSTGRES.getUsername(),
          POSTGRES.getPassword());
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
      return new JdbcTemplate(dataSource);
    }

    @Bean
    PlatformTransactionManager transactionManager(
        DataSource dataSource) {

      return new DataSourceTransactionManager(dataSource);
    }
  }
}