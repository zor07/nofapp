package com.zor07.nofapp.service;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.File;
import com.zor07.nofapp.entity.Note;
import com.zor07.nofapp.entity.Notebook;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.entity.UserPost;
import com.zor07.nofapp.repository.FileRepository;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.NotebookRepository;
import com.zor07.nofapp.repository.ProfileRepository;
import com.zor07.nofapp.repository.RelapseLogRepository;
import com.zor07.nofapp.repository.UserPostsRepository;
import com.zor07.nofapp.repository.UserRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ProfileServiceTest extends AbstractApplicationTest {

    private static final Instant TIMER_START = Instant.parse("2022-05-01T15:26:00Z");
    private static final String BUCKET = "user";
    private static final String KEY = "avatar";
    private static final String MIME = "MIME";
    private static final long SIZE = 1L;
    private static final String USERNAME = "user";
    private static final String PASS = "PASS";
    private static final String NOTEBOOK_NAME = "notebook name";
    private static final String NOTEBOOK_DESCRIPTION = "notebook desc";
    private static final String NOTE_DATA = "{\"data\":\"value\"}";
    private static final String NOTE_TITLE = "note title";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProfileService profileService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private NotebookRepository notebookRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserPostsRepository userPostsRepository;
    @Autowired
    private RelapseLogRepository relapseLogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3Service s3;
    @Test
    void shouldReturnProfileByUser() {
        // given
        final var user = persistUser(USERNAME);
        final var avatar = persistAvatar(createAvatar(user));
        final var persisted = persistProfile(createProfile(user, avatar));

        // when
        final var retrieved = profileService.getProfileByUserId(user.getId());

        // then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTimerStart()).isEqualTo(TIMER_START);
        assertThat(retrieved.getUser().getName()).isEqualTo(USERNAME);
        assertThat(retrieved.getAvatar().getId()).isEqualTo(persisted.getAvatar().getId());
        assertThat(retrieved.getAvatar().getBucket()).isEqualTo(persisted.getAvatar().getBucket());
        assertThat(retrieved.getAvatar().getPrefix()).isEqualTo(persisted.getAvatar().getPrefix());
        assertThat(retrieved.getAvatar().getKey()).isEqualTo(persisted.getAvatar().getKey());
        assertThat(retrieved.getAvatar().getMime()).isEqualTo(persisted.getAvatar().getMime());
        assertThat(retrieved.getAvatar().getSize()).isEqualTo(persisted.getAvatar().getSize());
    }

    @Test
    void shouldReturnProfiles() {
        // given
        final var username1 = "user1";
        final var username2 = "user2";
        final var username3 = "user3";
        final var user1 = persistUser(username1);
        final var user2 = persistUser(username2);
        final var user3 = persistUser(username3);
        final var avatar1 = persistAvatar(createAvatar(user1));
        final var avatar2 = persistAvatar(createAvatar(user2));
        final var avatar3 = persistAvatar(createAvatar(user3));
        persistProfile(createProfile(user1, avatar1));
        persistProfile(createProfile(user2, avatar2));
        persistProfile(createProfile(user3, avatar3));

        // when
        final var profiles = profileService.getProfiles();

        // then
        assertThat(profiles).hasSize(3);
    }

    @Test
    void shouldSaveAvatar() throws IOException {
        // given
        final var user = persistUser(USERNAME);
        final var userId = user.getId();
        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        final var data = Files.toByteArray(srcFile);
        final var contentType = "application/xml";
        persistProfile(createProfile(user, null));

        // when
        profileService.saveUserAvatar(userId, data, contentType, data.length);

        // then
        final var file = fileRepository.findAll().get(0);
        assertThat(file.getBucket()).isEqualTo(BUCKET);
        assertThat(file.getPrefix()).isEqualTo(String.valueOf(userId));
        assertThat(file.getKey()).isEqualTo(KEY);
        assertThat(file.getMime()).isEqualTo(contentType);
        assertThat(file.getSize()).isEqualTo(data.length);

        final var tempFile = java.io.File.createTempFile("temp", "file");
        s3.copyObject(BUCKET, String.format("%s/%s", userId, KEY), tempFile);

        final var bytes = Files.toByteArray(tempFile);
        assertThat(bytes).containsExactly(data);
    }

    @Test
    void shouldDeleteAvatar() throws IOException {
        // given
        final var user = persistUser(USERNAME);
        final var userId = user.getId();
        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        final var data = Files.toByteArray(srcFile);
        final var avatar = persistAvatar(createAvatar(user));
        persistProfile(createProfile(user, avatar));
        s3.persistObject(BUCKET, String.format("%s/%s", userId, KEY), data);

        // when
        profileService.deleteUserAvatar(userId);

        // then
        assertThat(s3.containsObject(BUCKET, String.format("%s/%s", userId, KEY))).isFalse();
        assertThat(fileRepository.findAll()).isEmpty();
        assertThat(profileRepository.findAll().get(0).getAvatar()).isNull();
    }

    @Test
    void shouldSaveRelapseLog() {
        // given
        final var user = persistUser(USERNAME);
        persistProfile(createProfile(user,  null));

        // when
        profileService.relapsed(user);

        // then
        final var relapseLog = relapseLogRepository.findAll().get(0);
        final var profile = profileRepository.findAll().get(0);

        assertThat(profile.getTimerStart()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
        assertThat(relapseLog.getStart()).isEqualTo(TIMER_START);
        assertThat(relapseLog.getStop()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void shouldAddPostToProfile() throws IOException {
        // given
        final var user = persistUser(USERNAME);
        final var notebook = persistNotebook(createNotebook(user));
        final var note = persistNote(createNote(notebook));

        // when
        profileService.addPostToProfile(user, note.getId());

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
    void shouldRemovePostFromProfile() {
        // given
        final var user = persistUser(USERNAME);
        final var notebook = persistNotebook(createNotebook(user));
        final var note = persistNote(createNote(notebook));
        final var userPost = new UserPost(user, note);
        userPostsRepository.save(userPost);

        // when
        profileService.removePostFromProfile(user.getId(), note.getId());

        // then
        assertThat(userPostsRepository.findAll()).isEmpty();
    }
    @BeforeMethod
    private void setUp() {
        if (!s3.containsBucket(BUCKET)) {
            s3.createBucket(BUCKET);
        }
    }
    @AfterMethod
    private void cleanUp() {
        s3.truncateBucket(BUCKET);
        profileRepository.deleteAll();
        relapseLogRepository.deleteAll();
        userPostsRepository.deleteAll();
        fileRepository.deleteAll();
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

    private File createAvatar(final User user) {
        final var file = new File();
        file.setBucket(BUCKET);
        file.setPrefix(String.valueOf(user.getId()));
        file.setKey(KEY);
        file.setMime(MIME);
        file.setSize(SIZE);
        return file;
    }

    private File persistAvatar(final File avatar) {
        return fileRepository.save(avatar);
    }

    private Profile persistProfile(final Profile profile) {
        return profileRepository.save(profile);
    }

    private Profile createProfile(final User user, final File avatar) {
        final var p = new Profile();
        p.setUser(user);
        p.setAvatar(avatar);
        p.setTimerStart(TIMER_START);
        return p;
    }
}