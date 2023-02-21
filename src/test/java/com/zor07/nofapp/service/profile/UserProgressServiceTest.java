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
import com.zor07.nofapp.test.TaskContentTestUtils;
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
        taskContentRepository.deleteAll();
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
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level,20));
        assertThat(userProgressRepository.findByUserId(user.getId())).isNull();

        userProgressService.initUserProgress(user);

        final var result = userProgressRepository.findByUserId(user.getId());
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
        final var result = userProgressService.updateUserProgressToNextTask(user);

        //then
        assertThat(result.getCurrentTask().getLevel().getOrder()).isEqualTo(task2.getLevel().getOrder());
        assertThat(result.getCurrentTask().getOrder()).isEqualTo(task2.getOrder());

        final var currentProgress = userProgressRepository.findByUserId(user.getId());
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
        final var result = userProgressService.updateUserProgressToNextTask(user);

        //then
        assertThat(result.getCurrentTask().getLevel().getOrder()).isEqualTo(task3.getLevel().getOrder());
        assertThat(result.getCurrentTask().getOrder()).isEqualTo(task3.getOrder());

        final var currentProgress = userProgressRepository.findByUserId(user.getId());
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
        final var result = userProgressService.updateUserProgressToNextTask(user);

        //then
        assertThat(result).isNull();

        final var currentProgress = userProgressRepository.findByUserId(user.getId());
        assertThat(currentProgress.getCurrentTask().getLevel().getOrder()).isEqualTo(task4.getLevel().getOrder());
        assertThat(currentProgress.getCurrentTask().getOrder()).isEqualTo(task4.getOrder());
    }

    @Test
    void getCurrentTaskContentForUserTest() {
        //given
        final var title = "my test title";
        final var user = userRepository.save(UserTestUtils.createUser());

        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(level));
        final var taskContent1 = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, null));
        final var taskContent2 = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, null));
        taskContent1.setTitle(title);
        taskContent2.setTitle(title);
        taskContentRepository.save(taskContent1);
        taskContentRepository.save(taskContent2);
        userProgressRepository.save(new UserProgress(user, task));

        //when
        final var result = userProgressService.getCurrentTaskContentForUser(user);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch( it -> it.getTitle().equals(title));
    }

}
