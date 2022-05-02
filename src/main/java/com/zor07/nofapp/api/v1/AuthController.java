package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.auth.AuthenticationDto;
import com.zor07.nofapp.api.v1.dto.auth.TokensDto;
import com.zor07.nofapp.api.v1.dto.auth.UserInfoDto;
import com.zor07.nofapp.exception.IllegalAuthorizationHeaderException;
import com.zor07.nofapp.security.SecurityUtils;
import com.zor07.nofapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  private final UserService userService;

  @Autowired
  public AuthController(final UserService userService) {
    this.userService = userService;
  }


  @PostMapping("/login")
  public void  login(@RequestBody AuthenticationDto authenticationDto) {
    // handled via CustomAuthenticationFilter
  }


  @GetMapping("/me")
  public ResponseEntity<UserInfoDto> me(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        final var decodedJWT = SecurityUtils.decodeJWT(authorizationHeader);
        final var username = decodedJWT.getSubject();
        final var user = userService.getUser(username);
        return ResponseEntity.ok(new UserInfoDto(user.getId().toString(), user.getName(), user.getUsername()));
      } catch (final Exception e) {
        LOGGER.error("Got exception while authorizing request", e);
        throw new IllegalAuthorizationHeaderException(e.getMessage());
      }
    } else {
      throw new RuntimeException("Refresh token is missing");
    }
  }

  @GetMapping("/token/refresh")
  public ResponseEntity<TokensDto> refreshToken(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        final var decodedJWT = SecurityUtils.decodeJWT(authorizationHeader);
        final var username = decodedJWT.getSubject();
        final var user = userService.getUser(username);
        final var accessToken = SecurityUtils.createAccessToken(user, request.getRequestURL().toString());
        final var tokens = new TokensDto(accessToken, SecurityUtils.parseRefreshToken(authorizationHeader));
        return ResponseEntity.ok(tokens);
      } catch (final Exception e) {
        LOGGER.error("Got exception while authorizing request", e);
        throw new IllegalAuthorizationHeaderException(e.getMessage());
      }
    } else {
      throw new IllegalAuthorizationHeaderException("Refresh token is missing");
    }
  }
}
