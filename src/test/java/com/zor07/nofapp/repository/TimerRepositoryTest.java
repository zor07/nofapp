package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.entity.timer.Timer;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class TimerRepositoryTest extends AbstractApplicationTest {

  @Autowired
  private TimerRepository timerRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserRepository userRepository;

  private void clearDb() {
    timerRepository.deleteAll();
    userRepository.deleteAll();
    roleRepository.deleteAll();
  }

  @BeforeClass
  void setup() {
    clearDb();
    userService.saveUser(new User(null, "user", "user", "pass", new ArrayList<>()));
    userService.saveRole(new Role(null, "role"));
    userService.addRoleToUser("user", "role");
  }

  @Test
  void testCrud() {

    timerRepository.deleteAll();
    final var all = timerRepository.findAll();
    assertThat(all).isEmpty();

    final var timer = new Timer();
    timer.setUser(userService.getUser("user"));
    timer.setStart(Instant.now());
    timer.setDescription("Test description");

    final var id = timerRepository.save(timer).getId();
    final var inserted = timerRepository.findById(id).get();
    assertThat(inserted).isNotNull();
    assertThat(inserted.getStop()).isNull();

    inserted.setStop(Instant.now());
    timerRepository.save(inserted);

    final var updated = timerRepository.findById(id).get();
    assertThat(updated.getStop()).isNotNull();

    timerRepository.delete(updated);

    assertThat(timerRepository.findById(id)).isEmpty();
  }

  @Test
  void findAllByUserIdTest() {
    timerRepository.deleteAll();
    final var user = userService.getUser("user");
    final var timer1 = new Timer(
        null,
        user,
        Instant.now(),
        null,
        "Test description"
    );
    final var timer2 = new Timer(
        null,
        user,
        Instant.now(),
        null,
        "Test description"
    );
    timerRepository.save(timer1);
    timerRepository.save(timer2);
    final var allByUserId = timerRepository.findAllByUserId(user.getId());
    assertThat(allByUserId).hasSize(2);
  }
}
