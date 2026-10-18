package br.com.ecociente.calendario.core.exception;

public class OperacaoColetaNaoPermitidaException
    extends RuntimeException {

  public OperacaoColetaNaoPermitidaException(String mensagem) {
    super(mensagem);
  }
}