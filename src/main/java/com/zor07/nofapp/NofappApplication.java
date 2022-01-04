package com.zor07.nofapp;

import java.util.ArrayList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.zor07.nofapp.user.Role;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

@SpringBootApplication
public class NofappApplication {

  public static void main(String[] args) {
    SpringApplication.run(NofappApplication.class, args);
  }

  @Bean
  CommandLineRunner run(UserService userService) {
    return  args -> {
      userService.saveRole(new Role(null, "ROLE_ADMIN"));
      userService.saveRole(new Role(null, "ROLE_USER"));

      userService.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>()));
      userService.saveUser(new User(null, "Mister X", "mstrx", "1234", new ArrayList<>()));
      userService.saveUser(new User(null, "Mister y", "mstry", "1234", new ArrayList<>()));

      userService.addRoleToUser("will", "ROLE_ADMIN");
      userService.addRoleToUser("will", "ROLE_USER");
      userService.addRoleToUser("mstrx", "ROLE_USER");
      userService.addRoleToUser("mstry", "ROLE_USER");
    };
  }

}
