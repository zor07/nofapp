package com.zor07.nofapp.service.user.impl;

import com.zor07.nofapp.entity.profile.Profile;
import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.repository.profile.ProfileRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl  implements UserService, UserDetailsService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProfileRepository profileRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         ProfileRepository profileRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.profileRepository = profileRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final var user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    final var authorities = new ArrayList<SimpleGrantedAuthority>();
    user.getRoles().forEach(role -> {
      authorities.add(new SimpleGrantedAuthority(role.getName()));
    });
    return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
  }

  @Override
  public User createNewUser(User user) {
    final var savedUser = saveUser(user);
    final var profile = new Profile(
            null,
            savedUser,
            Instant.now(),
            null
    );
    profileRepository.save(profile);
    return savedUser;
  }

  @Override
  public User saveUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public Role saveRole(Role role) {
    return roleRepository.save(role);
  }

  @Override
  public void addRoleToUser(String username, String roleName) {
    final var user = userRepository.findByUsername(username);
    final var role = roleRepository.findByName(roleName);
    user.getRoles().add(role);
  }

  @Override
  public User getUser(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public List<User> getUsers() {
    return userRepository.findAll();
  }
}
