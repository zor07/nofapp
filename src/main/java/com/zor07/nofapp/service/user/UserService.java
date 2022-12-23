package com.zor07.nofapp.service.user;

import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.entity.user.User;

import java.security.Principal;
import java.util.List;

public interface UserService {

  default User getUser(final Principal principal) {
    final var username = principal.getName();
    return getUser(username);
  }

  User saveUser(User user);
  Role saveRole(Role role);
  void addRoleToUser(String username, String roleName);
  User getUser(String username);
  List<User> getUsers();

}
