package br.com.ecociente.calendario.core.exception;

public class PerfilNaoAutorizadoException extends RuntimeException {
  public PerfilNaoAutorizadoException(String perfil) {
    super("Perfil não autorizado: " + perfil);
  }
}
