package com.zor07.nofapp.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthorizationFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (request.getServletPath().equals("/api/login")) {
      filterChain.doFilter(request, response);
    } else {
      final var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        try {
          final var token = authorizationHeader.substring("Bearer ".length());
          final var algorithm = Algorithm.HMAC256("secret".getBytes());
          final var verifier = JWT.require(algorithm).build();
          final var decodedJWT = verifier.verify(token);
          final var username = decodedJWT.getSubject();
          final var roles = decodedJWT.getClaim("roles").asArray(String.class);
          final var authorities = new ArrayList<SimpleGrantedAuthority>();
          Stream.of(roles)
              .forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role));
              });
          final var authenticationToken =
              new UsernamePasswordAuthenticationToken(username, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          filterChain.doFilter(request, response);
        } catch (Exception e) {
          LOGGER.error("", e);
          response.setHeader("error", e.getMessage());
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//          response.sendError(HttpServletResponse.SC_FORBIDDEN);
          final var error = new HashMap<String, String>();
          error.put("error_message", e.getMessage());
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
      } else {
        filterChain.doFilter(request, response);
      }
    }

  }
}
