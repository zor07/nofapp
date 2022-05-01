package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.Diary;
import com.zor07.nofapp.entity.Role;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
@Deprecated //soon will be replaced with Note
public class DiaryRepositoryTest extends AbstractApplicationTest {

  @Autowired
  private DiaryRepository diaryRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserRepository userRepository;

  private void clearDb() {
    diaryRepository.deleteAll();
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

    diaryRepository.deleteAll();
    final var all = diaryRepository.findAll();
    assertThat(all).isEmpty();

    final var diary = new Diary();
    diary.setUser(userService.getUser("user"));
    diary.setTitle("title");
    diary.setData("data");

    final var id = diaryRepository.save(diary).getId();
    final var inserted = diaryRepository.findById(id).get();
    assertThat(inserted).isNotNull();
    assertThat(inserted.getData()).isEqualTo("data");

    inserted.setData("new data");
    diaryRepository.save(inserted);

    final var updated = diaryRepository.findById(id).get();
    assertThat(updated.getData()).isEqualTo("new data");

    diaryRepository.delete(updated);

    assertThat(diaryRepository.findById(id)).isEmpty();
  }

  @Test
  void findAllByUserIdTest() {
    diaryRepository.deleteAll();
    final var user = userService.getUser("user");
    final var diary1 = new Diary(
        null,
        user,
        "title",
        "data"
    );
    final var diary2 = new Diary(
        null,
        user,
        "title",
        "data"
    );
    diaryRepository.save(diary1);
    diaryRepository.save(diary2);
    final var allByUserId = diaryRepository.findAllByUserId(user.getId());
    assertThat(allByUserId).hasSize(2);
    assertThat(allByUserId.get(0).getId()).isNotNull();
    assertThat(allByUserId.get(0).getTitle()).isEqualTo("title");
    assertThat(allByUserId.get(1).getId()).isNotNull();
    assertThat(allByUserId.get(1).getTitle()).isEqualTo("title");
  }

  @Test
  void findByIdAndUserIdTest() {
    diaryRepository.deleteAll();
    final var user = userService.getUser("user");
    final var diary = new Diary(
        null,
        user,
        "title",
        "data"
    );

    final var id = diaryRepository.save(diary).getId();
    final var byIdAndUserId = diaryRepository.findByIdAndUserId(id, user.getId());
    assertThat(byIdAndUserId.getTitle()).isEqualTo("title");
    assertThat(byIdAndUserId.getData()).isEqualTo("data");
  }
}
