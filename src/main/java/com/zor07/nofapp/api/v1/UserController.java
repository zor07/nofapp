package com.zor07.nofapp.api.v1;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.zor07.nofapp.api.v1.dto.RoleToUserDto;
import com.zor07.nofapp.entity.Role;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user")
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }

  @PostMapping("/user")
  public ResponseEntity<User> saveUser(final @RequestBody User user) {
    final var uri = URI.create(ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/v1/user")
        .toUriString());
    return ResponseEntity.created(uri).body(userService.saveUser(user));
  }

  @PostMapping("/role")
  public ResponseEntity<Role> saveRole(final @RequestBody Role role) {
    final var uri = URI.create(ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/v1/role")
        .toUriString());
    return ResponseEntity.created(uri).body(userService.saveRole(role));
  }

  @PostMapping("/role/addToUser")
  public ResponseEntity<?> addRoleToUser(final @RequestBody RoleToUserDto dto) {
    userService.addRoleToUser(dto.username(), dto.role());
    return ResponseEntity.ok().build();
  }
}
