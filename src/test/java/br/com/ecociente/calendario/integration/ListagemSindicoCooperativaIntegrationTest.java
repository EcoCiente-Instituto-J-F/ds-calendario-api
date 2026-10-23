package br.com.ecociente.calendario.integration;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * A listagem do síndico e da cooperativa passa pelo JPA (consulta nativa mapeada para a
 * entity, com ddl-auto=validate). Este teste sobe a aplicação inteira contra um Postgres de
 * verdade para provar que o mapeamento e os nomes em lote funcionam.
 */
@Testcontainers
@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
class ListagemSindicoCooperativaIntegrationTest {

  private static final String SECRET = "0123456789abcdef0123456789abcdef";

  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
      .withInitScript("sql/ecociente-schema.sql");

  @DynamicPropertySource
  static void propriedades(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("app.security.jwt.secret", () -> SECRET);
  }

  @Autowired
  WebApplicationContext context;

  @Autowired
  DataSource dataSource;

  @Autowired
  JdbcTemplate jdbc;

  private MockMvc mvc;

  @BeforeEach
  void preparar() throws Exception {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {

      statement.execute("DROP SCHEMA public CASCADE");
      statement.execute("CREATE SCHEMA public");
      statement.execute(recurso("sql/ecociente-schema.sql"));
      statement.execute(recurso("sql/morador-calendario-dados-test.sql"));
    }

    OffsetDateTime inicio = OffsetDateTime.now()
        .plusDays(10)
        .truncatedTo(ChronoUnit.MINUTES);

    // status 1 = agendado e 2 = confirmado, como no teste do morador
    criarAgendamento(100, 1, 1, inicio, 20, "COOPERATIVA");
    criarAgendamento(101, 1, 2, inicio.plusHours(2), 10, "CONDOMINIO");

    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  void sindicoDeveVerOrigemSolicitanteENomes() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(10, "SINDICO")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].id").value(100))
        .andExpect(jsonPath("$.content[0].origemSolicitacao").value("COOPERATIVA"))
        .andExpect(jsonPath("$.content[0].solicitadoPorUsuarioId").value(20))
        .andExpect(jsonPath("$.content[0].cooperativaNome").value("Cooperativa Um"))
        .andExpect(jsonPath("$.content[0].condominioNome").value("Condomínio Um"))
        .andExpect(jsonPath("$.content[1].origemSolicitacao").value("CONDOMINIO"))
        .andExpect(jsonPath("$.content[1].solicitadoPorUsuarioId").value(10))
        .andExpect(jsonPath("$.content[1].cooperativaNome").value("Cooperativa Um"));
  }

  @Test
  void cooperativaDeveVerOsMesmosCampos() throws Exception {
    mvc.perform(get("/api/v1/agendamentos")
        .header("Authorization", bearer(20, "COOPERATIVA")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].cooperativaNome").value("Cooperativa Um"))
        .andExpect(jsonPath("$.content[0].condominioNome").value("Condomínio Um"))
        .andExpect(jsonPath("$.content[0].origemSolicitacao").value("COOPERATIVA"));
  }

  @Test
  void proximoDaCooperativaTambemDeveTrazerNomes() throws Exception {
    mvc.perform(get("/api/v1/agendamentos/proxima")
        .header("Authorization", bearer(20, "COOPERATIVA")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(100))
        .andExpect(jsonPath("$.cooperativaNome").value("Cooperativa Um"))
        .andExpect(jsonPath("$.condominioNome").value("Condomínio Um"));
  }

  private void criarAgendamento(
      int id,
      int condominio,
      int status,
      OffsetDateTime data,
      int solicitante,
      String origem) {

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
        VALUES (?, ?, 1, ?, ?, ?, FALSE, ?, ?)
        """,
        id,
        condominio,
        status,
        data,
        data.plusHours(1),
        solicitante,
        origem);
  }

  private String bearer(int usuario, String perfil) {
    String token = Jwts.builder()
        .subject("usuario-" + usuario + "@teste.com")
        .claim("usuarioId", usuario)
        .claim("perfil", perfil)
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
}
