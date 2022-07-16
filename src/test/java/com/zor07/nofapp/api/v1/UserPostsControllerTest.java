package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.NoteDto;
import com.zor07.nofapp.entity.Note;
import com.zor07.nofapp.entity.Notebook;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.entity.UserPost;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.NotebookRepository;
import com.zor07.nofapp.repository.RoleRepository;
import com.zor07.nofapp.repository.UserPostsRepository;
import com.zor07.nofapp.repository.UserRepository;
import com.zor07.nofapp.service.UserPostService;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.spring.AbstractApiTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserPostsControllerTest extends AbstractApiTest {

    private static final String USER_POSTS_ENDPOINT = "/api/v1/profiles/{userId}/posts";
    private static final String USERNAME = "user";
    private static final String NOTEBOOK_NAME = "notebook name";
    private static final String NOTEBOOK_DESCRIPTION = "notebook desc";
    private static final String NOTE_DATA = "{\"data\":\"value\"}";
    private static final String NOTE_TITLE = "note title";
    @Autowired
    private NotebookRepository notebookRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserPostsRepository userPostsRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserPostService userPostService;

    private @Autowired WebApplicationContext context;

    private MockMvc mvc;

    @Test
    void shouldAddPostToProfile() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(USERNAME, roleName);
        final var notebook = persistNotebook(createNotebook(user));
        final var note = persistNote(createNote(notebook));
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        // when
        mvc.perform(post(USER_POSTS_ENDPOINT + "/{noteId}", user.getId(), note.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        // then
        final var posts = userPostsRepository.findAllByUserId(user.getId());
        final var post = posts.get(0);
        assertThat(posts).hasSize(1);
        assertThat(post.getNote().getId()).isEqualTo(note.getId());
        assertThat(post.getNote().getTitle()).isEqualTo(NOTE_TITLE);
        assertThat(objectMapper.readTree(post.getNote().getData())).isEqualTo(objectMapper.readTree(NOTE_DATA));
        assertThat(post.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldReturnUserPosts() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(USERNAME, roleName);
        final var notebook = persistNotebook(createNotebook(user));
        final var note1 = persistNote(createNote(notebook));
        final var note2 = persistNote(createNote(notebook));
        final var note3 = persistNote(createNote(notebook));
        userPostsRepository.save(new UserPost(user, note1));
        userPostsRepository.save(new UserPost(user, note2));
        userPostsRepository.save(new UserPost(user, note3));
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        final var content = mvc.perform(get(USER_POSTS_ENDPOINT, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final var userPosts = objectMapper.readValue(content, new TypeReference<List<NoteDto>>() {});

        assertThat(userPosts).hasSize(3);
        final var post = userPosts.get(0);
        assertThat(post.data()).isEqualTo(objectMapper.readTree(NOTE_DATA));
        assertThat(post.title()).isEqualTo(NOTE_TITLE);

    }

    @Test
    void shouldRemovePostFromProfile() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(USERNAME, roleName);
        final var notebook = persistNotebook(createNotebook(user));
        final var note = persistNote(createNote(notebook));
        final var userPost = new UserPost(user, note);
        userPostsRepository.save(userPost);
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        // when
        userPostService.removePostFromUser(user.getId(), note.getId());

        // then
        assertThat(userPostsRepository.findAll()).isEmpty();
    }

//    @DeleteMapping("/{noteId}")
//    public ResponseEntity<ProfileDto> removePostFromProfile(final Principal principal,
//                                                            final @PathVariable Long userId,
//                                                            final @PathVariable Long noteId) {
//        final var user = userService.getUser(principal);
//        if (!Objects.equals(userId, user.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        userPostService.removePostFromUser(userId, noteId);
//        return ResponseEntity.noContent().build();
//    }

    @BeforeClass
    private void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterMethod
    private void cleanUp() {
        userPostsRepository.deleteAll();
        noteRepository.deleteAll();
        notebookRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private String persistRole() {
        final var role = createRole();
        roleRepository.save(role);
        return role.getName();
    }
    private User persistUser(final String name, final String roleName) {
        final var user = createUser(name);
        userService.saveUser(user);
        userService.addRoleToUser(user.getUsername(), roleName);
        return user;
    }

    private Note createNote(final Notebook notebook) {
        final var note = new Note();
        note.setTitle(NOTE_TITLE);
        note.setData(NOTE_DATA);
        note.setNotebook(notebook);
        return note;
    }

    private Notebook createNotebook(final User user) {
        final var notebook = new Notebook();
        notebook.setUser(user);
        notebook.setName(NOTEBOOK_NAME);
        notebook.setDescription(NOTEBOOK_DESCRIPTION);
        return notebookRepository.save(notebook);
    }

    private Note persistNote(final Note note) {
        return noteRepository.save(note);
    }

    private Notebook persistNotebook(final Notebook notebook) {
        return notebookRepository.save(notebook);
    }


}
