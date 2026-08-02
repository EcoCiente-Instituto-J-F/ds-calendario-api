package br.com.ecociente.calendario.entrypoint.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecociente.calendario.config.security.JwtUsuario;
import br.com.ecociente.calendario.core.usecase.BuscarProximaVisitaUseCase;
import br.com.ecociente.calendario.core.usecase.ListarAgendamentosUseCase;
import br.com.ecociente.calendario.entrypoint.dto.response.CalendarioResponseDto;
import br.com.ecociente.calendario.entrypoint.mapper.CalendarioResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/agendamentos")
@RequiredArgsConstructor
@Tag(name = "Agendamento", description = "API responsável pelo gerenciamento dos agendamentos de coleta.")
public class AgendamentoController {
  private final ListarAgendamentosUseCase listarAgendamentosUseCase;
  private final BuscarProximaVisitaUseCase buscarProximoAgendamentoUsecase;
  private final CalendarioResponseMapper calendarioResponseMapper;

  @GetMapping
  @Operation(
    summary = "Listar agendamentos",
    description = "Endpoint para listar os agendamentos de coleta de acordo com o perfil do usuário autenticado."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso."),
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou token inválido."),
    @ApiResponse(responseCode = "403", description = "Usuário não autorizado")
  })
  public ResponseEntity<Page<CalendarioResponseDto>> listarAgendamentos(@AuthenticationPrincipal JwtUsuario usuario,
    @PageableDefault(size = 10,sort ="dataInicio", direction = Sort.Direction.ASC) Pageable pageable){
      Page<CalendarioResponseDto> response = listarAgendamentosUseCase.executar(usuario.usuarioId(),usuario.perfil(), pageable)
      .map(calendarioResponseMapper :: toResponseDto);
      return ResponseEntity.ok(response);
  }

  @GetMapping("/proxima")
  @Operation(
    summary = "Buscar próximo agendamento",
    description = "Endpoint para buscar o próximo agendamento de coleta de acordo com o perfil do usuário autenticado."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Próximo agendamento retornado com sucesso."),
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou token inválido."),
    @ApiResponse(responseCode = "403", description = "Usuário não autorizado")
  })
  public ResponseEntity<CalendarioResponseDto> buscarProximoAgendamento(@AuthenticationPrincipal JwtUsuario usuario){
    return buscarProximoAgendamentoUsecase.executar(usuario.usuarioId(), usuario.perfil())
      .map(calendarioResponseMapper :: toResponseDto)
      .map(ResponseEntity :: ok)
      .orElse(ResponseEntity.notFound().build());
  } 
  
}
