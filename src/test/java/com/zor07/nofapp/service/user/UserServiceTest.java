package com.zor07.nofapp.service.user;

import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_ROLE;
import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_USERNAME;
import static com.zor07.nofapp.test.UserTestUtils.createRole;
import static com.zor07.nofapp.test.UserTestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest extends AbstractApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    protected UserService userService;

    private void clearDb() {
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
        final var user = roleRepository.findAll().get(0);
        assertThat(user.getName()).isEqualTo(DEFAULT_ROLE);
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
