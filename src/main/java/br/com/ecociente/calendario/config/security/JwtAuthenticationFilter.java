package br.com.ecociente.calendario.config.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String authorization = request.getHeader(
        HttpHeaders.AUTHORIZATION);

    if (authorization == null
        || !authorization.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      JwtUsuario usuario = jwtService.validar(
          authorization.substring(7));

      var authorities = List.of(
          new SimpleGrantedAuthority(
              "ROLE_" + usuario.perfil().name()));

      var authentication = new UsernamePasswordAuthenticationToken(
          usuario,
          null,
          authorities);

      SecurityContextHolder.getContext()
          .setAuthentication(authentication);

    } catch (PerfilNaoAutorizadoException exception) {
      SecurityContextHolder.clearContext();

      response.sendError(
          HttpServletResponse.SC_FORBIDDEN,
          "Perfil não autorizado");
      return;

    } catch (JwtException | IllegalArgumentException exception) {
      SecurityContextHolder.clearContext();

      response.sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          "Token inválido ou expirado");
      return;
    }

    filterChain.doFilter(request, response);
  }
}