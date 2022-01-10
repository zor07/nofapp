package com.zor07.nofapp.test;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.user.Role;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

public class AbstractUserRelatedApplicationTest extends AbstractApplicationTest {

  protected static Role createRole() {
    return createRole("ROLE_USER");
  }

  protected static User createUser() {
    return createUser("user");
  }

  protected static Role createRole(String name) {
    return new Role(null, name);
  }

  protected static User createUser(final String name) {
    return new User(null, name, name, "pass", new ArrayList<>());
  }

  @Autowired
  protected UserService userService;

  protected void createDefaultUser() {
    userService.saveUser(createUser());
    userService.saveRole(createRole());
    userService.addRoleToUser("user", "ROLE_USER");
  }
}
