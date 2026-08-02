package br.com.ecociente.calendario.core.exception;

public class PerfilNaoAutorizadoException extends RuntimeException {
  private String perfil;

  public PerfilNaoAutorizadoException(String perfil) {
    super("Perfil não autorizado: " + perfil);
    this.perfil = perfil;
  }
}
