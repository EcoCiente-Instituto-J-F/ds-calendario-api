// package br.com.ecociente.calendario.entrypoint.exception;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;

// import java.lang.reflect.Method;
// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.core.MethodParameter;
// import org.springframework.http.HttpInputMessage;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.converter.HttpMessageNotReadableException;
// import org.springframework.validation.BindingResult;
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// import br.com.ecociente.calendario.core.domain.StatusType;
// import br.com.ecociente.calendario.core.exception.AgendamentoInvalidoException;
// import br.com.ecociente.calendario.core.exception.AgendamentoNaoEncontradoException;
// import br.com.ecociente.calendario.core.exception.CondominioNaoEncontradoException;
// import br.com.ecociente.calendario.core.exception.CooperativaNaoEncontradaException;
// import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
// import br.com.ecociente.calendario.entrypoint.dto.ErrorResponse;

// public class GlobalExceptionHandlerTest {

//   private GlobalExceptionHandler globalExceptionHandler;

//   @BeforeEach
//   public void setUp() {
//     globalExceptionHandler = new GlobalExceptionHandler();
//   }

//   @Test
//   @DisplayName("Deve retornar 403 quando ocorrer PerfilNaoAutorizadoException")
//   void shouldReturnPerfilNaoAutorizadoExceptionWhenProfileNotAuthorized(){
//     PerfilNaoAutorizadoException exception = new PerfilNaoAutorizadoException("ADMIN");

//     ResponseEntity<ErrorResponse> response =
//      globalExceptionHandler.handlePerfilNaoAutorizadoException(exception, null);

//      assertNotNull(response.getBody());
//      assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//      assertEquals(403, response.getBody().status());
//      assertEquals("PERFIL_NAO_AUTORIZADO", response.getBody().codigoError());
//      assertEquals("perfil", response.getBody().details().get(0).field());
//      assertEquals("Perfil não autorizado: ADMIN", response.getBody().details().get(0).message());
//   }

//   @Test
//   @DisplayName("Deve retornar 404 quando ocorrer AgendamentoNaoEncontradoException")
//   void shouldReturn404WhenAgendamentoNaoEncontradoException() {
//     AgendamentoNaoEncontradoException exception = new AgendamentoNaoEncontradoException(999);

//     ResponseEntity<ErrorResponse> response =
//      globalExceptionHandler.handleAgendamentoNaoEncontradoException(exception, null);

//      assertNotNull(response.getBody());
//      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//      assertEquals(404, response.getBody().status());
//      assertEquals("AGENDAMENTO_NAO_ENCONTRADO", response.getBody().codigoError());
//      assertEquals("id", response.getBody().details().get(0).field());
//      assertEquals("Agendamento não encontrado: 999", response.getBody().details().get(0).message());

//   }

//   @Test
//   @DisplayName("Deve retornar 404 quando ocorrer CondominioNaoEncontradoException")
//   void shouldReturn404WhenCondominioNaoEncontradoException() {
//     CondominioNaoEncontradoException exception = new CondominioNaoEncontradoException(999);

//     ResponseEntity<ErrorResponse> response =
//      globalExceptionHandler.handleCondominioNaoEncontradoException(exception, null);

//      assertNotNull(response.getBody());
//      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//      assertEquals(404, response.getBody().status());
//      assertEquals("CONDOMINIO_NAO_ENCONTRADO", response.getBody().codigoError());
//      assertEquals("condominioId", response.getBody().details().get(0).field());
//      assertEquals("Condomínio não encontrado: 999", response.getBody().details().get(0).message());
//   }

//   @Test
//   @DisplayName("Deve retornar 404 quando ocorrer CooperativaNaoEncontradoException")
//   void shouldReturn404WhenCooperativaNaoEncontradoException() {
//     CooperativaNaoEncontradaException exception = new CooperativaNaoEncontradaException(999);

//     ResponseEntity<ErrorResponse> response =
//      globalExceptionHandler.handleCooperativaNaoEncontradaException(exception, null);

//      assertNotNull(response.getBody());
//      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//      assertEquals(404, response.getBody().status());
//      assertEquals("COOPERATIVA_NAO_ENCONTRADA", response.getBody().codigoError());
//      assertEquals("cooperativaId", response.getBody().details().get(0).field());
//      assertEquals("Cooperativa não encontrada: 999", response.getBody().details().get(0).message());
//   }

//   @Test
//   @DisplayName("Deve retornar 400 quando ocorrer AgendamentoInvalidoException")
//   void shouldReturn400WhenAgendamentoInvalidoException() {
//     AgendamentoInvalidoException exception = new AgendamentoInvalidoException("Data de inicio no passado");

//     ResponseEntity<ErrorResponse> response =
//       globalExceptionHandler.handleAgendamentoInvalidoException(exception, null);
    
//     assertNotNull(response.getBody());
//     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//     assertEquals(400, response.getBody().status());
//     assertEquals("AGENDAMENTO_INVALIDO", response.getBody().codigoError());
//     assertEquals("agendamentoId", response.getBody().details().get(0).field());
//     assertEquals("Data de inicio no passado", response.getBody().details().get(0).message());
//   }

//   @Test
//   @DisplayName("Deve retornar 400 quando MethodArgumentTypeMismatchException")
//   void shouldReturn400WhenMethodArgumentTypeMismatchException() throws Exception {
//     Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class);
//     MethodParameter parameter = new MethodParameter(method, 0);
//     MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException("INVALIDO", StatusType.class, "status", parameter, null);

//     ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatch(exception, null);

//     assertNotNull(response.getBody());
//     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//     assertEquals(400, response.getBody().status());
//     assertEquals("PARAMETRO_INVALIDO", response.getBody().codigoError());
//     assertEquals("status", response.getBody().details().get(0).field());
//   }

//   @Test
//   @DisplayName("Deve retornar 400 com lista de erros quando ocorrer MethodArgumentNotValidException")
//   void shouldReturn400WithFieldErrorsWhenMethodArgumentNotValidException() throws Exception{
//     Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class);
//     MethodParameter parameter = new MethodParameter(method, 0);
//     BindingResult bindingResult = mock(BindingResult.class);
//     when(bindingResult.getFieldErrors()).thenReturn(List.of(
//       new FieldError("agendamento", "dataInicio","não pode ser nula"),
//       new FieldError("agendamento", "condominioId","é obrigatório")
//     ));

//     MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter,bindingResult);
//     ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception, null);

//     assertNotNull(response.getBody());
//     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//     assertEquals(400, response.getBody().status());
//     assertEquals("ERRO_VALIDACAO", response.getBody().codigoError());
//     assertEquals(2, response.getBody().details().size());
//     assertEquals("dataInicio", response.getBody().details().get(0).field());
//     assertEquals("não pode ser nula", response.getBody().details().get(0).message());
//     assertEquals("condominioId", response.getBody().details().get(1).field());
//   }

//   @Test
//   @DisplayName("Deve retornar 400 quando ocorrer HttpMessageNotReadableException")
//   void shouldReturn400WhenHttpMessageNotReadableException(){
//     HttpMessageNotReadableException exception = new HttpMessageNotReadableException("JSON malformada",(HttpInputMessage) null);
//     ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(exception, null);

//     assertNotNull(response.getBody());
//     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//     assertEquals(400, response.getBody().status());
//     assertEquals("JSON_MALFORMADO", response.getBody().codigoError());
//     assertEquals("body", response.getBody().details().get(0).field());
//     assertEquals("JSON malformado ou impossível de ler", response.getBody().details().get(0).message());
//   }

//   @Test
//   @DisplayName("Deve retornar 500 e mensagem genérica quando ocorrer Exception não mapeada")
//   void shouldReturn500AndGenericMessageWhenUnmappedException(){
//     RuntimeException exception = new RuntimeException("Error interno do banco de dados");
//     ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, null);
//     assertNotNull(response.getBody());
//     assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//     assertEquals(500, response.getBody().status());
//     assertEquals("ERRO_INTERNO", response.getBody().codigoError());
//     assertEquals("erro", response.getBody().details().get(0).field());
//     assertEquals("Erro interno do servidor", response.getBody().details().get(0).message()); 
//   }

//   @Test
//   @DisplayName("Não deve vazar a mensagem interna da exception no fallback genérico")
//   void shouldNotLeakInternalMessageInGenericFallback() {
//     RuntimeException exception = new RuntimeException("Detalhe sensível do banco de dados");
//     ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, null);
//     assertNotNull(response.getBody());
//     String mensagenRetornada = response.getBody().details().get(0).message();
//     assertEquals("Erro interno do servidor", mensagenRetornada);
//     assertEquals(false,mensagenRetornada.contains("Detalhe sensível")); 
//   }

//   private void dummyMethod(String param){}

  
// }
