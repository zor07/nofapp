package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.entity.Notebook;
import com.zor07.nofapp.repository.NotebookRepository;
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

public class NotebookControllerTest extends AbstractApiTest {

  private static final String USER_1 = "user1";
  private static final String USER_2 = "user2";
  private static final String NAME = "test name";
  private static final String DESCRIPTION = "test description";

  private static final String ENDPOINT = "/api/v1/notebooks";
  private static class NotebookTestDto {
    public Long id;
    public String name;
    public String description;
  }

  private void createNotebook(final String username) {
    final var user = userService.getUser(username);
    final var diary = new Notebook(null, user, NAME, DESCRIPTION);
    notebookRepository.save(diary);
  }

  @Autowired
  private NotebookRepository notebookRepository;
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;

  private MockMvc mvc;

  private void clearDb() {
    notebookRepository.deleteAll();
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
    createNotebook(USER_1);
    createNotebook(USER_1);
    createNotebook(USER_2);
    final var authHeader = getAuthHeader(mvc, USER_1);

    final var content = mvc.perform(get(ENDPOINT)
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var notebooks = objectMapper.readValue(content, new TypeReference<List<NotebookTestDto>>(){});
    assertThat(notebooks).hasSize(2);
    assertThat(notebooks.get(0).id).isNotNull();
    assertThat(notebooks.get(0).description).isEqualTo(DESCRIPTION);
    assertThat(notebooks.get(0).name).isEqualTo(NAME);
    assertThat(notebooks.get(1).id).isNotNull();
    assertThat(notebooks.get(1).description).isEqualTo(DESCRIPTION);
    assertThat(notebooks.get(1).name).isEqualTo(NAME);
  }

  @Test
  void findByIdTest() throws Exception {
    createNotebook(USER_1);
    final var userId = userRepository.findByUsername(USER_1).getId();
    final var diaryId = notebookRepository.findAllByUserId(userId).get(0).getId();
    final var authHeader = getAuthHeader(mvc, USER_1);

    final var content = mvc.perform(get(ENDPOINT+"/"+diaryId)
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var notebook = objectMapper.readValue(content, NotebookTestDto.class);
    assertThat(notebook.id).isEqualTo(diaryId);
    assertThat(notebook.name).isEqualTo(NAME);
    assertThat(notebook.description).isEqualTo(DESCRIPTION);
  }

  @Test
  void saveTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var notebookTestDto = new NotebookTestDto();
    notebookTestDto.name = NAME;
    notebookTestDto.description = DESCRIPTION;
    mvc.perform(post(ENDPOINT)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(notebookTestDto))
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isCreated());
    final var notebook = notebookRepository.findAll().get(0);
    assertThat(notebook.getName()).isEqualTo(NAME);
    assertThat(notebook.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(notebook.getUser().getName()).isEqualTo(USER_1);
  }

  @Test
  void updateTest() throws Exception {
    createNotebook(USER_1);
    final var id = notebookRepository.findAll().get(0).getId();
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var newName = "new name";
    final var newDesc = "new description";
    final var notebookTestDto = new NotebookTestDto();
    notebookTestDto.id = id;
    notebookTestDto.name = newName;
    notebookTestDto.description = newDesc;
    mvc.perform(post(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(notebookTestDto))
        .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isCreated());
    final var all = notebookRepository.findAll();
    assertThat(all).hasSize(1);
    final var notebook = all.get(0);
    assertThat(notebook.getName()).isEqualTo(newName);
    assertThat(notebook.getDescription()).isEqualTo(newDesc);
    assertThat(notebook.getUser().getName()).isEqualTo(USER_1);
  }


  @Test
  void deleteTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    createNotebook(USER_1);
    final var notebook = notebookRepository.findAll().get(0);
    mvc.perform(delete(ENDPOINT +"/"+notebook.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isNoContent());
    assertThat(notebookRepository.findAll()).isEmpty();
  }

}