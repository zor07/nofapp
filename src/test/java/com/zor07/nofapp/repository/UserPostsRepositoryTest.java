package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.notes.Note;
import com.zor07.nofapp.entity.notes.Notebook;
import com.zor07.nofapp.entity.profile.UserPost;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UserPostsRepositoryTest extends AbstractApplicationTest {

    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";
    private static final String USER_PASS = "pass";

    private static final String NOTEBOOK_NAME = "practice1";
    private static final String NOTEBOOK_DESCRIPTION = "description1";
    private static final String NOTE_TITLE_1 = "title1";
    private static final String NOTE_TITLE_2 = "title2";
    private static final String NOTE_DATA = "{\"data\": \"value\"}";

    private @Autowired UserRepository userRepository;
    private @Autowired UserService userService;
    private @Autowired NotebookRepository notebookRepository;
    private @Autowired NoteRepository noteRepository;
    private @Autowired UserPostsRepository userPostsRepository;

    @BeforeMethod
    private void setup() {
        clearDb();
        userRepository.save(createUser(USER_1));
        userRepository.save(createUser(USER_2));
    }

    @AfterClass
    private void clearDb() {
        userPostsRepository.deleteAll();
        noteRepository.deleteAll();
        notebookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateDelete() {
        final var user = getUser();
        final var notebook = createNotebook(user);
        final var note = createNote(notebook, NOTE_TITLE_1);


        final var userPost = new UserPost(user, note);

        userPostsRepository.save(userPost);

        final var userPosts = userPostsRepository.findAll();
        assertThat(userPosts).hasSize(1);
        final var inserted = userPosts.get(0);
        assertThat(inserted.getUser().getName()).isEqualTo(USER_1);
        assertThat(inserted.getNote().getTitle()).isEqualTo(NOTE_TITLE_1);
        assertThat(inserted.getNote().getData()).isEqualTo(NOTE_DATA);
        assertThat(inserted.getNote().getNotebook()).isNotNull();

        userPostsRepository.delete(inserted);
        assertThat(userPostsRepository.findAll()).isEmpty();
    }

    @Test
    void findAllByUserTest() {
        final var user = getUser();
        final var notebook = createNotebook(user);
        final var note1 = createNote(notebook, NOTE_TITLE_1);
        final var note2 = createNote(notebook, NOTE_TITLE_2);
        userPostsRepository.save(new UserPost(user, note1));
        userPostsRepository.save(new UserPost(user, note2));

        assertThat(userPostsRepository.findAllByUserId(user.getId())).hasSize(2);
    }

    private User getUser() {
        return userService.getUser(USER_1);
    }

    private User createUser(final String username) {
        return new User(null, username, username, USER_PASS, Collections.emptyList());
    }

    private Notebook createNotebook(final User user) {
        final var notebook = new Notebook();
        notebook.setUser(user);
        notebook.setName(NOTEBOOK_NAME);
        notebook.setDescription(NOTEBOOK_DESCRIPTION);
        return notebookRepository.save(notebook);
    }

    private Note createNote(final Notebook notebook,
                            final String title) {
        final var note = new Note();
        note.setTitle(title);
        note.setNotebook(notebook);
        note.setData(NOTE_DATA);
        return noteRepository.save(note);
    }
}
