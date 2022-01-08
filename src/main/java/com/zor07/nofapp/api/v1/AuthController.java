package com.zor07.nofapp.api.v1;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.security.SecurityUtils;
import com.zor07.nofapp.user.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  private final UserService userService;

  @Autowired
  public AuthController(final UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public void me(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        final var decodedJWT = SecurityUtils.decodeJWT(authorizationHeader);
        final var username = decodedJWT.getSubject();
        final var user = userService.getUser(username);
        final var responseData = new HashMap<String, String>();
        responseData.put("id", user.getId().toString());
        responseData.put("name", user.getName());
        responseData.put("username", user.getUsername());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseData);
      } catch (final Exception e) {
        LOGGER.error("Got exception while authorizing request", e);
        SecurityUtils.addErrorToResponse(response, e.getMessage());
      }
    } else {
      throw new RuntimeException("Refresh token is missing");
    }
  }

  @GetMapping("/token/refresh")
  public void refreshToken(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        final var decodedJWT = SecurityUtils.decodeJWT(authorizationHeader);
        final var username = decodedJWT.getSubject();
        final var user = userService.getUser(username);
        final var accessToken = SecurityUtils.createAccessToken(user, request.getRequestURL().toString());
        final var tokens = new HashMap<String, String>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", SecurityUtils.parseRefreshToken(authorizationHeader));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
      } catch (final Exception e) {
        LOGGER.error("Got exception while authorizing request", e);
        SecurityUtils.addErrorToResponse(response, e.getMessage());
      }
    } else {
      throw new RuntimeException("Refresh token is missing");
    }
  }



}
