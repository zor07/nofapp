package com.zor07.nofapp.timer;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.zor07.nofapp.test.AbstractUserRelatedApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TimerRepositoryTest extends AbstractUserRelatedApplicationTest {

  @Autowired
  private TimerRepository repository;

  @BeforeClass
  void setup() {
    createDefaultUser();
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
    final var timer = new Timer(
        null,
        user,
        Instant.now(),
        null,
        "Test description"
    );
    repository.save(timer);
    repository.save(timer);
    final var allByUserId = repository.findAllByUserId(user.getId());
    assertThat(allByUserId).hasSize(2);
  }



}
