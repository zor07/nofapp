package com.zor07.nofapp.timer;

import java.time.Instant;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.user.Role;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;

public class TimerRepositoryTest extends AbstractApplicationTest {

  @Autowired
  private TimerRepository repository;
  @Autowired
  private UserService userService;

  @BeforeClass
  void setup() {
    userService.saveUser(new User(null, "user", "user", "pass", new ArrayList<>()));
    userService.saveRole(new Role(null, "role"));
    userService.addRoleToUser("user", "role");
  }

  @Test
  void testCrud() {

    repository.deleteAll();
    final var all = repository.findAll();
    assertThat(all).isEmpty();

    final var timer = new Timer();
    timer.setUser(userService.getUser("user"));
    timer.setStart(Instant.now());
    timer.setDescription("Test description");

    final var id = repository.save(timer).getId();
    final var inserted = repository.findById(id).get();
    assertThat(inserted).isNotNull();
    assertThat(inserted.getStop()).isNull();

    inserted.setStop(Instant.now());
    repository.save(inserted);

    final var updated = repository.findById(id).get();
    assertThat(updated.getStop()).isNotNull();

    repository.delete(updated);

    assertThat(repository.findById(id)).isEmpty();
  }

  @Test
  void findAllByUserIdTest() {
    repository.deleteAll();
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
    repository.save(timer1);
    repository.save(timer2);
    final var allByUserId = repository.findAllByUserId(user.getId());
    assertThat(allByUserId).hasSize(2);
  }
}
