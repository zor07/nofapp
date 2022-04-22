package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.NoteDto;
import com.zor07.nofapp.notebook.Note;
import com.zor07.nofapp.notebook.NoteRepository;
import com.zor07.nofapp.notebook.Notebook;
import com.zor07.nofapp.notebook.NotebookRepository;
import com.zor07.nofapp.test.AbstractApiTest;
import com.zor07.nofapp.user.RoleRepository;
import com.zor07.nofapp.user.UserRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NoteControllerTest extends AbstractApiTest {

  private static final String USER_1 = "user1";
  private static final String USER_2 = "user2";
  private static final String NOTEBOOK_NAME = "NOTEBOOK_NAME";
  private static final String NOTEBOOK_DESCRIPTION = "NOTEBOOK_DESCRIPTION";
  private static final String NOTE_TITLE = "NOTE_TITLE";
  private static final String NOTE_DATA = "{\"data\":\"data\"}";
  private static final String ENDPOINT = "/api/v1/notebook/%d/note";
  @Autowired
  private NotebookRepository notebookRepository;
  @Autowired
  private NoteRepository noteRepository;
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;

  private MockMvc mvc;

  private Notebook createNotebook(final String username) {
    final var user = userService.getUser(username);
    final var notebook = new Notebook(null, user, NOTEBOOK_NAME, NOTEBOOK_DESCRIPTION);
    return notebookRepository.save(notebook);
  }

  private Note createNote(final Notebook notebook) {
    final var note = new Note(null, notebook, NOTE_TITLE, NOTE_DATA);
    return noteRepository.save(note);
  }
  private void clearDb() {
    noteRepository.deleteAll();
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
    final var notebook = createNotebook(USER_1);
    createNote(notebook);
    createNote(notebook);
    final var authHeader = getAuthHeader(mvc, USER_1);

    final var endpoint = String.format("/api/v1/notebooks/%d/notes", notebook.getId());

    final var content = mvc.perform(get(endpoint)
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var notes = objectMapper.readValue(content, new TypeReference<List<NoteDto>>(){});
    assertThat(notes).hasSize(2);
    assertThat(notes.get(0).id).isNotNull();
    assertThat(notes.get(0).data.isNull()).isTrue();
    assertThat(notes.get(0).title).isEqualTo(NOTE_TITLE);
    assertThat(notes.get(1).id).isNotNull();
    assertThat(notes.get(1).data.isNull()).isTrue();
    assertThat(notes.get(1).title).isEqualTo(NOTE_TITLE);
  }

//  @Test
//  void findByIdTest() throws Exception {
//    createNotebook(USER_1);
//    final var userId = userRepository.findByUsername(USER_1).getId();
//    final var diaryId = notebookRepository.findAllByUserId(userId).get(0).getId();
//    final var authHeader = getAuthHeader(mvc, USER_1);
//
//    final var content = mvc.perform(get(ENDPOINT+"/"+diaryId)
//        .contentType(MediaType.APPLICATION_JSON)
//        .header(HttpHeaders.AUTHORIZATION, authHeader))
//        .andExpect(status().isOk())
//        .andReturn().getResponse().getContentAsString();
//    final var diary = objectMapper.readValue(content, NoteTestDto.class);
//    assertThat(diary.id).isEqualTo(diaryId);
//    assertThat(diary.title).isEqualTo(NOTE_TITLE);
//    assertThat(diary.data.toString()).isEqualTo(NOTE_DATA);
//  }
//
//  @Test
//  void saveTest() throws Exception {
//    final var authHeader = getAuthHeader(mvc, USER_1);
//    final var diaryTestDto = new NoteTestDto();
//    diaryTestDto.title = NOTE_TITLE;
//    diaryTestDto.data = objectMapper.readTree(NOTE_DATA);
//    mvc.perform(post(ENDPOINT)
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(objectMapper.writeValueAsString(diaryTestDto))
//              .header(HttpHeaders.AUTHORIZATION, authHeader))
//        .andExpect(status().isCreated());
//    final var diary = notebookRepository.findAll().get(0);
//    assertThat(diary.getTitle()).isEqualTo(NOTE_TITLE);
//    assertThat(diary.getData()).isEqualTo(NOTE_DATA);
//  }
//
//  @Test
//  void updateTest() throws Exception {
//    createNotebook(USER_1);
//    final var id = notebookRepository.findAll().get(0).getId();
//    final var authHeader = getAuthHeader(mvc, USER_1);
//    final var newTitle = "new Title";
//    final var newData = "{\"data\":\"new Data\"}";
//    final var diaryTestDto = new NoteTestDto();
//    diaryTestDto.id = id;
//    diaryTestDto.title = newTitle;
//    diaryTestDto.data = objectMapper.readTree(newData);
//    mvc.perform(post(ENDPOINT)
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(objectMapper.writeValueAsString(diaryTestDto))
//        .header(HttpHeaders.AUTHORIZATION, authHeader))
//        .andExpect(status().isCreated());
//    final var all = notebookRepository.findAll();
//    assertThat(all).hasSize(1);
//    final var diary = all.get(0);
//    assertThat(diary.getTitle()).isEqualTo(newTitle);
//    assertThat(diary.getData()).isEqualTo(newData);
//  }


  @Test
  void deleteTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    createNotebook(USER_1);
    final var diary = notebookRepository.findAll().get(0);
    mvc.perform(delete(ENDPOINT +"/"+diary.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isNoContent());
    assertThat(notebookRepository.findAll()).isEmpty();
  }

}