package com.zor07.nofapp.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.auth.TokensDto;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.zor07.nofapp.test.UserTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AbstractApiTest extends AbstractApplicationTest {

  protected static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

  protected static final String REFRESH_TOKEN_ENDPOINT = "/api/v1/auth/token/refresh";

  private record LoginPayload(String username, String password) {}

  @Autowired
  protected UserService userService;
  @Autowired
  protected UserRepository userRepository;
  @Autowired
  protected RoleRepository roleRepository;

  protected final ObjectMapper objectMapper = new ObjectMapper();
  {
    objectMapper.findAndRegisterModules();
  }

  protected void createDefaultUser() {
    userService.saveUser(createUser());
    userService.saveRole(createRole());
    userService.addRoleToUser(DEFAULT_USERNAME, DEFAULT_ROLE);
  }

  protected String getAuthHeader(MockMvc mvc, String username) throws Exception {
    final var loginPayload = objectMapper.writeValueAsString(new LoginPayload(username, DEFAULT_PASSWORD));
    final var mvcResult = mvc.perform(post(LOGIN_ENDPOINT)
          .content(loginPayload)
          .contentType(MediaType.APPLICATION_JSON))
        .andReturn();
    final var tokens = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokensDto.class);
    return String.format("Bearer %s", tokens.accessToken());
  }

}
