package com.zor07.nofapp.security;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecurityUtils {

  public static Algorithm getAlgorithm() {
    return Algorithm.HMAC256("secret".getBytes());
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

  public static void addErrorToResponse(final HttpServletResponse response, final String errorMessage) throws IOException {
    final var error = new HashMap<String, String>();
    error.put("error_message", errorMessage);
    response.setHeader("error", errorMessage);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), error);
  }

}
