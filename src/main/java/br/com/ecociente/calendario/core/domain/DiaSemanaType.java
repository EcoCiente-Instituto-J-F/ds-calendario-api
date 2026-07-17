package br.com.ecociente.calendario.core.domain;

public enum DiaSemanaType {
  SEGUNDA("Segunda-feira"),
  TERCA("Terça-feira"),
  QUARTA("Quarta-feira"),
  QUINTA("Quinta-feira"),
  SEXTA("Sexta-feira"),
  SABADO("Sábado"),
  DOMINGO("Domingo");

   private final String nome;

    DiaSemanaType(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

}
