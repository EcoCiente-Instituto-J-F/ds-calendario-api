package br.com.ecociente.calendario.config.security;


public record JwtUsuario (
  Integer usuarioId,
  String perfil
) {
}
