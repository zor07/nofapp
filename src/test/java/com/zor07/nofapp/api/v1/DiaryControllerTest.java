package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.zor07.nofapp.entity.Diary;
import com.zor07.nofapp.repository.DiaryRepository;
import com.zor07.nofapp.test.AbstractApiTest;
import com.zor07.nofapp.repository.RoleRepository;
import com.zor07.nofapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Deprecated //soon will be replaced with Note
public class DiaryControllerTest extends AbstractApiTest {

  private static final String USER_1 = "user1";
  private static final String USER_2 = "user2";
  private static final String TITLE = "test title";
  private static final String DATA = "{\"data\":\"data\"}";

  private static final String ENDPOINT = "/api/v1/diary";
  private static class DiaryTestDto {
    public Long id;
    public String title;
    public JsonNode data;
  }

  private void createDiary(final String username) {
    final var user = userService.getUser(username);
    final var diary = new Diary(null, user, TITLE, DATA);
    diaryRepository.save(diary);
  }

  @Autowired
  private DiaryRepository diaryRepository;
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;

  private MockMvc mvc;

  private void clearDb() {
    diaryRepository.deleteAll();
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
  void findAllTest() throws Exception {
    createDiary(USER_1);
    createDiary(USER_1);
    createDiary(USER_2);
    final var authHeader = getAuthHeader(mvc, USER_1);

    final var content = mvc.perform(get(ENDPOINT)
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var diaries = objectMapper.readValue(content, new TypeReference<List<DiaryTestDto>>(){});
    assertThat(diaries).hasSize(2);
    assertThat(diaries.get(0).id).isNotNull();
    assertThat(diaries.get(0).data.isNull()).isTrue();
    assertThat(diaries.get(0).title).isEqualTo(TITLE);
    assertThat(diaries.get(1).id).isNotNull();
    assertThat(diaries.get(1).data.isNull()).isTrue();
    assertThat(diaries.get(1).title).isEqualTo(TITLE);
  }

  @Test
  void findByIdTest() throws Exception {
    createDiary(USER_1);
    final var userId = userRepository.findByUsername(USER_1).getId();
    final var diaryId = diaryRepository.findAllByUserId(userId).get(0).getId();
    final var authHeader = getAuthHeader(mvc, USER_1);

    final var content = mvc.perform(get(ENDPOINT+"/"+diaryId)
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var diary = objectMapper.readValue(content, DiaryTestDto.class);
    assertThat(diary.id).isEqualTo(diaryId);
    assertThat(diary.title).isEqualTo(TITLE);
    assertThat(diary.data.toString()).isEqualTo(DATA);
  }

  @Test
  void saveTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var diaryTestDto = new DiaryTestDto();
    diaryTestDto.title = TITLE;
    diaryTestDto.data = objectMapper.readTree(DATA);
    mvc.perform(post(ENDPOINT)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(diaryTestDto))
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isCreated());
    final var diary = diaryRepository.findAll().get(0);
    assertThat(diary.getTitle()).isEqualTo(TITLE);
    assertThat(diary.getData()).isEqualTo(DATA);
  }

  @Test
  void updateTest() throws Exception {
    createDiary(USER_1);
    final var id = diaryRepository.findAll().get(0).getId();
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var newTitle = "new Title";
    final var newData = "{\"data\":\"new Data\"}";
    final var diaryTestDto = new DiaryTestDto();
    diaryTestDto.id = id;
    diaryTestDto.title = newTitle;
    diaryTestDto.data = objectMapper.readTree(newData);
    mvc.perform(post(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(diaryTestDto))
        .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isCreated());
    final var all = diaryRepository.findAll();
    assertThat(all).hasSize(1);
    final var diary = all.get(0);
    assertThat(diary.getTitle()).isEqualTo(newTitle);
    assertThat(diary.getData()).isEqualTo(newData);
  }


  @Test
  void deleteTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    createDiary(USER_1);
    final var diary = diaryRepository.findAll().get(0);
    mvc.perform(delete(ENDPOINT +"/"+diary.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isNoContent());
    assertThat(diaryRepository.findAll()).isEmpty();
  }

}