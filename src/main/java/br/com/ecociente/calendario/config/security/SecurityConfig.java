package br.com.ecociente.calendario.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .requestCache(cache -> cache.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/api-docs",
                                "/api-docs/**")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/agendamentos",
                                "/api/v1/agendamentos/**")
                        .hasAnyRole("SINDICO", "COOPERATIVA")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/agendamentos",
                                "/api/v1/agendamentos/**")
                        .hasAnyRole("SINDICO", "COOPERATIVA")

                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "Autenticação necessária"))
                        .accessDeniedHandler((request, response, exception) -> response.sendError(
                                HttpServletResponse.SC_FORBIDDEN,
                                "Acesso negado")))
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}