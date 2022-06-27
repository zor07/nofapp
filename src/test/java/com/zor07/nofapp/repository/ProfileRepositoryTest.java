package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.File;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileRepositoryTest extends AbstractApplicationTest {

    private static final String BUCKET_1 = "bucket_1";
    private static final String PREFIX_1 = "prefix_1";
    private static final String KEY_1 = "key_1";
    private static final String MIME_1 = "mime_1";
    private static final int SIZE_1 = 1;
    private static final String BUCKET_2 = "bucket_2";
    private static final String PREFIX_2 = "prefix_2";
    private static final String KEY_2 = "key_2";
    private static final String MIME_2 = "mime_2";
    private static final int SIZE_2 = 2;

    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";


    private static final Instant TIMER_START_1 = Instant.parse("2022-05-01T15:26:00Z");
    private static final Instant TIMER_START_2 = Instant.parse("2022-05-01T15:27:00Z");


    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeMethod
    @AfterClass
    void clearDb() {
        profileRepository.deleteAll();
        userRepository.deleteAll();
        fileRepository.deleteAll();
    }

    private User persistUser() {
        final var user = new User();
        user.setName(USERNAME);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        return userRepository.save(user);
    }

    private File persistFile1() {
        final var file = new File();
        file.setBucket(BUCKET_1);
        file.setPrefix(PREFIX_1);
        file.setKey(KEY_1);
        file.setMime(MIME_1);
        file.setSize(SIZE_1);
        return fileRepository.save(file);
    }

    private File persistFile2() {
        final var file = new File();
        file.setBucket(BUCKET_2);
        file.setPrefix(PREFIX_2);
        file.setKey(KEY_2);
        file.setMime(MIME_2);
        file.setSize(SIZE_2);
        return fileRepository.save(file);
    }


    @Test
    void testCrud() {
        final var user = persistUser();
        final var file1 = persistFile1();
        final var file2 = persistFile2();

        final var all = profileRepository.findAll();
        assertThat(all).isEmpty();

        // create-read
        final var profile = new Profile();
        profile.setUser(user);
        profile.setAvatar(file1);
        profile.setTimerStart(TIMER_START_1);

        final var id = profileRepository.save(profile).getId();
        final var inserted = profileRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getTimerStart()).isEqualTo(TIMER_START_1);
        assertThat(inserted.getUser().getName()).isEqualTo(USERNAME);
        assertThat(inserted.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(inserted.getUser().getPassword()).isEqualTo(PASSWORD);
        assertThat(inserted.getAvatar().getBucket()).isEqualTo(BUCKET_1);
        assertThat(inserted.getAvatar().getPrefix()).isEqualTo(PREFIX_1);
        assertThat(inserted.getAvatar().getKey()).isEqualTo(KEY_1);
        assertThat(inserted.getAvatar().getMime()).isEqualTo(MIME_1);
        assertThat(inserted.getAvatar().getSize()).isEqualTo(SIZE_1);

        //update
        inserted.setAvatar(file2);
        inserted.setTimerStart(TIMER_START_2);
        profileRepository.save(inserted);

        final var updated = profileRepository.findById(id).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getTimerStart()).isEqualTo(TIMER_START_2);
        assertThat(updated.getUser().getName()).isEqualTo(USERNAME);
        assertThat(updated.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(updated.getUser().getPassword()).isEqualTo(PASSWORD);
        assertThat(updated.getAvatar().getBucket()).isEqualTo(BUCKET_2);
        assertThat(updated.getAvatar().getPrefix()).isEqualTo(PREFIX_2);
        assertThat(updated.getAvatar().getKey()).isEqualTo(KEY_2);
        assertThat(updated.getAvatar().getMime()).isEqualTo(MIME_2);
        assertThat(updated.getAvatar().getSize()).isEqualTo(SIZE_2);

        //delete
        profileRepository.delete(updated);
        assertThat(profileRepository.findById(id)).isEmpty();
    }

}
