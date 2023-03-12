package com.zor07.nofapp.repository.userprogress;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.userprogress.UserProgress;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import com.zor07.nofapp.test.UserProgresTestUtils;
import com.zor07.nofapp.test.UserTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProgressRepositoryTest extends AbstractApplicationTest {

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
    void findByUserIdTest() {
        final var user = userRepository.save(UserTestUtils.createUser());
        final var level = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));
        userProgressRepository.save(new UserProgress(user, task1));
        userProgressRepository.save(new UserProgress(user, task2));
        userProgressRepository.save(new UserProgress(user, task3));

        final var result = userProgressRepository.findByUserId(user.getId());

        assertThat(result).hasSize(3);
    }

    @Test
    void findCurrentUserProgressTest() {
        final var user = userRepository.save(UserTestUtils.createUser());
        final var level = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));
        userProgressRepository.save(new UserProgress(null, user, task1, Instant.now()));
        userProgressRepository.save(new UserProgress(null, user, task2, Instant.now()));
        userProgressRepository.save(new UserProgress(null, user, task3, null));

        final var result = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(result.getTask().getOrder()).isEqualTo(30);
    }

    @Test
    void testCrud() {
        final var user = userRepository.save(UserTestUtils.createUser());
        final var task = taskRepository.save(createTask());
        userProgressRepository.deleteAll();

        final var all = userProgressRepository.findAll();
        assertThat(all).isEmpty();

        final var userProgress = UserProgresTestUtils.getBlankEntity(user, task);

        final var id = userProgressRepository.save(userProgress).getId();
        final var inserted = userProgressRepository.findById(id).get();
        UserProgresTestUtils.checkEntity(userProgress, inserted, false);

        final var newTask = taskRepository.save(TaskTestUtils.updateEntity(createTask()));
        inserted.setTask(newTask);
        inserted.setCompletedDatetime(Instant.now());

        userProgressRepository.save(inserted);

        final var updated = userProgressRepository.findById(id).get();
        UserProgresTestUtils.checkUpdated(updated);
        assertThat(updated.getCompletedDatetime()).isNotNull();

        userProgressRepository.delete(updated);

        assertThat(userProgressRepository.findById(id)).isEmpty();
    }

    private Task createTask() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        return TaskTestUtils.getBlankEntity(level);
    }

    private Task createTaskWithOrder(final Integer order) {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = TaskTestUtils.getBlankEntity(level);
        task.setOrder(order);
        return task;
    }
}
