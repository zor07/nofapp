package com.zor07.nofapp.service.profile;

import com.zor07.nofapp.entity.profile.UserProgress;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.repository.profile.UserProgressRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
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
    private TaskContentRepository taskContentRepository;
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
        taskContentRepository.deleteAll();
        fileRepository.deleteAll();
        levelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void findByUserId_shouldReturnUserProgress() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var user = userRepository.save(UserTestUtils.createUser());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level,777));

        userProgressRepository.save(new UserProgress(user, task));

        final var result = userProgressService.getUserProgress(user);

        assertThat(result.getCurrentTask().getOrder()).isEqualTo(777);
    }

    @Test
    void initUserProgressTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var user = userRepository.save(UserTestUtils.createUser());
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level,20));
        assertThat(userProgressService.getUserProgress(user)).isNull();

        userProgressService.initUserProgress(user);

        final var result = userProgressService.getUserProgress(user);
        assertThat(result.getCurrentTask().getOrder()).isEqualTo(task1.getOrder());
    }

    @Test
    void setNextTaskInUserProgress_shouldSetNextTaskOfSameLevel() {
        //given
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,20));
        userProgressRepository.save(new UserProgress(user, task1));

        //when
        final var result = userProgressService.setNextTaskInUserProgress(user);

        //then
        assertThat(result.getCurrentTask().getLevel().getOrder()).isEqualTo(task2.getLevel().getOrder());
        assertThat(result.getCurrentTask().getOrder()).isEqualTo(task2.getOrder());

        final var currentProgress = userProgressService.getUserProgress(user);
        assertThat(currentProgress.getCurrentTask().getLevel().getOrder()).isEqualTo(task2.getLevel().getOrder());
        assertThat(currentProgress.getCurrentTask().getOrder()).isEqualTo(task2.getOrder());
    }

    @Test
    void setNextTaskInUserProgress_shouldSetNextTaskOfNextLevel() {
        //given
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,20));
        userProgressRepository.save(new UserProgress(user, task2));

        //when
        final var result = userProgressService.setNextTaskInUserProgress(user);

        //then
        assertThat(result.getCurrentTask().getLevel().getOrder()).isEqualTo(task3.getLevel().getOrder());
        assertThat(result.getCurrentTask().getOrder()).isEqualTo(task3.getOrder());

        final var currentProgress = userProgressService.getUserProgress(user);
        assertThat(currentProgress.getCurrentTask().getLevel().getOrder()).isEqualTo(task3.getLevel().getOrder());
        assertThat(currentProgress.getCurrentTask().getOrder()).isEqualTo(task3.getOrder());
    }

    @Test
    void setNextTaskInUserProgress_shouldreturnNull() {
        //given
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,20));
        userProgressRepository.save(new UserProgress(user, task4));

        //when
        final var result = userProgressService.setNextTaskInUserProgress(user);

        //then
        assertThat(result).isNull();

        final var currentProgress = userProgressService.getUserProgress(user);
        assertThat(currentProgress.getCurrentTask().getLevel().getOrder()).isEqualTo(task4.getLevel().getOrder());
        assertThat(currentProgress.getCurrentTask().getOrder()).isEqualTo(task4.getOrder());
    }
}
