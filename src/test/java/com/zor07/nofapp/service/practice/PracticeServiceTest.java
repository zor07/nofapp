package com.zor07.nofapp.service.practice;

import com.zor07.nofapp.entity.practice.Practice;
import com.zor07.nofapp.entity.practice.PracticeTag;
import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.practice.UserPractice;
import com.zor07.nofapp.exception.IllegalResourceAccessException;
import com.zor07.nofapp.repository.practice.PracticeRepository;
import com.zor07.nofapp.repository.practice.PracticeTagRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.practice.UserPracticeRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.security.UserRole;
import com.zor07.nofapp.service.practice.PracticeService;
import com.zor07.nofapp.service.user.UserService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PracticeServiceTest extends AbstractApplicationTest {
    private static final String DEFAULT_PASSWORD = "pass";
    private static final String USER_1_NOT_ADMIN = "user1";
    private static final String USER_2_ADMIN = "user2";
    private static final String USER_3_NOT_ADMIN = "user3";
    private static final String PRACTICE_TAG_NAME = "practice tag name";
    private static final String PRACTICE_NAME = "practice name";
    private static final String PRACTICE_DESCRIPTION = "practice desc";
    private static final String PRACTICE_DATA = "{\"data\": \"value\"}";
    private static final String PRACTICE_DESCRIPTION_NEW = "practice desc new";

    private @Autowired PracticeService practiceService;
    private @Autowired PracticeRepository practiceRepository;
    private @Autowired PracticeTagRepository tagRepository;

    private @Autowired UserPracticeRepository userPracticeRepository;
    private @Autowired UserService userService;
    private @Autowired UserRepository userRepository;
    private @Autowired RoleRepository roleRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User getUser(final String username) {
        return userService.getUser(username);
    }
    private User persistUser(final String name) {
        return userService.saveUser(new User(null, name, name, DEFAULT_PASSWORD, new ArrayList<>()));
    }

    private Role createRole(final String roleName) {
        return userService.saveRole(new Role(null, roleName));
    }

    private PracticeTag createTag() {
        final var practiceTag = new PracticeTag();
        practiceTag.setName(PRACTICE_TAG_NAME);
        return practiceTag;
    }

    private Practice createPractice(final boolean isPublic, final PracticeTag tag) {
        final var practice = new Practice();
        practice.setPracticeTag(tag);
        practice.setName(PRACTICE_NAME);
        practice.setDescription(PRACTICE_DESCRIPTION);
        practice.setData(PRACTICE_DATA);
        practice.setPublic(isPublic);
        return practice;
    }

    private UserPractice createUserPractice(final User user, final Practice practice) {
        final var userPractice = new UserPractice();
        userPractice.setUser(user);
        userPractice.setPractice(practice);
        return userPractice;
    }

    private PracticeTag persistTag(final PracticeTag tag) {
        return tagRepository.save(tag);
    }

    private Practice persistPractice(final Practice practice) {
        return practiceRepository.save(practice);
    }

    private UserPractice persistUserPractice(final UserPractice userPractice) {
        return userPracticeRepository.save(userPractice);
    }

    private JsonNode readTree(final String source) {
        try {
            return objectMapper.readTree(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void clearDb() {
        userPracticeRepository.deleteAll();
        practiceRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @BeforeMethod
    void setup() {
        clearDb();
        userService.saveUser(persistUser(USER_1_NOT_ADMIN));
        userService.saveUser(persistUser(USER_2_ADMIN));
        userService.saveUser(persistUser(USER_3_NOT_ADMIN));
        userService.saveRole(createRole(UserRole.ROLE_USER.getRoleName()));
        userService.saveRole(createRole(UserRole.ROLE_ADMIN.getRoleName()));
        userService.addRoleToUser(USER_1_NOT_ADMIN, UserRole.ROLE_USER.getRoleName());
        userService.addRoleToUser(USER_3_NOT_ADMIN, UserRole.ROLE_USER.getRoleName());
        userService.addRoleToUser(USER_2_ADMIN, UserRole.ROLE_USER.getRoleName());
        userService.addRoleToUser(USER_2_ADMIN, UserRole.ROLE_ADMIN.getRoleName());
    }

    @AfterClass
    void teardown() {
        clearDb();
    }


    @Test
    void getPractices_shouldReturnAllPublicPractices() throws Exception {
        //given
        final var practiceTag = persistTag(createTag());
        persistPractice(createPractice(true, practiceTag));
        persistPractice(createPractice(true, practiceTag));
        persistPractice(createPractice(true, practiceTag));
        persistPractice(createPractice(false, practiceTag));

        //when
        final var practices = practiceService.getPractices(true, getUser(USER_1_NOT_ADMIN).getId());

        //then
        assertThat(practices).hasSize(3);
        practices.forEach(practice -> {
            assertThat(practice.getId()).isNotNull();
            assertThat(practice.getPracticeTag().getId()).isNotNull();
            assertThat(practice.getPracticeTag().getName()).isEqualTo(PRACTICE_TAG_NAME);
            assertThat(practice.getName()).isEqualTo(PRACTICE_NAME);
            assertThat(practice.getDescription()).isEqualTo(PRACTICE_DESCRIPTION);
            assertThat(readTree(practice.getData())).isEqualTo(readTree(PRACTICE_DATA));
            assertThat(practice.isPublic()).isTrue();
        });
    }

    @Test
    void getPractices_shouldReturnAllPrivateUserPractices() throws Exception {
        //given
        final var user1 = getUser(USER_1_NOT_ADMIN);
        final var user2 = getUser(USER_2_ADMIN);
        final var practiceTag = persistTag(createTag());
        persistPractice(createPractice(true, practiceTag));
        final var practice1 = persistPractice(createPractice(false, practiceTag));
        final var practice2 = persistPractice(createPractice(false, practiceTag));
        final var practice3 = persistPractice(createPractice(false, practiceTag));
        persistUserPractice(createUserPractice(user1, practice1));
        persistUserPractice(createUserPractice(user1, practice2));
        persistUserPractice(createUserPractice(user2, practice3));

        //when
        final var practices = practiceService.getPractices(false, getUser(USER_1_NOT_ADMIN).getId());

        //then
        assertThat(practices).hasSize(2);
        practices.forEach(practice -> {
            assertThat(practice.getId()).isNotNull();
            assertThat(practice.getPracticeTag().getId()).isNotNull();
            assertThat(practice.getPracticeTag().getName()).isEqualTo(PRACTICE_TAG_NAME);
            assertThat(practice.getName()).isEqualTo(PRACTICE_NAME);
            assertThat(practice.getDescription()).isEqualTo(PRACTICE_DESCRIPTION);
            assertThat(readTree(practice.getData())).isEqualTo(readTree(PRACTICE_DATA));
            assertThat(practice.isPublic()).isFalse();
        });
    }

    @Test
    void getPractices_shouldReturnEmptyListWhenNoPracticesExist() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);

        //when
        final var practices = practiceService.getPractices(true, user.getId());

        //then
        assertThat(practices).isEmpty();
    }

    @Test
    void getPracticeForUser_shouldReturnUsersPrivatePractice() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = persistPractice(createPractice(false, practiceTag));
        persistUserPractice(createUserPractice(user, expected));

        //when
        final var actual = practiceService.getPracticeForUser(expected.getId(), user);

        //then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getPracticeTag().getId()).isNotNull();
        assertThat(actual.getPracticeTag().getName()).isEqualTo(expected.getPracticeTag().getName());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(readTree(actual.getData())).isEqualTo(readTree(expected.getData()));
        assertThat(actual.isPublic()).isEqualTo(expected.isPublic());
    }

    @Test
    void getPracticeForUser_shouldReturnPublicPractice() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = persistPractice(createPractice(true, practiceTag));

        //when
        final var actual = practiceService.getPracticeForUser(expected.getId(), user);

        //then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getPracticeTag().getId()).isNotNull();
        assertThat(actual.getPracticeTag().getName()).isEqualTo(expected.getPracticeTag().getName());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(readTree(actual.getData())).isEqualTo(readTree(expected.getData()));
        assertThat(actual.isPublic()).isEqualTo(expected.isPublic());
    }

    @Test(expectedExceptions = IllegalResourceAccessException.class)
    void getPracticeForUser_shouldThrowExceptionWhenUserNotOwnsPrivatePractice() throws Exception {
        //given
        final var user1 = getUser(USER_1_NOT_ADMIN);
        final var user2 = getUser(USER_2_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = persistPractice(createPractice(false, practiceTag));
        persistUserPractice(createUserPractice(user1, expected));
        //when
        final var actual = practiceService.getPracticeForUser(expected.getId(), user2);

        //then
        //exception should be thrown
    }

    @Test
    void addPracticeToUser_shouldAddPublicPracticeToUser() throws Exception {
        //given
        final var expectedUser = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expectedPractice = persistPractice(createPractice(true, practiceTag));

        //when
        practiceService.addPracticeToUser(expectedPractice.getId(), expectedUser);

        //then
        final var userPractices = userPracticeRepository.findAll();
        final var actualPractice = userPractices.get(0).getPractice();
        final var actualUser = userPractices.get(0).getUser();
        assertThat(userPractices).hasSize(1);
        assertThat(actualUser.getName()).isEqualTo(expectedUser.getName());
        assertThat(actualPractice.getId()).isEqualTo(expectedPractice.getId());
        assertThat(actualPractice.getPracticeTag().getId()).isNotNull();
        assertThat(actualPractice.getPracticeTag().getName()).isEqualTo(expectedPractice.getPracticeTag().getName());
        assertThat(actualPractice.getName()).isEqualTo(expectedPractice.getName());
        assertThat(actualPractice.getDescription()).isEqualTo(expectedPractice.getDescription());
        assertThat(readTree(actualPractice.getData())).isEqualTo(readTree(expectedPractice.getData()));
        assertThat(actualPractice.isPublic()).isEqualTo(expectedPractice.isPublic());
    }

    @Test
    void addPracticeToUser_shouldNotAddPublicPracticeToUserWhenUserAlreadyOwnsPractice() throws Exception {
        //given
        final var expectedUser = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expectedPractice = persistPractice(createPractice(true, practiceTag));
        persistUserPractice(createUserPractice(expectedUser, expectedPractice));

        //when
        practiceService.addPracticeToUser(expectedPractice.getId(), expectedUser);

        //then
        final var userPractices = userPracticeRepository.findAll();
        final var actualPractice = userPractices.get(0).getPractice();
        final var actualUser = userPractices.get(0).getUser();
        assertThat(userPractices).hasSize(1);
        assertThat(actualUser.getName()).isEqualTo(expectedUser.getName());
        assertThat(actualPractice.getId()).isEqualTo(expectedPractice.getId());
        assertThat(actualPractice.getPracticeTag().getId()).isNotNull();
        assertThat(actualPractice.getPracticeTag().getName()).isEqualTo(expectedPractice.getPracticeTag().getName());
        assertThat(actualPractice.getName()).isEqualTo(expectedPractice.getName());
        assertThat(actualPractice.getDescription()).isEqualTo(expectedPractice.getDescription());
        assertThat(readTree(actualPractice.getData())).isEqualTo(readTree(expectedPractice.getData()));
        assertThat(actualPractice.isPublic()).isEqualTo(expectedPractice.isPublic());
    }

    @Test(expectedExceptions = IllegalResourceAccessException.class)
    void addPracticeToUser_shouldThrowExceptionWhenPracticeIsPrivate() throws Exception {
        //given
        final var expectedUser = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expectedPractice = persistPractice(createPractice(false, practiceTag));

        //when
        practiceService.addPracticeToUser(expectedPractice.getId(), expectedUser);

        //then
        //exception should be thrown
    }

    @Test
    void removePracticeFromUser_shouldRemovePracticeFromUser() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var practice = persistPractice(createPractice(true, practiceTag));
        persistUserPractice(createUserPractice(user, practice));
        //when
        practiceService.removePracticeFromUser(practice.getId(), user);
        //then
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test
    void savePractice_shouldSavePracticeAndUserPracticeWhenPracticeIsPrivate() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var unsaved = createPractice(false, practiceTag);

        //when
        practiceService.savePractice(unsaved, user);

        //then
        final var practices = practiceRepository.findAll();
        final var userPractices = userPracticeRepository.findAll();
        assertThat(practices).hasSize(1);
        assertThat(userPractices).hasSize(1);

        final var practice = practices.get(0);
        final var userPractice = userPractices.get(0);
        assertThat(userPractice.getPractice().getId()).isEqualTo(practice.getId());
        assertThat(userPractice.getUser().getId()).isEqualTo(user.getId());
        assertThat(practice.getId()).isNotNull();
        assertThat(practice.getPracticeTag().getId()).isNotNull();
        assertThat(practice.getPracticeTag().getName()).isEqualTo(unsaved.getPracticeTag().getName());
        assertThat(practice.getName()).isEqualTo(unsaved.getName());
        assertThat(practice.getDescription()).isEqualTo(unsaved.getDescription());
        assertThat(readTree(practice.getData())).isEqualTo(readTree(unsaved.getData()));
        assertThat(practice.isPublic()).isEqualTo(unsaved.isPublic());
    }

    @Test
    void savePractice_shouldSavePublicPracticeWhenUserIsAdmin() throws Exception {
        //given
        final var user = getUser(USER_2_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var unsaved = createPractice(true, practiceTag);
        //when
        practiceService.savePractice(unsaved, user);
        //then
        final var practices = practiceRepository.findAll();
        final var userPractices = userPracticeRepository.findAll();
        assertThat(practices).hasSize(1);
        assertThat(userPractices).isEmpty();

        final var practice = practices.get(0);
        assertThat(practice.getId()).isNotNull();
        assertThat(practice.getPracticeTag().getId()).isNotNull();
        assertThat(practice.getPracticeTag().getName()).isEqualTo(unsaved.getPracticeTag().getName());
        assertThat(practice.getName()).isEqualTo(unsaved.getName());
        assertThat(practice.getDescription()).isEqualTo(unsaved.getDescription());
        assertThat(readTree(practice.getData())).isEqualTo(readTree(unsaved.getData()));
        assertThat(practice.isPublic()).isEqualTo(unsaved.isPublic());
    }

    @Test(expectedExceptions = IllegalResourceAccessException.class)
    void savePractice_shouldThrowExceptionWhenPracticeIsPublicAndUserIsNotAdmin() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var unsaved = createPractice(true, practiceTag);
        //when
        practiceService.savePractice(unsaved, user);
        //then
        //exception should be thrown
    }

    @Test
    void updatePractice_shouldUpdatePublicPractice() throws Exception {
        //given
        final var user = getUser(USER_2_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = persistPractice(createPractice(true, practiceTag));
        expected.setDescription(PRACTICE_DESCRIPTION_NEW);
        //when
        final var actual = practiceService.updatePractice(expected, user);
        //then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getPracticeTag().getId()).isNotNull();
        assertThat(actual.getPracticeTag().getName()).isEqualTo(expected.getPracticeTag().getName());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(readTree(actual.getData())).isEqualTo(readTree(expected.getData()));
        assertThat(actual.isPublic()).isEqualTo(expected.isPublic());
    }

    @Test
    void updatePractice_shouldUpdatePrivatePractice() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = persistPractice(createPractice(false, practiceTag));
        expected.setDescription(PRACTICE_DESCRIPTION_NEW);
        persistUserPractice(createUserPractice(user, expected));
        //when
        final var actual = practiceService.updatePractice(expected, user);
        //then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getPracticeTag().getId()).isNotNull();
        assertThat(actual.getPracticeTag().getName()).isEqualTo(expected.getPracticeTag().getName());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(readTree(actual.getData())).isEqualTo(readTree(expected.getData()));
        assertThat(actual.isPublic()).isEqualTo(expected.isPublic());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    void updatePractice_shouldThrowExceptionWhenPracticeIsNotPersisted() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = createPractice(true, practiceTag);

        //when
        final var actual = practiceService.updatePractice(expected, user);
        //then
        //exception should be thrown
    }

    @Test(expectedExceptions = IllegalResourceAccessException.class)
    void updatePractice_shouldThrowExceptionWhenPracticeIsPublicAndUserIsNotAdmin() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = persistPractice(createPractice(true, practiceTag));
        expected.setDescription(PRACTICE_DESCRIPTION_NEW);
        //when
        final var actual = practiceService.updatePractice(expected, user);
        //then
        //exception should be thrown
    }

    @Test(expectedExceptions = IllegalResourceAccessException.class)
    void updatePractice_shouldThrowExceptionWhenUserNotOwnsPrivatePractice() throws Exception {
        //given
        final var user1 = getUser(USER_1_NOT_ADMIN);
        final var user2 = getUser(USER_3_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var expected = persistPractice(createPractice(false, practiceTag));
        expected.setDescription(PRACTICE_DESCRIPTION_NEW);
        persistUserPractice(createUserPractice(user1, expected));
        //when
        final var actual = practiceService.updatePractice(expected, user2);
        //then
        //exception should be thrown
    }

    @Test
    void deletePractice_shouldDeletePublicPracticeAndUserPractices() throws Exception {
        //given
        final var userAdmin = getUser(USER_2_ADMIN);
        final var user1 = getUser(USER_1_NOT_ADMIN);
        final var user3 = getUser(USER_3_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var practice = persistPractice(createPractice(true, practiceTag));
        persistUserPractice(createUserPractice(user1, practice));
        persistUserPractice(createUserPractice(user3, practice));
        //when
        practiceService.deletePractice(practice.getId(), userAdmin);
        //then
        assertThat(practiceRepository.findAll()).isEmpty();
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test(expectedExceptions = IllegalResourceAccessException.class)
    void deletePractice_shouldThrowExceptionWhenPracticeIsPublicAndUserIsNotAdmin() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var practice = persistPractice(createPractice(true, practiceTag));
        //when
        practiceService.deletePractice(practice.getId(), user);
        //then
        //exception should be thrown
    }

    @Test
    void deletePractice_shouldDeletePrivatePracticeAndUserPractices() throws Exception {
        //given
        final var user = getUser(USER_1_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var practice = persistPractice(createPractice(false, practiceTag));
        persistUserPractice(createUserPractice(user, practice));
        //when
        practiceService.deletePractice(practice.getId(), user);
        //then
        assertThat(practiceRepository.findAll()).isEmpty();
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test(expectedExceptions = IllegalResourceAccessException.class)
    void deletePractice_shouldThrowExceptionWhenPracticeIsPrivateAndUserNotOwnsIt() throws Exception {
        //given
        final var user1 = getUser(USER_1_NOT_ADMIN);
        final var user2 = getUser(USER_3_NOT_ADMIN);
        final var practiceTag = persistTag(createTag());
        final var practice = persistPractice(createPractice(false, practiceTag));
        persistUserPractice(createUserPractice(user1, practice));
        //when
        practiceService.deletePractice(practice.getId(), user2);
        //then
        //exception should be thrown
    }
}
