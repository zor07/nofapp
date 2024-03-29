package com.zor07.nofapp.api.v1.controller;

import com.zor07.nofapp.api.v1.dto.auth.AuthenticationDto;
import com.zor07.nofapp.api.v1.dto.auth.TokensDto;
import com.zor07.nofapp.api.v1.dto.auth.UserInfoDto;
import com.zor07.nofapp.api.v1.dto.auth.UserRegisterDto;
import com.zor07.nofapp.api.v1.dto.auth.mapper.UserMapper;
import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.exception.IllegalAuthorizationHeaderException;
import com.zor07.nofapp.security.SecurityUtils;
import com.zor07.nofapp.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@Api( tags = "Auth" )
public class AuthController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  private final UserService userService;

  private final UserMapper userMapper;

  @Autowired
  public AuthController(final UserService userService,
                        final UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
  }


  @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Authenticates user in application", response = TokensDto.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successfully authenticated user"),
          @ApiResponse(code = 403, message = "Authentication failed with given payload")
  })
  public void  login(@RequestBody AuthenticationDto authenticationDto) {
    // handled via CustomAuthenticationFilter
  }

  @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Registers user in application", response = TokensDto.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successfully registered user"),
          @ApiResponse(code = 403, message = "Authentication failed with given payload")
  })
  public ResponseEntity<TokensDto> register(final HttpServletRequest request,
                       final HttpServletResponse response,
                       final @RequestBody UserRegisterDto userRegisterDto) {
    final var user = userMapper.toUser(userRegisterDto);
    final var savedUser = userService.createNewUser(user);
    final var accessToken = SecurityUtils.createAccessToken(
            savedUser.getUsername(),
            request.getRequestURL().toString(),
            savedUser.getRoles().stream().map(Role::getName).toList()
    );
    final var refreshToken = SecurityUtils.createRefreshToken(savedUser.getUsername(),
            request.getRequestURL().toString());

    return ResponseEntity.ok(new TokensDto(accessToken, refreshToken));
  }

  @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Retrieves information about current user", response = UserInfoDto.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successfully retrieves information about current user"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
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

  @GetMapping(path = "/token/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Updates users access token", response = TokensDto.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successfully updated access token"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  @ApiImplicitParam(name = "Authorization", value = "Refresh Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer refresh_token")
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
