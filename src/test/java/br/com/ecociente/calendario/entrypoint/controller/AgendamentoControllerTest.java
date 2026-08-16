// package br.com.ecociente.calendario.entrypoint.controller;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doReturn;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.http.HttpHeaders;
// import org.springframework.test.web.servlet.MockMvc;

// import br.com.ecociente.calendario.config.security.JwtService;
// import br.com.ecociente.calendario.config.security.JwtUsuario;
// import br.com.ecociente.calendario.config.security.SecurityConfig;
// import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
// import br.com.ecociente.calendario.core.domain.StatusType;
// import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
// import br.com.ecociente.calendario.core.usecase.BuscarProximaVisitaUseCase;
// import br.com.ecociente.calendario.core.usecase.ListarAgendamentosUseCase;
// import br.com.ecociente.calendario.entrypoint.exception.GlobalExceptionHandler;
// import br.com.ecociente.calendario.entrypoint.mapper.CalendarioResponseMapper;

// @WebMvcTest(controllers = AgendamentoController.class)
// @Import({
//     SecurityConfig.class,
//     GlobalExceptionHandler.class,
//     CalendarioResponseMapper.class
// })
// class AgendamentoControllerTest {

//   @Autowired
//   private MockMvc mockMvc;

//   @MockBean
//   private ListarAgendamentosUseCase listarAgendamentosUseCase;

//   @MockBean
//   private BuscarProximaVisitaUseCase buscarProximaVisitaUseCase;

//   @MockBean
//   private JwtService jwtService;

//   private static final String TOKEN_SINDICO = "token-sindico";
//   private static final String TOKEN_COOPERATIVA = "token-cooperativa";

//   @BeforeEach
//   void setupSecurity() {
//     doReturn(new JwtUsuario(1, "SINDICO"))
//         .when(jwtService)
//         .validar(TOKEN_SINDICO);

//     doReturn(new JwtUsuario(2, "COOPERATIVA"))
//         .when(jwtService)
//         .validar(TOKEN_COOPERATIVA);
//   }

//   private AgendamentoColeta agendamento() {
//     return AgendamentoColeta.builder()
//         .id(1)
//         .condominioId(10)
//         .cooperativaId(20)
//         .dataInicio(LocalDateTime.of(2024, 1, 15, 10, 0))
//         .dataFim(LocalDateTime.of(2024, 1, 15, 12, 0))
//         .statusAgendamento(StatusType.AGENDADO)
//         .possuiRecorrencia(false)
//         .build();
//   }

//  @Nested
// @DisplayName("GET /api/v1/agendamentos")
// class ListarAgendamentos {

//   @Test
//   @DisplayName("Deve retornar 200 com página de agendamentos")
//   void shouldReturn200WithPageOfAgendamentos() throws Exception {
//     when(listarAgendamentosUseCase.executar(any(), any(), any(), any()))
//         .thenReturn(new PageImpl<>(List.of(agendamento())));

//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO)
//             .param("status", "AGENDADO")
//             .param("dataInicio", "2024-01-01T00:00:00")
//             .param("dataFim", "2024-12-31T23:59:59")
//             .param("possuiRecorrencia", "false")
//             .param("condominioId", "10")
//             .param("cooperativaId", "20"))
//         .andExpect(status().isOk())
//         .andExpect(jsonPath("$.content[0].id").value(1))
//         .andExpect(jsonPath("$.content[0].condominioId").value(10))
//         .andExpect(jsonPath("$.content[0].cooperativaId").value(20))
//         .andExpect(jsonPath("$.content[0].statusAgendamento").value("AGENDADO"))
//         .andExpect(jsonPath("$.content[0].possuiRecorrencia").value(false));
//   }

//   @Test
//   @DisplayName("Deve retornar 200 com página vazia quando não houver agendamentos")
//   void shouldReturn200WithEmptyPageWhenNoAgendamentos() throws Exception {
//     when(listarAgendamentosUseCase.executar(any(), any(), any(), any()))
//         .thenReturn(new PageImpl<>(List.of()));

//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO))
//         .andExpect(status().isOk())
//         .andExpect(jsonPath("$.content").isEmpty());
//   }

//   @Test
//   @DisplayName("Deve retornar 200 com filtros opcionais ausentes")
//   void shouldReturn200WithoutOptionalFilters() throws Exception {
//     when(listarAgendamentosUseCase.executar(any(), any(), any(), any()))
//         .thenReturn(new PageImpl<>(List.of()));

//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_COOPERATIVA))
//         .andExpect(status().isOk());
//   }

//   @Test
//   @DisplayName("Deve retornar 400 quando status for inválido")
//   void shouldReturn400WhenStatusIsInvalid() throws Exception {
//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO)
//             .param("status", "INVALIDO"))
//         .andExpect(status().isBadRequest())
//         .andExpect(jsonPath("$.status").value(400))
//         .andExpect(jsonPath("$.codigoError").value("PARAMETRO_INVALIDO"))
//         .andExpect(jsonPath("$.details[0].field").value("status"));
//   }

//   @Test
//   @DisplayName("Deve aceitar data no formato ISO 8601")
//   void shouldAcceptDateInISOFormat() throws Exception {
//     when(listarAgendamentosUseCase.executar(any(), any(), any(), any()))
//         .thenReturn(new PageImpl<>(List.of()));

//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO)
//             .param("dataInicio", "2024-01-01T10:00:00"))
//         .andExpect(status().isOk());
//   }

//   @Test
//   @DisplayName("Deve retornar 400 quando data estiver em formato inválido")
//   void shouldReturn400WhenDateFormatIsInvalid() throws Exception {
//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO)
//             .param("dataInicio", "01/01/2024"))
//         .andExpect(status().isBadRequest())
//         .andExpect(jsonPath("$.codigoError").value("PARAMETRO_INVALIDO"));
//   }

//   @Test
//   @DisplayName("Deve retornar 403 quando use case lançar PerfilNaoAutorizadoException")
//   void shouldReturn403WhenUseCaseThrowsPerfilNaoAutorizado() throws Exception {
//     when(listarAgendamentosUseCase.executar(any(), any(), any(), any()))
//         .thenThrow(new PerfilNaoAutorizadoException("ADMIN"));

//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO))
//         .andExpect(status().isForbidden())
//         .andExpect(jsonPath("$.status").value(403))
//         .andExpect(jsonPath("$.codigoError").value("PERFIL_NAO_AUTORIZADO"))
//         .andExpect(jsonPath("$.details[0].field").value("perfil"));
//   }

//   @Test
//   @DisplayName("Deve retornar 500 e não vazar mensagem interna em erro genérico")
//   void shouldReturn500AndNotLeakInternalMessageOnGenericError() throws Exception {
//     when(listarAgendamentosUseCase.executar(any(), any(), any(), any()))
//         .thenThrow(new RuntimeException("Erro interno do banco"));

//     mockMvc.perform(get("/api/v1/agendamentos")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO))
//         .andExpect(status().isInternalServerError())
//         .andExpect(jsonPath("$.status").value(500))
//         .andExpect(jsonPath("$.codigoError").value("ERRO_INTERNO"))
//         .andExpect(jsonPath("$.details[0].message").value("Erro interno do servidor"));
//   }
// }

// @Nested
// @DisplayName("GET /api/v1/agendamentos/proxima")
// class BuscarProximoAgendamento {

//   @Test
//   @DisplayName("Deve retornar 200 quando há próximo agendamento")
//   void shouldReturn200WhenNextAgendamentoExists() throws Exception {
//     AgendamentoColeta agendamento = AgendamentoColeta.builder()
//         .id(5)
//         .condominioId(10)
//         .cooperativaId(20)
//         .dataInicio(LocalDateTime.of(2024, 2, 1, 8, 0))
//         .statusAgendamento(StatusType.AGENDADO)
//         .possuiRecorrencia(true)
//         .build();

//     when(buscarProximaVisitaUseCase.executar(any(), any(), any()))
//         .thenReturn(Optional.of(agendamento));

//     mockMvc.perform(get("/api/v1/agendamentos/proxima")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO))
//         .andExpect(status().isOk())
//         .andExpect(jsonPath("$.id").value(5))
//         .andExpect(jsonPath("$.condominioId").value(10))
//         .andExpect(jsonPath("$.possuiRecorrencia").value(true));
//   }

//   @Test
//   @DisplayName("Deve retornar 404 quando não há próximo agendamento")
//   void shouldReturn404WhenNoNextAgendamento() throws Exception {
//     when(buscarProximaVisitaUseCase.executar(any(), any(), any()))
//         .thenReturn(Optional.empty());

//     mockMvc.perform(get("/api/v1/agendamentos/proxima")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO))
//         .andExpect(status().isNotFound());
//   }

//   @Test
//   @DisplayName("Deve retornar 403 quando use case lançar PerfilNaoAutorizadoException")
//   void shouldReturn403WhenUseCaseThrowsPerfilNaoAutorizado() throws Exception {
//     when(buscarProximaVisitaUseCase.executar(any(), any(), any()))
//         .thenThrow(new PerfilNaoAutorizadoException("ADMIN"));

//     mockMvc.perform(get("/api/v1/agendamentos/proxima")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO))
//         .andExpect(status().isForbidden())
//         .andExpect(jsonPath("$.codigoError").value("PERFIL_NAO_AUTORIZADO"));
//   }

//   @Test
//   @DisplayName("Deve aceitar filtros de data no formato ISO")
//   void shouldAcceptDateFiltersInISOFormat() throws Exception {
//     when(buscarProximaVisitaUseCase.executar(any(), any(), any()))
//         .thenReturn(Optional.empty());

//     mockMvc.perform(get("/api/v1/agendamentos/proxima")
//             .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN_SINDICO)
//             .param("dataInicio", "2024-01-01T00:00:00")
//             .param("dataFim", "2024-12-31T23:59:59"))
//         .andExpect(status().isNotFound());
//   }
// }
// }