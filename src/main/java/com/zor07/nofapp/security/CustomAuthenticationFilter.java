package com.zor07.nofapp.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.AuthenticationDto;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

  private final AuthenticationManager authenticationManager;
  private final ObjectMapper objectMapper;

  public CustomAuthenticationFilter(final AuthenticationManager authenticationManager,
      final ObjectMapper objectMapper) {
    this.authenticationManager = authenticationManager;
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
      final HttpServletResponse response) throws AuthenticationException {
    final var authData = getAuthDataFromRequest(request);
    final var authenticationToken = new UsernamePasswordAuthenticationToken(authData.username(), authData.password());
    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain chain,
      final Authentication authentication) throws IOException {
    final var user = (User) authentication.getPrincipal();
    final var algorithm = Algorithm.HMAC256("secret".getBytes());
    final var accessToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()))
        .withIssuer(request.getRequestURL().toString())
        .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .sign(algorithm);

    final var refreshToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + Duration.ofDays(10).toMillis()))
        .withIssuer(request.getRequestURL().toString())
        .sign(algorithm);
    final var tokens = new HashMap<String, String>();
    tokens.put("access_token", accessToken);
    tokens.put("refresh_token", refreshToken);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
  }

  private AuthenticationDto getAuthDataFromRequest(final HttpServletRequest request) {
    try {
      final var body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
      return objectMapper.readValue(body, AuthenticationDto.class);
    } catch (final IOException e) {
      LOGGER.error("Cannot get body from request", e);
      return new AuthenticationDto(null, null);
    }
  }
}
