package com.zor07.nofapp.service.userprogress;

import com.zor07.nofapp.entity.userprogress.UserProgress;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.repository.userprogress.UserProgressRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import com.zor07.nofapp.test.UserTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProgressServiceTest extends AbstractApplicationTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private UserProgressService userProgressService;


    @BeforeMethod
    @AfterClass
    void clearDb() {
        userProgressRepository.deleteAll();
        taskRepository.deleteAll();
        fileRepository.deleteAll();
        levelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void initUserProgressTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var user = userRepository.save(UserTestUtils.createUser());
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        assertThat(userProgressRepository.findByUserId(user.getId())).isEmpty();

        userProgressService.initUserProgress(user);

        final var result = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(result.getTask().getOrder()).isEqualTo(task1.getOrder());
        assertThat(userProgressRepository.findByUserId(user.getId())).hasSize(1);
    }

    @Test
    void setNextTaskInUserProgress_shouldSetNextTaskOfSameLevel() {
        //given
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2, 10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2, 20));
        userProgressRepository.save(new UserProgress(user, task1));

        //when
        final var result = userProgressService.addNextTaskToUserProgress(user);

        //then
        final var userProgressList = userProgressRepository.findByUserId(user.getId());
        assertThat(userProgressList.stream().filter(it -> it.getTask().getOrder() == 10 && it.getCompletedDatetime() != null).count()).isEqualTo(1);
        assertThat(userProgressList.stream().filter(it -> it.getTask().getOrder() == 20 && it.getCompletedDatetime() == null).count()).isEqualTo(1);

        assertThat(result.getTask().getLevel().getOrder()).isEqualTo(task2.getLevel().getOrder());
        assertThat(result.getTask().getOrder()).isEqualTo(task2.getOrder());

        final var currentProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(currentProgress.getTask().getLevel().getOrder()).isEqualTo(task2.getLevel().getOrder());
        assertThat(currentProgress.getTask().getOrder()).isEqualTo(task2.getOrder());
    }

    @Test
    void setNextTaskInUserProgress_shouldSetNextTaskOfNextLevel() {
        //given
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2, 10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2, 20));
        userProgressRepository.save(new UserProgress(user, task2));

        //when
        final var result = userProgressService.addNextTaskToUserProgress(user);

        //then
        final var userProgressList = userProgressRepository.findByUserId(user.getId());
        assertThat(userProgressList.stream().filter(it -> it.getTask().getOrder() == 20 && it.getCompletedDatetime() != null).count()).isEqualTo(1);
        assertThat(userProgressList.stream().filter(it -> it.getTask().getOrder() == 10 && it.getCompletedDatetime() == null).count()).isEqualTo(1);

        assertThat(result.getTask().getLevel().getOrder()).isEqualTo(task3.getLevel().getOrder());
        assertThat(result.getTask().getOrder()).isEqualTo(task3.getOrder());

        final var currentProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(currentProgress.getTask().getLevel().getOrder()).isEqualTo(task3.getLevel().getOrder());
        assertThat(currentProgress.getTask().getOrder()).isEqualTo(task3.getOrder());
    }

    @Test
    void setNextTaskInUserProgress_shouldreturnNull() {
        //given
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2, 10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2, 20));
        userProgressRepository.save(new UserProgress(user, task4));

        //when
        final var result = userProgressService.addNextTaskToUserProgress(user);

        //then
        final var userProgressList = userProgressRepository.findByUserId(user.getId());
        assertThat(userProgressList.stream().filter(it -> it.getTask().getOrder() == 20 && it.getCompletedDatetime() != null).count()).isEqualTo(1);
        assertThat(result).isNull();

        final var currentProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(currentProgress.getTask().getLevel().getOrder()).isEqualTo(task4.getLevel().getOrder());
        assertThat(currentProgress.getTask().getOrder()).isEqualTo(task4.getOrder());
    }

    @Test
    void getCurrentTaskForUserTest() {
        //given
        final var title = "my test title";
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 777));
        userProgressRepository.save(new UserProgress(user, task));

        //when
        final var result = userProgressService.getCurrentTaskForUser(user);

        assertThat(result.getOrder()).isEqualTo(777);
    }

    @Test
    void getUserProgressTest() {
        final var user = userRepository.save(UserTestUtils.createUser());
        final var level = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));
        userProgressRepository.save(new UserProgress(user, task1));
        userProgressRepository.save(new UserProgress(user, task2));
        userProgressRepository.save(new UserProgress(user, task3));

        final var result = userProgressService.getUserProgress(user);

        assertThat(result).hasSize(3);
    }

    @Test
    void getUserProgressShouldInitUserProgress() {
        final var user = userRepository.save(UserTestUtils.createUser());
        final var level = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));

        final var result = userProgressService.getUserProgress(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTask().getOrder()).isEqualTo(10);
    }

}
