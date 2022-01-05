package com.zor07.nofapp.api.v1;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.user.Role;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerTest extends AbstractApplicationTest {

  private static record TokensDto(String access_token, String refresh_token) {}

  private static final String LOGIN = "/api/v1/auth/login";
  private static final String REFRESH_TOKEN = "/api/v1/auth/token/refresh";
  private static final String USERS = "/api/v1/user";

  private static Role createRole(String name) {
    return new Role(null, name);
  }

  private static User createUser(String name) {
    return new User(null, name, name, "pass", new ArrayList<>());
  }

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserService userService;
  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @BeforeClass
  public void setup() {
    userService.saveUser(createUser("user"));
    userService.saveRole(createRole("ROLE_USER"));
    userService.addRoleToUser("user", "ROLE_USER");
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  void login_isOk_test() throws Exception {
    final var payload = """
        {
          "username": "user",
          "password": "pass"
        }
        """;

    mvc.perform(post(LOGIN).servletPath(LOGIN).content(payload).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void login_isUnauthorized_test() throws Exception {
    final var payload = """
        {
          "username": "wrong_user",
          "password": "pass"
        }
        """;

    mvc.perform(post(LOGIN).servletPath(LOGIN).content(payload).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getUsers_isForbidden_test() throws Exception {
    mvc.perform(get(USERS).servletPath(USERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void getUsers_isOK_test() throws Exception {
    final var payload = """
        {
          "username": "user",
          "password": "pass"
        }
        """;

    mvc.perform(post(LOGIN).content(payload).contentType(MediaType.APPLICATION_JSON))
        .andDo(mvcResult -> {
          final var tokens = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokensDto.class);
          final var authHeader = String.format("Bearer %s", tokens.access_token);
          mvc.perform(get(USERS).servletPath(USERS).header(HttpHeaders.AUTHORIZATION, authHeader).contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
        });
  }

  @Test
  void refreshToken_isOK_test() throws Exception {
    final var payload = """
        {
          "username": "user",
          "password": "pass"
        }
        """;

    mvc.perform(post(LOGIN).servletPath(LOGIN).content(payload).contentType(MediaType.APPLICATION_JSON))
        .andDo(loginResult -> {
          final var tokens = objectMapper.readValue(loginResult.getResponse().getContentAsString(), TokensDto.class);
          final var authHeader = String.format("Bearer %s", tokens.refresh_token);
          final var refreshTokenResult =
              mvc.perform(get(REFRESH_TOKEN)
                  .servletPath(REFRESH_TOKEN)
                  .header(HttpHeaders.AUTHORIZATION, authHeader)
                  .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

          final var newTokens = objectMapper.readValue(refreshTokenResult.getResponse().getContentAsString(), TokensDto.class);
          assertThat(newTokens.refresh_token).isEqualTo(tokens.refresh_token);
          assertThat(newTokens.access_token).isNotEqualTo(tokens.access_token);
        });
  }


}
