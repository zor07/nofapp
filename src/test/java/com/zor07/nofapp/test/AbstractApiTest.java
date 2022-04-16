package com.zor07.nofapp.test;

import java.util.ArrayList;

import com.zor07.nofapp.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.user.Role;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AbstractApiTest extends AbstractApplicationTest {

  protected static final String DEFAULT_ROLE = "ROLE_USER";
  protected static final String DEFAULT_USERNAME = "user";
  protected static final String DEFAULT_PASSWORD = "pass";

  protected static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

  protected static final String REFRESH_TOKEN_ENDPOINT = "/api/v1/auth/token/refresh";

  protected static record LoginPayload(String username, String password) {}

  protected static record TokensDto(String access_token, String refresh_token) {
  }

  protected static Role createRole() {
    return new Role(null, DEFAULT_ROLE);
  }

  protected static Role createAdminRole() {
    return new Role(null, UserRole.ROLE_ADMIN.getRoleName());
  }

  protected static User createUser(final String name) {
    return new User(null, name, name, DEFAULT_PASSWORD, new ArrayList<>());
  }

  @Autowired
  protected UserService userService;

  protected final ObjectMapper objectMapper = new ObjectMapper();

  protected String getAuthHeader(MockMvc mvc, String username) throws Exception {
    final var loginPayload = objectMapper.writeValueAsString(new LoginPayload(username, DEFAULT_PASSWORD));
    final var mvcResult = mvc.perform(post(LOGIN_ENDPOINT)
          .content(loginPayload)
          .contentType(MediaType.APPLICATION_JSON))
        .andReturn();
    final var tokens = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokensDto.class);
    return String.format("Bearer %s", tokens.access_token());
  }

}
