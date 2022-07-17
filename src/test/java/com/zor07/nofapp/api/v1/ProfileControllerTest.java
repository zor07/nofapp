package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.ProfileDto;
import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.File;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.repository.FileRepository;
import com.zor07.nofapp.repository.ProfileRepository;
import com.zor07.nofapp.repository.RelapseLogRepository;
import com.zor07.nofapp.repository.RoleRepository;
import com.zor07.nofapp.repository.UserRepository;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.spring.AbstractApiTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerTest extends AbstractApiTest {

    private static final String PROFILE_ENDPOINT = "/api/v1/profiles";
    private static final Instant TIMER_START = Instant.parse("2022-05-01T15:26:00Z");
    private static final String BUCKET = "user";
    private static final String KEY = "avatar";
    private static final String MIME = "MIME";
    private static final long SIZE = 1L;
    private @Autowired FileRepository fileRepository;
    private @Autowired ProfileRepository profileRepository;
    private @Autowired RelapseLogRepository relapseLogRepository;
    private @Autowired UserService userService;
    private @Autowired UserRepository userRepository;
    private @Autowired RoleRepository roleRepository;
    private @Autowired S3Service s3;
    private @Autowired WebApplicationContext context;
    private MockMvc mvc;

    @Test
    void shouldReturnProfiles() throws Exception {
        // given
        final var roleName = persistRole();
        final var username1 = "user1";
        final var username2 = "user2";
        final var username3 = "user3";
        final var user1 = persistUser(username1, roleName);
        final var user2 = persistUser(username2, roleName);
        final var user3 = persistUser(username3, roleName);
        final var avatar1 = persistAvatar(createAvatar(user1));
        final var avatar2 = persistAvatar(createAvatar(user2));
        final var avatar3 = persistAvatar(createAvatar(user3));
        persistProfile(createProfile(user1, avatar1));
        persistProfile(createProfile(user2, avatar2));
        persistProfile(createProfile(user3, avatar3));
        final var authHeader = getAuthHeader(mvc, username1);

        // when
        final var content = mvc.perform(get(PROFILE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        final var profiles = objectMapper.readValue(content, new TypeReference<List<ProfileDto>>() {});
        assertThat(profiles).hasSize(3);
        assertThat(profiles).allMatch(profile ->
            profile.id() != null &&
            profile.avatarUri() != null &&
            profile.timerStart() != null &&
            profile.user().name().startsWith("user") &&
            profile.user().id() != null
        );
    }

    @Test
    void shouldReturnProfileByUser() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        final var avatar = persistAvatar(createAvatar(user));
        persistProfile(createProfile(user, avatar));
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        final var content = mvc.perform(get(PROFILE_ENDPOINT + "/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        final var profile = objectMapper.readValue(content, ProfileDto.class);
        assertThat(profile).isNotNull();
        assertThat(profile.timerStart().atZone(ZoneId.systemDefault()).toInstant()).isEqualTo(TIMER_START);
        assertThat(profile.user().id()).isEqualTo(String.valueOf(user.getId()));
        assertThat(profile.user().name()).isEqualTo(String.valueOf(user.getName()));
        assertThat(profile.user().username()).isEqualTo(String.valueOf(user.getUsername()));
        assertThat(profile.avatarUri()).isEqualTo(String.format("%s/%s/%s", BUCKET, user.getId(), KEY));
    }

    @Test
    void shouldSaveAvatar() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        final var userId = user.getId();
        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        final var data = Files.toByteArray(srcFile);
        final var contentType = "application/xml";
        persistProfile(createProfile(user, null));
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            MockMultipartFile requestFile = new MockMultipartFile("file", srcFile.getName(), contentType, fis);
            mvc.perform(multipart(PROFILE_ENDPOINT + "/{userId}/avatar", userId)
                            .file(requestFile)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, authHeader))
                    .andExpect(status().isAccepted())
                    .andReturn().getResponse().getContentAsString();
        }

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
    void shouldDeleteAvatar() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        final var userId = user.getId();
        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        final var data = Files.toByteArray(srcFile);
        final var avatar = persistAvatar(createAvatar(user));
        persistProfile(createProfile(user, avatar));
        s3.persistObject(BUCKET, String.format("%s/%s", userId, KEY), data);
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        mvc.perform(delete(PROFILE_ENDPOINT + "/{userId}/avatar", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(s3.containsObject(BUCKET, String.format("%s/%s", userId, KEY))).isFalse();
        assertThat(fileRepository.findAll()).isEmpty();
        assertThat(profileRepository.findAll().get(0).getAvatar()).isNull();
    }

    @Test
    void shouldSaveRelapseLog() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        persistProfile(createProfile(user,  null));
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        mvc.perform(post(PROFILE_ENDPOINT + "/{userId}/relapsed", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        // then
        final var relapseLog = relapseLogRepository.findAll().get(0);
        final var profile = profileRepository.findAll().get(0);

        assertThat(profile.getTimerStart()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
        assertThat(relapseLog.getStart()).isEqualTo(TIMER_START);
        assertThat(relapseLog.getStop()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @BeforeClass
    private void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        if (!s3.containsBucket(BUCKET)) {
            s3.createBucket(BUCKET);
        }
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterMethod
    private void cleanUp() {
        s3.truncateBucket(BUCKET);
        profileRepository.deleteAll();
        relapseLogRepository.deleteAll();
        fileRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private User persistUser(final String name, final String roleName) {
        final var user = createUser(name);
        userService.saveUser(user);
        userService.addRoleToUser(user.getUsername(), roleName);
        return user;
    }

    private String persistRole() {
        final var role = createRole();
        roleRepository.save(role);
        return role.getName();
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
