package com.zor07.nofapp.api.v1;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.test.AbstractAuthRelatedApplicationTest;
import com.zor07.nofapp.timer.Timer;
import com.zor07.nofapp.timer.TimerRepository;
import com.zor07.nofapp.user.RoleRepository;
import com.zor07.nofapp.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TimerControllerTest extends AbstractAuthRelatedApplicationTest {

  private static final String DATETIME = "2021-12-31T00:00:00";
  private static final String DATE = "2021-12-31";
  private static final String USER_1 = "user1";
  private static final String USER_2 = "user2";

  private static final String TIMER_ENDPOINT = "/api/v1/timer";
  private static class TimerTestDto {
    public Long id;
    public String start;
    public String stop;
    public String description;
  }

  private static final String TIMER_PAYLOAD = """
      {
        "start": "2021-12-31T00:00:00"
        "description": "test"
      }
      """;

  private void createTimer(final String username) {
    final var user = userService.getUser(username);
    final var start = LocalDate.parse(DATE).atStartOfDay(ZoneId.systemDefault()).toInstant();
    final var timer = new Timer(null, user, start, null, "test");
    timerRepository.save(timer);
  }

  @Autowired
  private TimerRepository timerRepository;
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;

  private MockMvc mvc;

  private void clearDb() {
    timerRepository.deleteAll();
    userRepository.deleteAll();
    roleRepository.deleteAll();
  }

  @BeforeMethod
  public void setup() {
    clearDb();
    userService.saveUser(createUser(USER_1));
    userService.saveUser(createUser(USER_2));
    userService.saveRole(createRole());
    userService.addRoleToUser(USER_1, DEFAULT_ROLE);
    userService.addRoleToUser(USER_2, DEFAULT_ROLE);
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @AfterClass
  void teardown() {
    clearDb();
  }

  @Test
  void getTimersTest() throws Exception {
    createTimer(USER_1);
    createTimer(USER_1);
    createTimer(USER_2);
    final var authHeader = getAuthHeader(mvc, USER_1);

    final var content = mvc.perform(get(TIMER_ENDPOINT)
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var timers = objectMapper.readValue(content, new TypeReference<List<TimerTestDto>>(){});
    assertThat(timers).hasSize(2);

    assertThat(timers.get(0).start).isEqualTo(DATETIME);
  }

  @Test
  void postTimerTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var userId = userService.getUser(USER_1).getId();
    final var timerTestDto = new TimerTestDto();
    final var description = "test description";
    timerTestDto.description = description;
    timerTestDto.start = DATETIME;
    mvc.perform(post(TIMER_ENDPOINT)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(timerTestDto))
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isCreated());
    final var timer = timerRepository.findAll().get(0);
    assertThat(timer.getDescription()).isEqualTo(description);
    assertThat(timer.getUser().getId()).isEqualTo(userId);
  }
}