package com.zor07.nofapp.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.user.Role;

public class SecurityUtils {

  private static Algorithm getAlgorithm() {
    return Algorithm.HMAC256("secret".getBytes());
  }

  private static String createAccessToken(final String username, final String issuer, final List<String> claims) {
    return JWT.create()
        .withSubject(username)
        .withExpiresAt(new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()))
        .withIssuer(issuer)
        .withClaim("roles", claims)
        .sign(getAlgorithm());
  }

  public static String createRefreshToken(final User user, final String issuer) {
    return JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + Duration.ofDays(10).toMillis()))
        .withIssuer(issuer)
        .sign(getAlgorithm());
  }

  public static String createAccessToken(final com.zor07.nofapp.user.User user, final String issuer) {
    return createAccessToken(user.getUsername(), issuer,
        user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
  }

  public static String createAccessToken(final User user, final String issuer) {
    return createAccessToken(user.getUsername(), issuer,
      user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
  }

  public static String parseRefreshToken(final String authorizationHeader) {
    return authorizationHeader.substring("Bearer ".length());
  }

  public static DecodedJWT decodeJWT(final String authorizationHeader) {
    final var refreshToken = parseRefreshToken(authorizationHeader);
    final var algorithm = getAlgorithm();
    final var verifier = JWT.require(algorithm).build();
    return verifier.verify(refreshToken);
  }

  public static void addErrorToResponse(final HttpServletResponse response,
      final String errorMessage) throws IOException {
    final var error = new HashMap<String, String>();
    error.put("error_message", errorMessage);
    response.setHeader("error", errorMessage);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), error);
  }



}
