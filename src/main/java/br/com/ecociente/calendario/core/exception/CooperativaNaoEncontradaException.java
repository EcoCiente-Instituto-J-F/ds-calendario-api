package br.com.ecociente.calendario.core.exception;

public class CooperativaNaoEncontradaException extends RuntimeException {
  public CooperativaNaoEncontradaException(Integer cooperativaId) {
    super("Cooperativa não encontrada: " + cooperativaId);
  } 
}
