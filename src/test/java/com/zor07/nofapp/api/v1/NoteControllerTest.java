package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.NoteDto;
import com.zor07.nofapp.api.v1.dto.NotebookDto;
import com.zor07.nofapp.notebook.note.Note;
import com.zor07.nofapp.notebook.note.NoteRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

  @Test
  void findByIdTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var notebook = createNotebook(USER_1);
    final var note = createNote(notebook);
    final var endpoint = String.format("/api/v1/notebooks/%d/notes/%d", notebook.getId(), note.getId());

    final var content = mvc.perform(get(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var response = objectMapper.readValue(content, NoteDto.class);
    assertThat(response.id).isEqualTo(note.getId());
    assertThat(response.title).isEqualTo(note.getTitle());
    assertThat(response.data.toString()).isEqualTo(note.getData());
  }

  @Test
  void saveTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var notebook = createNotebook(USER_1);
    final var notebookDto = new NotebookDto();
    notebookDto.id = notebook.getId();
    final var noteRequestDto = new NoteDto();
    noteRequestDto.notebookDto = notebookDto;
    noteRequestDto.title = NOTE_TITLE;
    noteRequestDto.data = objectMapper.readTree(NOTE_DATA);

    final var endpoint = String.format("/api/v1/notebooks/%d/notes", notebook.getId());

    mvc.perform(post(endpoint)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(noteRequestDto))
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isCreated());
    final var noteResponse = noteRepository.findAll().get(0);
    assertThat(noteResponse.getTitle()).isEqualTo(noteRequestDto.title);
    assertThat(noteResponse.getData()).isEqualTo(noteRequestDto.data.toString());
  }

  @Test
  void updateTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var notebook = createNotebook(USER_1);
    createNote(notebook);
    final var noteId = notebookRepository.findAll().get(0).getId();
    final var newTitle = "new Title";
    final var newData = "{\"data\":\"new Data\"}";
    final var notebookDto = new NotebookDto();
    notebookDto.id = notebook.getId();
    final var noteRequestDto = new NoteDto();
    noteRequestDto.id = noteId;
    noteRequestDto.notebookDto = notebookDto;
    noteRequestDto.title = newTitle;
    noteRequestDto.data = objectMapper.readTree(newData);
    final var endpoint = String.format("/api/v1/notebooks/%d/notes", notebook.getId());
    assertThat(noteRepository.findAll()).hasSize(1);

    mvc.perform(put(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(noteRequestDto))
        .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isAccepted());

    final var all = noteRepository.findAll();
    assertThat(all).hasSize(1);
    final var note = all.get(0);
    assertThat(note.getTitle()).isEqualTo(newTitle);
    assertThat(note.getData()).isEqualTo(newData);
  }


  @Test
  void deleteTest() throws Exception {
    final var authHeader = getAuthHeader(mvc, USER_1);
    final var notebook = createNotebook(USER_1);
    final var note = createNote(notebook);
    final var endpoint = String.format("/api/v1/notebooks/%d/notes/%d", notebook.getId(), note.getId());

    mvc.perform(delete(endpoint)
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, authHeader))
        .andExpect(status().isNoContent());

    assertThat(noteRepository.findAll()).isEmpty();
  }

}