package br.com.ecociente.calendario.core.domain;

import java.util.Locale;

import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;

public enum Perfil {

  SINDICO,
  COOPERATIVA,
  MORADOR;

  public static Perfil tipoPerfil(String perfil) {
    if (perfil == null || perfil.isBlank()) {
      throw new PerfilNaoAutorizadoException(perfil);
    }

    try {
      return Perfil.valueOf(
          perfil.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new PerfilNaoAutorizadoException(perfil);
    }
  }
}