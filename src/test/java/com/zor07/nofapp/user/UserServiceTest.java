package com.zor07.nofapp.user;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.zor07.nofapp.spring.AbstractApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest extends AbstractApplicationTest {


  private static final String DEFAULT_PASSWORD = "pass";
  private static final String DEFAULT_ROLE = "role";

  protected static Role createRole() {
    return new Role(null, DEFAULT_ROLE);
  }

  protected static User createUser(final String name) {
    return new User(null, name, name, DEFAULT_PASSWORD, new ArrayList<>());
  }


  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  protected UserService userService;

  @BeforeMethod
  void clearDb () {
    userRepository.deleteAll();
    roleRepository.deleteAll();
  }

  @Test
  void saveUserTest() {
    userService.saveUser(createUser("user"));
    final var user = userRepository.findByUsername("user");
    assertThat(user.getName()).isEqualTo("user");
  }

  @Test(expectedExceptions = DataIntegrityViolationException.class)
  void saveUserTest_failsWhenSavingUsersWithSameUsername() {
    userService.saveUser(createUser("user"));
    userService.saveUser(createUser("user"));
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
    userService.saveUser(createUser("user"));
    userService.saveRole(createRole());
    userService.addRoleToUser("user", "role");
    final var user = userRepository.findByUsername("user");
    final var roles = new ArrayList<>(user.getRoles());
    assertThat(roles).hasSize(1);
    assertThat(roles.get(0).getName()).isEqualTo("role");
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
