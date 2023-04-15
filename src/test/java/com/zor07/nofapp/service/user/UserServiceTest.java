package com.zor07.nofapp.service.user;

import com.zor07.nofapp.repository.profile.ProfileRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_ROLE;
import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_USERNAME;
import static com.zor07.nofapp.test.UserTestUtils.createRole;
import static com.zor07.nofapp.test.UserTestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class UserServiceTest extends AbstractApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    protected UserService userService;

    private void clearDb() {
        profileRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @BeforeMethod
    void setup() {
        clearDb();
    }

    @AfterClass
    void teardown() {
        clearDb();
    }

    @Test
    void createNewUserTest() {
        final var user = createUser();
        userService.createNewUser(user);
        final var savedUser = userRepository.findByUsername(DEFAULT_USERNAME);
        final var profile = profileRepository.findAll().get(0);
        assertThat(savedUser.getName()).isEqualTo(DEFAULT_USERNAME);
        assertThat(profile.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(profile.getTimerStart()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
        assertThat(profile.getAvatar()).isNull();
        assertThat(profile.getId()).isNotNull();
    }

    @Test
    void saveUserTest() {
        userService.saveUser(createUser());
        final var user = userRepository.findByUsername(DEFAULT_USERNAME);
        assertThat(user.getName()).isEqualTo(DEFAULT_USERNAME);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    void saveUserTest_failsWhenSavingUsersWithSameUsername() {
        userService.saveUser(createUser());
        userService.saveUser(createUser());
    }

    @Test
    void saveRoleTest() {
        userService.saveRole(createRole());
        final var role = roleRepository.findAll().get(0);
        assertThat(role.getName()).isEqualTo(DEFAULT_ROLE);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    void saveRoleTest_failsWhenSavingRolesWithSameName() {
        userService.saveRole(createRole());
        userService.saveRole(createRole());
    }

    @Test
    void addRoleToUserTest() {
        userService.saveUser(createUser());
        userService.saveRole(createRole());
        userService.addRoleToUser(DEFAULT_USERNAME, DEFAULT_ROLE);
        final var user = userRepository.findByUsername(DEFAULT_USERNAME);
        final var roles = new ArrayList<>(user.getRoles());
        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getName()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    void getUserTest() {
        saveUserTest();
    }

    @Test
    void getUsersTest() {
        userService.saveUser(createUser("user1"));
        userService.saveUser(createUser("user2"));
        userService.saveUser(createUser("user3"));
        assertThat(userService.getUsers()).hasSize(3);
    }

}
