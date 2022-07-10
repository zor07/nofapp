package com.zor07.nofapp.service;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.File;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.repository.*;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileServiceTest extends AbstractApplicationTest {

    private static final Instant TIMER_START = Instant.parse("2022-05-01T15:26:00Z");
    private static final Instant TIMER_STOP = Instant.parse("2022-05-01T15:27:00Z");

    private static final String BUCKET = "user";
    private static final String KEY = "avatar";
    private static final String MIME = "MIME";
    private static final long SIZE = 1L;

    private static final String USER = "USER";
    private static final String PASS = "PASS";

    @Autowired
    private ProfileService profileService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private ProfileRepository profileRepository;
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

    private User getUser(final String username) {
        return userService.getUser(username);
    }

    private User persistUser() {
        return userService.saveUser(new User(null, USER, USER, PASS, new ArrayList<>()));
    }

    @BeforeClass
    private void setUp() {
        if (!s3.containsBucket(BUCKET)) {
            s3.createBucket(BUCKET);
        }
    }

    @AfterClass
    private void cleanUp() {
        s3.deleteBucket(BUCKET);
        profileRepository.deleteAll();
        fileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnProfileByUser() {
        // given
        final var user = persistUser();
        final var avatar = persistAvatar(createAvatar(user));
        final var persisted = persistProfile(createProfile(user, avatar));

        // when
        final var retrieved = profileService.getProfileByUserId(user.getId());

        // then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTimerStart()).isEqualTo(TIMER_START);
        assertThat(retrieved.getUser().getName()).isEqualTo(USER);
        assertThat(retrieved.getAvatar().getId()).isEqualTo(persisted.getAvatar().getId());
        assertThat(retrieved.getAvatar().getBucket()).isEqualTo(persisted.getAvatar().getBucket());
        assertThat(retrieved.getAvatar().getPrefix()).isEqualTo(persisted.getAvatar().getPrefix());
        assertThat(retrieved.getAvatar().getKey()).isEqualTo(persisted.getAvatar().getKey());
        assertThat(retrieved.getAvatar().getMime()).isEqualTo(persisted.getAvatar().getMime());
        assertThat(retrieved.getAvatar().getSize()).isEqualTo(persisted.getAvatar().getSize());
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
