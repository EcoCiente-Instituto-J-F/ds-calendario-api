package br.com.ecociente.calendario.config.security;

import br.com.ecociente.calendario.core.domain.Perfil;

public record JwtUsuario (
  Integer usuarioId,
  Perfil perfil
) {
}
