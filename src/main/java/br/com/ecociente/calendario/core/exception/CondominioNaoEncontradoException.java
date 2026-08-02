package br.com.ecociente.calendario.core.exception;

public class CondominioNaoEncontradoException extends RuntimeException {
  public CondominioNaoEncontradoException(Integer condominioId) {
    super("Condomínio não encontrado: " + condominioId);
  }
}
