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
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.com.ecociente.calendario.config.security.JwtService;
import br.com.ecociente.calendario.config.security.SecurityConfig;
import br.com.ecociente.calendario.core.service.*;
import br.com.ecociente.calendario.dataprovider.gateway.AgendamentoMoradorGatewayImpl;
import br.com.ecociente.calendario.dataprovider.gateway.ColetaGatewayImpl;
import br.com.ecociente.calendario.dataprovider.mapper.ColetaPersistenciaMapper;
import br.com.ecociente.calendario.entrypoint.controller.AgendamentoController;
import br.com.ecociente.calendario.entrypoint.controller.ColetaController;
import br.com.ecociente.calendario.entrypoint.exception.ColetaExceptionHandler;
import br.com.ecociente.calendario.entrypoint.exception.GlobalExceptionHandler;
import br.com.ecociente.calendario.entrypoint.mapper.AgendamentoFiltroMapper;
import br.com.ecociente.calendario.entrypoint.mapper.CalendarioResponseMapper;
import br.com.ecociente.calendario.entrypoint.mapper.ColetaMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Testcontainers
@SpringJUnitConfig(MoradorCalendarioIntegrationTest.Config.class)
@WebAppConfiguration
@TestPropertySource(properties = {
    "app.security.jwt.secret=0123456789abcdef0123456789abcdef",
    "app.timezone=America/Sao_Paulo"
})
@Execution(ExecutionMode.SAME_THREAD)
class MoradorCalendarioIntegrationTest {

  private static final String SECRET = "0123456789abcdef0123456789abcdef";

  private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  @Autowired
  WebApplicationContext context;

  @Autowired
  DataSource dataSource;

  @Autowired
  JdbcTemplate jdbc;

  private MockMvc mvc;
  private OffsetDateTime inicio;

  @BeforeEach
  void preparar() throws Exception {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {

      statement.execute("DROP SCHEMA public CASCADE");
      statement.execute("CREATE SCHEMA public");

      statement.execute(recurso("sql/ecociente-schema.sql"));

      statement.execute(recurso(
          "sql/morador-calendario-dados-test.sql"));
    }

    inicio = OffsetDateTime.now(ZONA)
        .plusDays(10)
        .truncatedTo(ChronoUnit.MINUTES);

    // Condomínio do morador 70.
    criarAgendamento(100, 1, 1, inicio);
    criarAgendamento(101, 1, 2, inicio.plusHours(1));
    criarAgendamento(102, 1, 5, inicio.minusHours(2));
    criarAgendamento(103, 1, 4, inicio.minusHours(3));
    criarAgendamento(104, 1, 3, inicio.minusHours(4));
    criarAgendamento(105, 1, 2, inicio.minusDays(20));

    // Mais próximo no tempo, mas de OUTRO condomínio.
    criarAgendamento(200, 2, 2, inicio.minusHours(1));

    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  void deveListarSomenteCondominioDoMorador() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("size", "20")
        .param("sort", "id,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(6))
        .andExpect(jsonPath("$.content.length()").value(6))
        .andExpect(jsonPath("$.content[0].id").value(100))
        .andExpect(jsonPath("$.content[5].id").value(105));
  }

  @Test
  void filtroDeOutroCondominioNaoDeveLiberarAcesso()
      throws Exception {

    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("condominioId", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.content").isEmpty());
  }

  @Test
  void outroMoradorDeveVerSomenteSeuCondominio()
      throws Exception {

    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(120)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].id").value(200))
        .andExpect(jsonPath("$.content[0].condominioId").value(2));
  }

  @ParameterizedTest
  @ValueSource(ints = { 80, 90, 100, 110 })
  void naoDeveConsultarSemVinculoValido(int usuario)
      throws Exception {

    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(usuario)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.content").isEmpty());

    mvc.perform(get("/api/v1/agendamentos/proxima")
        .header("Authorization", bearer(usuario)))
        .andExpect(status().isNotFound());
  }

  @Test
  void naoDeveConsultarCondominioInativo() throws Exception {
    jdbc.update("""
        UPDATE tb_condominios
        SET ativo = FALSE
        WHERE id_condominio = 1
        """);

    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void deveFiltrarStatusSemDependerDeMaiusculasNoBanco()
      throws Exception {

    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("status", "CONFIRMADO")
        .param("sort", "id,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].id").value(101))
        .andExpect(jsonPath("$.content[1].id").value(105));
  }

  @Test
  void deveFiltrarPeriodoNoFusoConfigurado() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("dataInicio",
            inicio.toLocalDateTime().toString())
        .param("dataFim",
            inicio.plusDays(1).toLocalDateTime().toString())
        .param("sort", "id,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].id").value(100))
        .andExpect(jsonPath("$.content[1].id").value(101));
  }

  @Test
  void deveFiltrarCooperativa() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("cooperativaId", "999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void devePaginarComOrdenacaoEstavel() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("page", "0")
        .param("size", "2")
        .param("sort", "id,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(6))
        .andExpect(jsonPath("$.totalPages").value(3))
        .andExpect(jsonPath("$.content[0].id").value(100))
        .andExpect(jsonPath("$.content[1].id").value(101));

    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("page", "1")
        .param("size", "2")
        .param("sort", "id,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(102))
        .andExpect(jsonPath("$.content[1].id").value(103));
  }

  @Test
  void proximaDeveIgnorarOutroCondominioEStatusFinais()
      throws Exception {

    mvc.perform(get("/api/v1/agendamentos/proxima")
        .header("Authorization", bearer(70)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(100))
        .andExpect(jsonPath("$.condominioId").value(1));
  }

  @Test
  void proximaNaoDeveAceitarFiltroDeOutroCondominio()
      throws Exception {

    mvc.perform(get("/api/v1/agendamentos/proxima")
        .header("Authorization", bearer(70))
        .param("condominioId", "2"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deveRejeitarPeriodoInvertido() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("dataInicio",
            inicio.plusDays(1).toLocalDateTime().toString())
        .param("dataFim",
            inicio.toLocalDateTime().toString()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deveRejeitarOrdenacaoNaoPermitida() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .param("sort", "campoInexistente,asc"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void moradorNaoDeveCriarOuAlterarAgendamento()
      throws Exception {

    Long quantidadeAntes = jdbc.queryForObject(
        "SELECT COUNT(*) FROM tb_agendamentos_coletas",
        Long.class);

    mvc.perform(post("/api/v1/agendamentos")
        .header("Authorization", bearer(70))
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "condominioId":1,
              "cooperativaId":1,
              "dataInicio":"%s"
            }
            """.formatted(inicio)))
        .andExpect(status().isForbidden());

    mvc.perform(patch("/api/v1/agendamentos/100/status")
        .header("Authorization", bearer(70))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"status\":\"CONFIRMADO\"}"))
        .andExpect(status().isForbidden());

    assertEquals(
        quantidadeAntes,
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM tb_agendamentos_coletas",
            Long.class));

    assertEquals(
        1,
        jdbc.queryForObject("""
            SELECT status_agendamento_id
            FROM tb_agendamentos_coletas
            WHERE id_agendamento_coleta = 100
            """, Integer.class));
  }

  @Test
  void deveExigirAutenticacao() throws Exception {
    mvc.perform(get("/api/v1/agendamentos"))
        .andExpect(status().isUnauthorized());

    mvc.perform(get("/api/v1/agendamentos/proxima"))
        .andExpect(status().isUnauthorized());
  }

  private void criarAgendamento(
      int id,
      int condominio,
      int status,
      OffsetDateTime data) {

    jdbc.update("""
        INSERT INTO tb_agendamentos_coletas (
          id_agendamento_coleta,
          condominio_id,
          cooperativa_id,
          status_agendamento_id,
          data_inicio,
          data_fim,
          possui_recorrencia,
          solicitado_por_usuario_id,
          origem_solicitacao
        )
        OVERRIDING SYSTEM VALUE
        VALUES (?, ?, 1, ?, ?, ?, FALSE, 20, 'COOPERATIVA')
        """,
        id,
        condominio,
        status,
        data,
        data.plusHours(1));
  }

  private String bearer(int usuario) {
    String token = Jwts.builder()
        .subject("morador-" + usuario + "@teste.com")
        .claim("usuarioId", usuario)
        .claim("perfil", "MORADOR")
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(
            Instant.now().plusSeconds(600)))
        .signWith(Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    return "Bearer " + token;
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
  @EnableSpringDataWebSupport
  @EnableTransactionManagement
  @Import({
      SecurityConfig.class,
      JwtService.class,
      ConsultaMorador.class,
      ResolvedorConsultaAgendamento.class,
      ListarAgendamentoService.class,
      BuscarProximaVisitaService.class,
      AgendamentoMoradorGatewayImpl.class,
      AgendamentoController.class,
      AgendamentoFiltroMapper.class,
      CalendarioResponseMapper.class,
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
    NamedParameterJdbcTemplate namedParameterJdbcTemplate(
        DataSource dataSource) {

      return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    PlatformTransactionManager transactionManager(
        DataSource dataSource) {

      return new DataSourceTransactionManager(dataSource);
    }
  }
}