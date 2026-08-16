package br.com.ecociente.calendario.core.domain;

import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;

public enum Perfil {
  SINDICO,
  COOPERATIVA;

  public static Perfil tipoPerfil(String perfil){
    if (perfil == null) {
      throw new PerfilNaoAutorizadoException(null);
      
    }
    try{
      return Perfil.valueOf(perfil.toUpperCase());
    }catch(IllegalArgumentException exception){
      throw new PerfilNaoAutorizadoException(perfil);
    }
  }
}
