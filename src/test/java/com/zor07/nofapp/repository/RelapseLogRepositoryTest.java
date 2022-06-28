package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.RelapseLog;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class RelapseLogRepositoryTest extends AbstractApplicationTest {

  private static final String USERNAME = "user";
  private static final String PASSWORD = "pass";

  private static final Instant START_1 = Instant.parse("2022-05-01T15:26:00Z");
  private static final Instant STOP_1 = Instant.parse("2022-05-01T15:27:00Z");

  private static final Instant START_2 = Instant.parse("2021-05-01T15:26:00Z");
  private static final Instant STOP_2 = Instant.parse("2021-05-01T15:27:00Z");

  @Autowired
  private RelapseLogRepository relapseLogRepository;

  @Autowired
  private UserRepository userRepository;


  @BeforeMethod
  @AfterClass
  void clearDb() {
    relapseLogRepository.deleteAll();
    userRepository.deleteAll();
  }

  private User persistUser() {
    final var user = new User();
    user.setName(USERNAME);
    user.setUsername(USERNAME);
    user.setPassword(PASSWORD);
    return userRepository.save(user);
  }

  @Test
  void testCrud() {
    final var user = persistUser();

    final var all = relapseLogRepository.findAll();
    assertThat(all).isEmpty();

    // create-read
    final var log = new RelapseLog();
    log.setUser(user);
    log.setStart(START_1);
    log.setStop(STOP_1);

    final var id = relapseLogRepository.save(log).getId();
    final var inserted = relapseLogRepository.findById(id).get();
    assertThat(inserted).isNotNull();
    assertThat(inserted.getStart()).isEqualTo(START_1);
    assertThat(inserted.getStop()).isEqualTo(STOP_1);
    assertThat(inserted.getUser().getName()).isEqualTo(USERNAME);
    assertThat(inserted.getUser().getUsername()).isEqualTo(USERNAME);
    assertThat(inserted.getUser().getPassword()).isEqualTo(PASSWORD);

    //update
    inserted.setStart(START_2);
    inserted.setStop(STOP_2);
    relapseLogRepository.save(inserted);

    final var updated = relapseLogRepository.findById(id).get();
    assertThat(updated).isNotNull();
    assertThat(updated.getStart()).isEqualTo(START_2);
    assertThat(updated.getStop()).isEqualTo(STOP_2);
    assertThat(updated.getUser().getName()).isEqualTo(USERNAME);
    assertThat(updated.getUser().getUsername()).isEqualTo(USERNAME);
    assertThat(updated.getUser().getPassword()).isEqualTo(PASSWORD);

    //delete
    relapseLogRepository.delete(updated);
    assertThat(relapseLogRepository.findById(id)).isEmpty();
  }

}
