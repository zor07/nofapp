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
import com.zor07.nofapp.service.ProfileService;
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

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerTest extends AbstractApiTest {

    private static final String PROFILE_ENDPOINT = "/api/v1/profiles";
    private static final Instant TIMER_START = Instant.parse("2022-05-01T15:26:00Z");
    private static final String BUCKET = "user";
    private static final String KEY = "avatar";
    private static final String MIME = "MIME";
    private static final long SIZE = 1L;

    private @Autowired ProfileService profileService;
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
            profile.userId() != null
        );
    }

    @Test
    void shouldReturnProfileByUser() throws Exception {
        // given
        final var user = persistUser(DEFAULT_USERNAME);
        final var avatar = persistAvatar(createAvatar(user));
        persistProfile(createProfile(user, avatar));
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        final var content = mvc.perform(get(PROFILE_ENDPOINT + "/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        final var profile = objectMapper.readValue(content, ProfileDto.class);
        assertThat(profile).isNotNull();
        assertThat(profile.timerStart().atZone(ZoneId.systemDefault()).toInstant()).isEqualTo(TIMER_START);
        assertThat(profile.userId()).isEqualTo(user.getId());
        assertThat(profile.avatarUri()).isEqualTo(String.format("%s/%s/%s", BUCKET, user.getId(), KEY));
    }

//    @PostMapping("/{userId}/avatar")
//    public ResponseEntity<Void> uploadAvatar(final Principal principal,
//                                             final @PathVariable Long userId,
//                                             final @RequestParam("file") MultipartFile file) throws IOException {
//        final var user = userService.getUser(principal);
//        if (!Objects.equals(userId, user.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        final var data = file.getBytes();
//        final var contentType = file.getContentType();
//        final var size = file.getSize();
//        profileService.saveUserAvatar(userId, data, contentType, size);
//        return ResponseEntity.accepted().build();
//    }
//
//    @DeleteMapping("/{userId}/avatar")
//    public ResponseEntity<Void> deleteAvatar(final Principal principal,
//                                             final @PathVariable Long userId) {
//        final var user = userService.getUser(principal);
//        if (!Objects.equals(userId, user.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        profileService.deleteUserAvatar(userId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/{userId}/relapsed")
//    public ResponseEntity<ProfileDto> relapsed(final Principal principal,
//                                               final @PathVariable Long userId) {
//        final var user = userService.getUser(principal);
//        if (!Objects.equals(userId, user.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        profileService.relapsed(user);
//        return ResponseEntity.accepted().build();
//    }


    @BeforeClass
    private void setup() {
        createRole();
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

    private User persistUser(final String name) {
        final var user = createUser(name);
        userService.saveUser(user);
        userService.addRoleToUser(user.getUsername(), DEFAULT_ROLE);
        return user;
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
