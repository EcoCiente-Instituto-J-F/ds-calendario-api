package br.com.ecociente.calendario.entrypoint.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import br.com.ecociente.calendario.config.security.JwtUsuario;
import br.com.ecociente.calendario.core.service.ColetaService;
import br.com.ecociente.calendario.entrypoint.dto.request.AtualizarStatusColetaRequestDto;
import br.com.ecociente.calendario.entrypoint.dto.request.CriarColetaRequestDto;
import br.com.ecociente.calendario.entrypoint.dto.response.ColetaResponseDto;
import br.com.ecociente.calendario.entrypoint.mapper.ColetaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agendamentos")
public class ColetaController {

  private final ColetaService service;
  private final ColetaMapper mapper;

  @PostMapping
  public ResponseEntity<ColetaResponseDto> criar(
      @AuthenticationPrincipal JwtUsuario usuario,
      @Valid @RequestBody CriarColetaRequestDto request) {

    var coleta = service.criar(
        usuario.usuarioId(),
        mapper.toDomain(request));

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(mapper.toResponse(coleta));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ColetaResponseDto> atualizarStatus(
      @AuthenticationPrincipal JwtUsuario usuario,
      @PathVariable("id") Integer coletaId,
      @Valid @RequestBody AtualizarStatusColetaRequestDto request) {

    var coleta = service.atualizarStatus(
        usuario.usuarioId(),
        coletaId,
        request.status());

    return ResponseEntity.ok(mapper.toResponse(coleta));
  }
}