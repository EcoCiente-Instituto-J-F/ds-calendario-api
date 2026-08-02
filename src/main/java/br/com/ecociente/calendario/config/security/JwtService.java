package br.com.ecociente.calendario.config.security;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final byte[] secret;

  public JwtService(@Value("${app.security.jwt.secret}") String secret) {
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
  }

  public JwtUsuario validar(String token) {
    Claims claims = Jwts.parser()
      .verifyWith(Keys.hmacShaKeyFor(secret))
      .build()
      .parseSignedClaims(token)
      .getPayload();
    Integer usuarioId = claims.get("usuarioId", Integer.class);
    String perfil = claims.get("perfil", String.class);
    
    if (usuarioId == null || perfil == null) {
      throw new IllegalArgumentException("Claims obrigatórios não encontrados no token JWT");
    }
    return new JwtUsuario(usuarioId, perfil);
  }
}
