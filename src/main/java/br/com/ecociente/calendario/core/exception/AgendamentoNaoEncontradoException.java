package br.com.ecociente.calendario.core.exception;

public class AgendamentoNaoEncontradoException extends RuntimeException {
  public AgendamentoNaoEncontradoException(Integer agendamentoId) {
    super("Agendamento não encontrado: " + agendamentoId);
  }
  
}
