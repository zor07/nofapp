package com.zor07.nofapp.api.v1;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.RoleToUserDto;
import com.zor07.nofapp.user.Role;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user")
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }

  @PostMapping("/user")
  public ResponseEntity<User> saveUser(@RequestBody User user) {
    final var uri = URI.create(ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/v1/user")
        .toUriString());
    return ResponseEntity.created(uri).body(userService.saveUser(user));
  }

  @PostMapping("/role")
  public ResponseEntity<Role> saveRole(@RequestBody Role role) {
    final var uri = URI.create(ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/v1/role")
        .toUriString());
    return ResponseEntity.created(uri).body(userService.saveRole(role));
  }

  @PostMapping("/role/addToUser")
  public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserDto dto) {
    userService.addRoleToUser(dto.username, dto.role);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        final var refreshToken = authorizationHeader.substring("Bearer ".length());
        final var algorithm = Algorithm.HMAC256("secret".getBytes());
        final var verifier = JWT.require(algorithm).build();
        final var decodedJWT = verifier.verify(refreshToken);
        final var username = decodedJWT.getSubject();
        final var user = userService.getUser(username);
        final var accessToken = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
            .sign(algorithm);
        final var tokens = new HashMap<String, String>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
      } catch (Exception e) {
        LOGGER.error("", e);
        response.setHeader("error", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        final var error = new HashMap<String, String>();
        error.put("error_message", e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
      }
    } else {
      throw new RuntimeException("Refresh token is missing");
    }
  }


}
