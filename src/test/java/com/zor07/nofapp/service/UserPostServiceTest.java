package com.zor07.nofapp.service;

import com.zor07.nofapp.entity.notes.Note;
import com.zor07.nofapp.entity.notes.Notebook;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.entity.UserPost;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.NotebookRepository;
import com.zor07.nofapp.repository.UserPostsRepository;
import com.zor07.nofapp.repository.UserRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class UserPostServiceTest extends AbstractApplicationTest {

    private static final String USERNAME = "user";
    private static final String PASS = "PASS";
    private static final String NOTEBOOK_NAME = "notebook name";
    private static final String NOTEBOOK_DESCRIPTION = "notebook desc";
    private static final String NOTE_DATA = "{\"data\":\"value\"}";
    private static final String NOTE_TITLE = "note title";
    private final ObjectMapper objectMapper = new ObjectMapper();
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
    private UserPostService userPostService;

    @Test
    void shouldAddPostToProfile() throws IOException {
        // given
        final var user = persistUser(USERNAME);
        final var notebook = persistNotebook(createNotebook(user));
        final var note = persistNote(createNote(notebook));

        // when
        userPostService.addPostToUser(user, note.getId());

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
    void shouldReturnUserPosts() throws IOException {
        // given
        final var user = persistUser(USERNAME);
        final var notebook = persistNotebook(createNotebook(user));
        final var note1 = persistNote(createNote(notebook));
        final var note2 = persistNote(createNote(notebook));
        final var note3 = persistNote(createNote(notebook));
        userPostsRepository.save(new UserPost(user, note1));
        userPostsRepository.save(new UserPost(user, note2));
        userPostsRepository.save(new UserPost(user, note3));

        // when
        final var userPosts = userPostService.getUserPosts(user.getId());

        assertThat(userPosts).hasSize(3);
        final var post = userPosts.get(0);
        assertThat(objectMapper.readTree(post.getData())).isEqualTo(objectMapper.readTree(NOTE_DATA));
        assertThat(post.getTitle()).isEqualTo(NOTE_TITLE);

    }


    @Test
    void shouldRemovePostFromProfile() {
        // given
        final var user = persistUser(USERNAME);
        final var notebook = persistNotebook(createNotebook(user));
        final var note = persistNote(createNote(notebook));
        final var userPost = new UserPost(user, note);
        userPostsRepository.save(userPost);

        // when
        userPostService.removePostFromUser(user.getId(), note.getId());

        // then
        assertThat(userPostsRepository.findAll()).isEmpty();
    }

    @AfterMethod
    private void cleanUp() {
        userPostsRepository.deleteAll();
        noteRepository.deleteAll();
        notebookRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User persistUser(final String name) {
        return userService.saveUser(new User(null, name, name, PASS, new ArrayList<>()));
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
