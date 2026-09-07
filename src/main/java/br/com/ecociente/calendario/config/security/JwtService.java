package br.com.ecociente.calendario.config.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.ecociente.calendario.core.domain.Perfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final SecretKey key;

  public JwtService(@Value("${app.security.jwt.secret}") String secret) {
    if (secret == null || secret.isBlank()){
      throw new IllegalArgumentException(
        "JWT_SECRET n~zo pode ser vazio"
      );
    }

    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public JwtUsuario validar(String token) {
    Claims claims = Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload();
      
    Integer usuarioId = extrairUsuarioId(claims);
    String perfil = claims.get("perfil", String.class);
    
    if (usuarioId == null || usuarioId <= 0 || perfil == null) {
      throw new IllegalArgumentException("Claims obrigatórios não encontrados no token JWT");
    }
    return new JwtUsuario(usuarioId, Perfil.tipoPerfil(perfil));
  }

  private Integer extrairUsuarioId(Claims claims){
    Object usuarioId = claims.get("usuarioId");
    if (!(usuarioId instanceof Number number)) {
      return null;
    }

    return number.intValue();
  }
}
